package cucumber.contrib.formatter.pdf;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import cucumber.contrib.formatter.BricABrac;
import cucumber.contrib.formatter.FormatterException;
import cucumber.contrib.formatter.model.*;
import gherkin.formatter.model.DataTableRow;
import gherkin.formatter.model.Row;
import gherkin.formatter.model.Tag;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.io.Resources.asByteSource;
import static cucumber.contrib.formatter.pdf.Configuration.extendTableToWidth;

public class PdfEmitter {

    private Logger log = LoggerFactory.getLogger(PdfEmitter.class);

    private Document document;
    private Configuration configuration;
    private RootStatistics statistics;
    private boolean firstFeature = true;
    //
    private File fileTmp;
    private File fileDst;
    private PdfWriter pdfWriter;
    private FileOutputStream fileOutputStream;
    //
    private HeaderFooter headerFooter;
    private TableOfContents tableOfContents;
    private PageNumber pageNumber;

    public PdfEmitter(Configuration configuration) {
        this.configuration = configuration;
        initStatistics();
    }

    private void initStatistics() {
        this.statistics = new RootStatistics(new RootStatistics.Filter() {
            @Override
            public boolean isManual(ScenarioWrapper scenarioWrapper) {
                return scenarioWrapper.hasTag(configuration.manualTag());
            }
        });
    }

    public void init(File fileDst) throws FileNotFoundException, DocumentException {
        this.fileDst = fileDst;
        this.fileTmp = new File(fileDst.getParentFile(), fileDst.getName() + ".tmp");
        this.document = configuration.createDocument();
        this.fileOutputStream = new FileOutputStream(fileTmp);
        this.pageNumber = configuration.getPageNumber();
        this.tableOfContents = configuration.getTableOfContent();
        this.headerFooter = configuration.createHeaderFooter();

        //
        pdfWriter = PdfWriter.getInstance(document, fileOutputStream);
        pdfWriter.setBoxSize("art", configuration.getDocumentArtBox());
        pdfWriter.setPageEvent(headerFooter);
        pdfWriter.setPageEvent(tableOfContents);
        document.open();
    }

    public Document getDocument() {
        return document;
    }

    private void writeInitialData() {
        try {
            pageNumber.startContent();
            configuration.writeMetaInformations(document);
            configuration.writeFirstPages(document);
            configuration.writePreambule(document);
        } catch (DocumentException e) {
            log.warn("Error while writing initial data", e);
        }
    }

    public void emit(FeatureWrapper feature) {
        if (firstFeature) {
            firstFeature = false;
            writeInitialData();
        }

        // statistics
        statistics.consolidate(feature);

        //
        // Title
        Chapter featureChap = configuration.createTitledChapter(feature.getName());

        // Uri
        if (configuration.shouldDisplayUri()) {
            Paragraph uri = new Paragraph("Uri: " + feature.getUri(), configuration.defaultMetaFont());
            featureChap.add(uri);
        }

        // Description
        String description = feature.getDescription();
        if (BricABrac.isNotBlank(description)) {
            Margin descriptionMargin = configuration.getDescriptionMargin();
            Paragraph paragraph = new Paragraph("", configuration.defaultFont());
            paragraph.setSpacingBefore(descriptionMargin.marginTop);
            paragraph.setSpacingAfter(descriptionMargin.marginBottom);
            paragraph.setIndentationLeft(descriptionMargin.marginLeft);
            configuration.appendMarkdownContent(paragraph, description);
            featureChap.add(paragraph);
        }


        // Scenario
        for (ScenarioWrapper scenario : feature.getScenarios()) {
            try {
                emitScenario(featureChap, scenario);
            } catch (DocumentException e) {
                log.warn("Failed to emit scenario '{}'", scenario.getName(), e);
            }
        }

        //
        try {
            document.add(featureChap);
        } catch (DocumentException e) {
            log.warn("Failed to emit feature '{}'", feature.getName(), e);
        }
    }

    private void emitScenario(Chapter featureChap, ScenarioWrapper scenario) throws DocumentException {
        Paragraph scenarioTitle = new Paragraph(scenario.getName(), configuration.scenarioTitleFont());
        Section section = featureChap.addSection(scenarioTitle);

        Margin margin = configuration.getScenarioMargin();
        section.setIndentationLeft(margin.marginLeft);

        if (configuration.shouldDisplayTags()) {
            emitScenarioTags(scenario, section);
        }
        emitScenarioDescription(scenario, section);

        Paragraph steps = new Paragraph("");
        steps.setKeepTogether(configuration.shouldKeepScenarioUnbreakable());
        for (StepWrapper step : scenario.getSteps()) {
            emitStep(scenario, steps, step);
        }
        steps.setSpacingBefore(margin.marginTop);
        steps.setSpacingAfter(margin.marginBottom);
        section.add(steps);
    }

    private void emitScenarioDescription(ScenarioWrapper scenario, Section section) throws DocumentException {
        String description = scenario.getDescription();
        if (BricABrac.isBlank(description))
            return;

        List<Element> elements = configuration.markdownContent(description);
        for (Element element : elements) {
            if (element instanceof PdfPTable)
                extendTableToWidth((PdfPTable) element, document.right() - document.left() - 25.0f);
        }

        Paragraph paragraph = new Paragraph();
        paragraph.addAll(elements);

        Margin margin = configuration.getScenarioMargin();
        paragraph.setIndentationLeft(margin.marginLeft);
        paragraph.setSpacingBefore(margin.marginTop);
        paragraph.setSpacingAfter(margin.marginBottom);


        section.add(paragraph);
    }

    private void emitScenarioTags(ScenarioWrapper scenario, Section section) {
        List<Tag> tagList = scenario.getTags();
        if (!tagList.isEmpty()) {
            Paragraph tags = new Paragraph("Tags: ", configuration.defaultMetaFont());
            boolean first = true;
            for (Tag tag : tagList) {
                String text = tag.getName();
                if (first) {
                    first = false;
                } else {
                    text = ", " + tag.getName();
                }
                tags.add(new Chunk(text, configuration.tagsFont()));
            }
            section.add(tags);
        }
    }

    private float documentContentWidth() {
        return document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
    }

    private void emitStep(ScenarioWrapper scenario, Paragraph steps, StepWrapper step) {
        boolean isManualStep = scenario.hasTag(configuration.manualTag());


        float imageWidth = 16.5f;
        float imageHeight = 10.5f;

        PdfPTable stepAsTable = new PdfPTable(2);
        stepAsTable.setWidthPercentage(100);
        stepAsTable.setKeepTogether(true);
        try {
            float imageCellWidth = imageWidth + 5f;
            stepAsTable.setTotalWidth(new float[]{imageCellWidth, documentContentWidth() - imageCellWidth});
        } catch (DocumentException e) {
            log.warn("Step table issue", e);
        }

        Image stepStatus = isManualStep ? getManualStepImageOrNull() : getStepStatusAsImageOrNull(step);
        PdfPCell cell;
        if (stepStatus != null) {
            stepStatus.scaleAbsolute(imageWidth, imageHeight);
            cell = new PdfPCell(stepStatus);
            cell.setPaddingTop(2.0f);
        } else {
            cell = new PdfPCell(new Phrase(""));
        }

        cell.setBorder(Rectangle.NO_BORDER);
        stepAsTable.addCell(cell);

        Paragraph stepParagraph = new Paragraph();
        stepParagraph.add(new Chunk(step.getKeyword(), configuration.stepKeywordFont()));
        stepParagraph.add(new Chunk(step.getName(), configuration.stepDefaultFont()));

        cell = new PdfPCell(stepParagraph);
        cell.setBorder(Rectangle.NO_BORDER);
        stepAsTable.addCell(cell);

        if (step.hasTable()) {
            // table added on stepParagraph is not visible...
            // thus it becomes a direct nested table
            PdfPTable table = createStepDataTable(step.getTableRows());
            stepAsTable.addCell(noBorder(new PdfPCell(new Phrase(""))));
            stepAsTable.addCell(noBorder(new PdfPCell(table)));
        }

        steps.add(stepAsTable);

        for (Embedding embedding : step.getEmbeddings()) {
            steps.add(new Paragraph(embedding.getDataAsUTF8()));
        }
    }

    private static PdfPCell noBorder(PdfPCell pdfPCell) {
        pdfPCell.setBorder(Rectangle.NO_BORDER);
        return pdfPCell;
    }

    private static PdfPCell sideBorder(PdfPCell pdfPCell) {
        return border(pdfPCell, Rectangle.LEFT + Rectangle.RIGHT);
    }

    private static PdfPCell border(PdfPCell pdfPCell, int side) {
        pdfPCell.setBorder(side);
        return pdfPCell;
    }

    private Image getStepStatusAsImageOrNull(StepWrapper step) {
        return getResourceImageOrNull(getStepStatusResourceName(step));
    }

    private Image getManualStepImageOrNull() {
        return getResourceImageOrNull("images/manual.PNG");
    }

    private Image getResourceImageOrNull(String resourceName) {
        try {
            ByteSource byteSource = asByteSource(getClass().getResource(resourceName));
            byte[] bytes = byteSource.read();
            return Image.getInstance(bytes);
        } catch (BadElementException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getStepStatusResourceName(StepWrapper step) {
        if (step.isSuccess()) {
            return "images/ok-icon.PNG";
        } else if (step.isPending()) {
            return "images/pending-icon.PNG";
        } else if (step.isFailure()) {
            return "images/ko-icon.PNG";
        } else if (step.isSkipped()) {
            return "images/skipped-icon.PNG";
        } else {
            return "images/unknown-icon.PNG";
        }
    }

    private Font getTableFont(boolean firstRow) {
        if (firstRow) {
            return configuration.stepDataTableHeaderFont();
        } else {
            return configuration.stepDataTableContentFont();
        }
    }

    private PdfPTable createStepDataTable(List<DataTableRow> tableRows) {
        PdfPTable table = createEmptyStepDataTable(tableRows);

        for (int j = 0; j < tableRows.size(); j++) {
            boolean firstRow = (j == 0);
            Row row = tableRows.get(j);

            List<String> cells = row.getCells();
            Font font = getTableFont(firstRow);

            for (int i = 0; i < cells.size(); i++) {
                boolean firstColumn = (i == 0);
                String content = cells.get(i);
                PdfPCell c = new PdfPCell(new Phrase(content, font));

                if (firstRow) {
                    // c.setPaddingBottom(5);
                    c.setHorizontalAlignment(Element.ALIGN_CENTER);
                    BaseColor backgroundColor = configuration.stepDataTableHeaderBackground();
                    if (backgroundColor != null) {
                        c.setBackgroundColor(backgroundColor);
                    }
                }

                int border = 0;
                if (!firstColumn) {
                    border += Rectangle.LEFT;
                }
                c.setBorder(border);

                table.addCell(c);
            }
        }
        return table;
    }

    private PdfPTable createEmptyStepDataTable(List<DataTableRow> tableRows) {
        PdfPTable table = new PdfPTable(getTableColumnCount(tableRows));
        table.setTableEvent(new AlternatingBackgroundEvent(configuration.stepDataTableRowAlternateBackground()));
        try {
            int[] columnMaxSizes = getTableColumnsContentMaxLength(tableRows);
            table.setWidths(columnMaxSizes);
        } catch (DocumentException e) {
            // Should not append since columnMaxSizes comes from tableRows.
        }
        return table;
    }

    private int getTableColumnCount(List<DataTableRow> tableRows) {
        if (!tableRows.isEmpty()) {
            List<String> cells = tableRows.get(0).getCells();
            if (cells != null) {
                return cells.size();
            }
        }
        return 1; // weird
    }

    private int[] getTableColumnsContentMaxLength(List<DataTableRow> tableRows) {
        int[] columnMaxSizes = null;
        for (DataTableRow tableRow : tableRows) {
            List<String> cells = tableRow.getCells();
            for (int i = 0; i < cells.size(); i++) {
                if (columnMaxSizes == null) {
                    columnMaxSizes = new int[cells.size()];
                }
                columnMaxSizes[i] = Math.max(columnMaxSizes[i], cells.get(i).length());
            }
        }
        return columnMaxSizes;
    }

    public void done() {
        pageNumber.continueExtra();
        emitSummary();
        emitTableOfContents();
        closeDocumentAndFile();

        // 2nd pass
        postProcessFile();
    }

    private void emitTableOfContents() {
        try {
            document.add(generateTableOfContents());
        } catch (DocumentException e) {
            log.warn("Failed to add table of content", e);
        }
    }

    private Chapter generateTableOfContents() {

        Chapter toc = new Chapter(new Paragraph("Table of content", configuration.chapterTitleFont()), -1);
        toc.setChapterNumber(0);
        toc.setNumberDepth(0);
        toc.add(new Paragraph(""));

        Chunk CONNECT = new Chunk(new LineSeparator(0.5f, 95, configuration.defaultColor(), Element.ALIGN_CENTER, -.5f));
        Paragraph paragraph = new Paragraph();
        paragraph.setSpacingBefore(20.0f); // first paragraph only
        for (TableOfContents.Entry entry : tableOfContents.getEntries()) {
            Chunk chunk = new Chunk(entry.getText(), configuration.tocEntryFont());
            chunk.setLocalGoto(entry.getAnchorDst());
            paragraph.add(chunk);
            paragraph.add(CONNECT);
            paragraph.add(new Chunk("" + entry.getFormattedPageNumber(), configuration.tocEntryFont()));

            float indent = 10.0f * entry.getLevel();
            paragraph.setIndentationLeft(indent);

            toc.add(paragraph);
            paragraph = new Paragraph();
        }
        return toc;

    }

    private void closeDocumentAndFile() {
        try {
            if (document != null) {
                document.close();
            }

            if (fileOutputStream != null) {
                fileOutputStream.close();
            }

            log.info("Report generated {}", fileTmp.getAbsolutePath());
        } catch (Exception e) {
            throw new FormatterException("Error while flushing report to disk", e);
        }
    }

    /**
     * 2nd pass post-processing
     */
    private void postProcessFile() {

        FileInputStream in = null;
        FileOutputStream out = null;

        try {
            in = new FileInputStream(fileTmp);
            out = new FileOutputStream(fileDst);

            int startPage = lookupExtraInsertionPage() + 1;
            ColumnText ct = generateTableOfContent();

            PdfReader reader = new PdfReader(in);
            PdfStamper stamper = new PdfStamper(reader, out);
            while (true) {
                stamper.insertPage(++startPage, reader.getPageSize(1));
                ct.setCanvas(stamper.getOverContent(startPage));
                ct.setSimpleColumn(36, 36, 559, 770);
                if (!ColumnText.hasMoreText(ct.go()))
                    break;
            }
            stamper.close();

        } catch (FileNotFoundException e) {
            log.error("Unable to reopen temporary generated file", e);
        } catch (DocumentException e) {
            log.error("Error during report post-processing", e);
        } catch (IOException e) {
            log.error("Error during report post-processing", e);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
        }
    }

    private ColumnText generateTableOfContent() {
        ColumnText ct = new ColumnText(null);

        Chunk CONNECT = new Chunk(new LineSeparator(0.5f, 95, configuration.defaultColor(), Element.ALIGN_CENTER, -.5f));
        Paragraph paragraph = new Paragraph();
        paragraph.setSpacingBefore(20.0f); // first paragraph only

        ct.addElement(new Paragraph("Table of content", configuration.chapterTitleFont()));
        ct.addElement(new Paragraph(""));

        for (TableOfContents.Entry entry : tableOfContents.getEntries()) {
            if (entry.isExtra())
                continue;

            Chunk chunk = new Chunk(entry.getText(), configuration.tocEntryFont());
            paragraph.add(chunk);
            paragraph.add(CONNECT);
            paragraph.add(new Chunk("" + entry.getFormattedPageNumber(), configuration.tocEntryFont()));

            float indent = 10.0f * entry.getLevel();
            paragraph.setIndentationLeft(indent);

            ct.addElement(paragraph);
            paragraph = new Paragraph();
        }
        return ct;
    }

    private int lookupExtraInsertionPage() {
        int startPage = -1;
        for (PageInfos pageInfos : pageNumber.getEmittedPageInfos()) {
            if (!pageInfos.isExtra())
                break;
            startPage = pageInfos.getRawPageNumber();
        }
        return startPage;
    }

    private void emitSummary() {
        if (!configuration.shouldEmitSummary()) {
            return;
        }

        Chapter summary = configuration.createTitledChapter("Summary");

        emitScenarioSummary(summary);
        emitStepsSummary(summary);
        emitTagsSummary(summary);

        try {
            document.add(summary);
        } catch (DocumentException e) {
            log.warn("Failed to add summary", e);
        }
    }

    private void emitTagsSummary(Chapter chapter) {
        PdfPTable table = new PdfPTable(new float[]{10.f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f});
        table.setHeaderRows(2);
        table.setTableEvent(new AlternatingBackgroundEvent(configuration.tableAlternateBackground()));

        PdfPCell cell = createTableHeaderCell("Tags");
        cell.setRowspan(2);
        table.addCell(border(cell, Rectangle.RIGHT));

        PdfPCell cellScenario = createTableHeaderCell("Scenario");
        cellScenario.setColspan(3);
        table.addCell(sideBorder(cellScenario));

        PdfPCell cellSteps = createTableHeaderCell("Steps");
        cellSteps.setColspan(3);
        table.addCell(sideBorder(cellSteps));

        //
        table.addCell(sideBorder(createTableHeaderCell("M.")));
        table.addCell(sideBorder(createTableHeaderCell("OK")));
        table.addCell(sideBorder(createTableHeaderCell("KO")));
        table.addCell(sideBorder(createTableHeaderCell("M.")));
        table.addCell(sideBorder(createTableHeaderCell("OK")));
        table.addCell(sideBorder(createTableHeaderCell("KO")));

        ColorThresholdSelector successColors = ColorThresholdSelectors.redOrangeGreenPercent();
        ColorThresholdSelector errorColors = ColorThresholdSelectors.yellowOrangeRedPercent();


        Map<String, Statistics> tagStatistics = statistics.getTagStatistics();
        List<String> tags = Lists.newArrayList(tagStatistics.keySet());
        Collections.sort(tags);
        for (String tag : tags) {
            Statistics tagStats = tagStatistics.get(tag);

            table.addCell(noBorder(new PdfPCell(new Phrase(tag))));
            //
            table.addCell(sideBorder(addCell(errorColors, tagStats.getNbScenario(), tagStats.getNbScenarioManual())));
            table.addCell(sideBorder(addCell(successColors, tagStats.getNbScenarioExceptManual(), tagStats.getNbScenarioSucceeded())));
            table.addCell(sideBorder(addCell(errorColors, tagStats.getNbScenarioExceptManual(), tagStats.getNbScenarioExceptManual() - tagStats.getNbScenarioSucceeded())));
            //
            table.addCell(sideBorder(addCell(errorColors, tagStats.getNbSteps(), tagStats.getNbStepManual())));
            table.addCell(sideBorder(addCell(successColors, tagStats.getNbStepsExceptManual(), tagStats.getNbStepSucceeded())));
            table.addCell(noBorder(addCell(errorColors, tagStats.getNbStepsExceptManual(), tagStats.getNbStepsExceptManual() - tagStats.getNbStepSucceeded())));
        }


        Paragraph paragraph = new Paragraph();
        paragraph.add(table);
        paragraph.setSpacingBefore(20f);
        paragraph.setSpacingAfter(20f);
        Section section = chapter.addSection(20f, new Paragraph("Tags", configuration.sectionTitleFont()));
        section.setIndentationLeft(20.0f);
        section.add(paragraph);
    }

    private PdfPCell createTableHeaderCell(String text) {
        PdfPCell pdfPCell = new PdfPCell(new Phrase(text, configuration.tableHeaderFont()));
        pdfPCell.setBackgroundColor(configuration.tableHeaderBackground());
        pdfPCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return pdfPCell;
    }

    private static PdfPCell addCell(ColorThresholdSelector colors, int total, int value) {
        PdfPCell cell = new PdfPCell(new Phrase("" + value));
        cell.setCellEvent(new PercentBackgroundEvent(value, total, colors));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        return cell;
    }

    private void emitStepsSummary(Chapter chapter) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(createStepsManualStatisticsTable(statistics));
        paragraph.add(createStepsAutomaticStatisticsTable(statistics));
        paragraph.setSpacingAfter(20f);

        Section section = chapter.addSection(20f, new Paragraph("Steps", configuration.sectionTitleFont()));
        section.setIndentationLeft(20.0f);
        section.add(paragraph);
    }

    private void emitScenarioSummary(Chapter chapter) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(createScenarioManualStatisticsTable(statistics));
        paragraph.add(createScenarioAutomaticStatisticsTable(statistics));
        paragraph.setSpacingAfter(20f);

        Section section = chapter.addSection(20f, new Paragraph("Scenario", configuration.sectionTitleFont()));
        section.setIndentationLeft(20.0f);
        section.add(paragraph);
    }

    private Paragraph createScenarioManualStatisticsTable(Statistics statistics) {

        ColorThresholdSelector errorColors = ColorThresholdSelectors.yellowOrangeRedPercent();

        PdfPTable table = new PdfPTable(2);
        int total = statistics.getNbScenario();
        appendRow(table, errorColors, total, statistics.getNbScenarioManual(), "Manual");

        Paragraph paragraph = new Paragraph();
        Phrase phrase = new Phrase("Manual scenario", configuration.subSectionTitleFont());
        paragraph.add(phrase);
        paragraph.add(table);
        paragraph.setSpacingBefore(20f);
        return paragraph;
    }

    private Paragraph createScenarioAutomaticStatisticsTable(Statistics statistics) {

        ColorThresholdSelector successColors = ColorThresholdSelectors.redOrangeGreenPercent();
        ColorThresholdSelector errorColors = ColorThresholdSelectors.yellowOrangeRedPercent();

        PdfPTable table = new PdfPTable(2);
        int total = statistics.getNbScenarioExceptManual();
        appendRow(table, successColors, total, statistics.getNbScenarioSucceeded(), "Success");
        appendRow(table, errorColors, total, statistics.getNbScenarioFailed(), "Failure");
        appendRow(table, errorColors, total, statistics.getNbScenarioPending(), "Pending");
        appendRow(table, errorColors, total, statistics.getNbScenarioSkipped(), "Skipped");
        appendRow(table, errorColors, total, statistics.getNbScenarioOther(), "Other");
        appendTotalRow(table, total, "Total");

        Paragraph paragraph = new Paragraph();
        Phrase phrase = new Phrase("Automatised scenario", configuration.subSectionTitleFont());
        paragraph.add(phrase);
        paragraph.add(table);
        paragraph.setSpacingBefore(20f);
        return paragraph;
    }

    private Paragraph createStepsAutomaticStatisticsTable(Statistics statistics) {

        ColorThresholdSelector successColors = ColorThresholdSelectors.redOrangeGreenPercent();
        ColorThresholdSelector errorColors = ColorThresholdSelectors.yellowOrangeRedPercent();

        PdfPTable table = new PdfPTable(2);

        int total = statistics.getNbStepsExceptManual();
        appendRow(table, successColors, total, statistics.getNbStepSucceeded(), "Success");
        appendRow(table, errorColors, total, statistics.getNbStepFailed(), "Failure");
        appendRow(table, errorColors, total, statistics.getNbStepPending(), "Pending");
        appendRow(table, errorColors, total, statistics.getNbStepSkipped(), "Skipped");
        appendRow(table, errorColors, total, statistics.getNbStepNoMatching(), "No matching");
        appendRow(table, errorColors, total, statistics.getNbStepOther(), "Other");
        appendTotalRow(table, total, "Total");

        Paragraph paragraph = new Paragraph();
        Phrase phrase = new Phrase("Automatised steps", configuration.subSectionTitleFont());
        paragraph.add(phrase);
        paragraph.add(table);
        paragraph.setSpacingBefore(20f);
        return paragraph;
    }

    private Paragraph createStepsManualStatisticsTable(Statistics statistics) {
        ColorThresholdSelector errorColors = ColorThresholdSelectors.yellowOrangeRedPercent();

        PdfPTable table = new PdfPTable(2);
        int total = statistics.getNbSteps();
        appendRow(table, errorColors, total, statistics.getNbStepManual(), "Manual");

        Paragraph paragraph = new Paragraph();
        Phrase phrase = new Phrase("Manual steps", configuration.subSectionTitleFont());
        paragraph.add(phrase);
        paragraph.add(table);
        paragraph.setSpacingBefore(20f);
        return paragraph;
    }


    private void appendTotalRow(PdfPTable table, int count, String title) {
        PdfPCell cellTitle = new PdfPCell(new Phrase(title, configuration.defaultStrongFont()));
        cellTitle.setHorizontalAlignment(Element.ALIGN_RIGHT);
        PdfPCell cellValue = new PdfPCell(new Phrase(String.valueOf(count)));
        cellValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cellTitle);
        table.addCell(cellValue);
    }

    private void appendRow(PdfPTable table, ColorThresholdSelector colorSelector, int total, int count, String title) {
        PdfPCell cellTitle = new PdfPCell(new Phrase(title, configuration.defaultStrongFont()));
        PdfPCell cellValue = new PdfPCell(new Phrase(String.format("%d/%d", count, total)));
        cellValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellValue.setCellEvent(new PercentBackgroundEvent(count, total, colorSelector));
        table.addCell(cellTitle);
        table.addCell(cellValue);
    }
}
