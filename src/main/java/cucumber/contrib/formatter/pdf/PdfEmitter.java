package cucumber.contrib.formatter.pdf;

import static com.google.common.io.Resources.asByteSource;

import com.google.common.io.ByteSource;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import cucumber.contrib.formatter.model.FeatureWrapper;
import cucumber.contrib.formatter.model.ScenarioWrapper;
import cucumber.contrib.formatter.model.Statistics;
import cucumber.contrib.formatter.model.StepWrapper;
import gherkin.formatter.model.DataTableRow;
import gherkin.formatter.model.Row;
import gherkin.formatter.model.Tag;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PdfEmitter {

    private Document document;
    private PdfWriter writer;
    private Configuration configuration;
    private Statistics statistics;
    private boolean firstFeature = true;

    public PdfEmitter(Configuration configuration) {
        this.configuration = configuration;
        this.statistics = new Statistics();
    }

    public void init(File file) throws FileNotFoundException, DocumentException {
        document = configuration.createDocument();
        writer = PdfWriter.getInstance(document, new FileOutputStream(file));
        writer.setBoxSize("art", configuration.getDocumentArtBox());
        writer.setPageEvent(configuration.createHeaderFooter());
        document.open();
    }

    private void writeInitialData() {
        try {
            configuration.writeMetaInformations(document);
            configuration.writeFirstPages(document);
            configuration.writePreambule(document);
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public void emit(FeatureWrapper feature) {
        if (firstFeature) {
            firstFeature = false;
            writeInitialData();
        }

        feature.consolidate(statistics);

        //
        // Title
        Chapter featureChap = configuration.createTitledChapter(feature.getName());

        // Uri
        Paragraph uri = new Paragraph("Uri: " + feature.getUri(), configuration.defaultMetaFont());
        featureChap.add(uri);

        // Description
        Paragraph paragraph = new Paragraph("", configuration.defaultFont());
        paragraph.setSpacingBefore(25.0f);
        paragraph.setSpacingAfter(25.0f);
        configuration.appendMarkdownContent(paragraph, feature.getDescription());
        featureChap.add(paragraph);

        // Scenario
        for (ScenarioWrapper scenario : feature.getScenarios()) {
            emitScenario(featureChap, scenario);
        }

        //
        try {
            document.add(featureChap);
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void emitScenario(Chapter featureChap, ScenarioWrapper scenario) {
        Paragraph scenarioTitle = new Paragraph(scenario.getName(), configuration.scenarioTitleFont());
        Section section = featureChap.addSection(scenarioTitle);

        emitScenarioTags(scenario, section);
        configuration.appendMarkdownContent(section, scenario.getDescription());

        Paragraph steps = new Paragraph("");
        for (StepWrapper step : scenario.getSteps()) {
            emitStep(steps, step);
        }
        // steps.setIndentationLeft(25.0f);
        steps.setSpacingBefore(25.0f);
        steps.setSpacingAfter(25.0f);
        section.add(steps);
    }

    private void emitScenarioTags(ScenarioWrapper scenario, Section section) {
        List<Tag> tagList = scenario.getTags();
        if (!tagList.isEmpty()) {
            Paragraph tags = new Paragraph("Tags: ", configuration.defaultMetaFont());
            for (Tag tag : tagList) {
                tags.add(new Chunk(tag.getName(), configuration.tagsFont()));
            }
            section.add(tags);
        }
    }

    private float documentContentWidth() {
        return document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
    }

    private void emitStep(Paragraph steps, StepWrapper step) {
        float imageWidth = 16.5f;
        float imageHeight = 10.5f;

        PdfPTable stepAsTable = new PdfPTable(2);
        try {
            float imageCellWidth = imageWidth + 5f;
            stepAsTable.setTotalWidth(new float[]{imageCellWidth, documentContentWidth() - imageCellWidth});
        }
        catch (DocumentException e) {
            // ignore?
            e.printStackTrace();
        }

        Image stepStatus = getStepStatusAsImageOrNull(step);
        PdfPCell cell;
        if (stepStatus != null) {
            stepStatus.scaleAbsolute(imageWidth, imageHeight);
            cell = new PdfPCell(stepStatus);
            cell.setPaddingTop(2.0f);
        }
        else {
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
    }

    private static PdfPCell noBorder(PdfPCell pdfPCell) {
        pdfPCell.setBorder(Rectangle.NO_BORDER);
        return pdfPCell;
    }

    private Image getStepStatusAsImageOrNull(StepWrapper step) {
        try {
            String resourceName = getStepStatusResourceName(step);
            ByteSource byteSource = asByteSource(getClass().getResource(resourceName));
            byte[] bytes = byteSource.read();
            return Image.getInstance(bytes);
        }
        catch (BadElementException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getStepStatusResourceName(StepWrapper step) {
        if (step.isSuccess()) {
            return "images/ok-icon.PNG";
        }
        else if (step.isPending()) {
            return "images/pending-icon.PNG";
        }
        else if (step.isFailure()) {
            return "images/ko-icon.PNG";
        }
        else if (step.isSkipped()) {
            return "images/skipped-icon.PNG";
        }
        else {
            return "images/unknown-icon.PNG";
        }
    }

    private Font getTableFont(boolean firstRow) {
        if (firstRow) {
            return configuration.stepDataTableHeaderFont();
        }
        else {
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
        }
        catch (DocumentException e) {
            // Should not append since columnMaxSizes comes from tableRows.
        }
        return table;
    }

    private int getTableColumnCount(List<DataTableRow> tableRows) {
        if(!tableRows.isEmpty()) {
            List<String> cells = tableRows.get(0).getCells();
            if(cells != null)
                return cells.size();
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
        emitSummary();
        System.out.println("PdfEmitter.done!");
        document.close();
    }

    private void emitSummary() {
        if (!configuration.shouldEmitSummary()) {
            return;
        }

        Chapter summary = configuration.createTitledChapter("Summary");

        emitScenarioSummary(summary);
        emitStepsSummary(summary);

        try {
            document.add(summary);
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void emitStepsSummary(Chapter chapter) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(createStepsStatisticsTable(statistics));
        paragraph.setSpacingBefore(20f);
        paragraph.setSpacingAfter(20f);

        Section section = chapter.addSection(20f, new Paragraph("Steps", configuration.sectionTitleFont()));
        section.add(paragraph);
    }

    private void emitScenarioSummary(Chapter chapter) {
        Paragraph paragraph = new Paragraph();
        paragraph.add(createScenarioStatisticsTable(statistics));
        paragraph.setSpacingBefore(20f);
        paragraph.setSpacingAfter(20f);

        Section section = chapter.addSection(20f, new Paragraph("Scenario", configuration.sectionTitleFont()));
        section.add(paragraph);
    }

    private PdfPTable createScenarioStatisticsTable(Statistics statistics) {

        ColorThresholdSelector successColors = ColorThresholdSelectors.redOrangeGreenPercent();
        ColorThresholdSelector errorColors = ColorThresholdSelectors.yellowOrangeRedPercent();


        PdfPTable table = new PdfPTable(2);
        int total = statistics.getNbScenario();
        appendRow(table, successColors, total, statistics.getNbScenarioSucceeded(), "Success");
        appendRow(table, errorColors, total, statistics.getNbScenarioFailed(), "Failure");
        appendRow(table, errorColors, total, statistics.getNbScenarioPending(), "Pending");
        appendRow(table, errorColors, total, statistics.getNbScenarioSkipped(), "Skipped");
        appendRow(table, errorColors, total, statistics.getNbScenarioOther(), "Other");
        appendTotalRow(table, total, "Total");
        return table;
    }

    private PdfPTable createStepsStatisticsTable(Statistics statistics) {

        ColorThresholdSelector successColors = ColorThresholdSelectors.redOrangeGreenPercent();
        ColorThresholdSelector errorColors = ColorThresholdSelectors.yellowOrangeRedPercent();

        PdfPTable table = new PdfPTable(2);

        int total = statistics.getNbSteps();
        appendRow(table, successColors, total, statistics.getNbStepSucceeded(), "Success");
        appendRow(table, errorColors, total, statistics.getNbStepFailed(), "Failure");
        appendRow(table, errorColors, total, statistics.getNbStepPending(), "Pending");
        appendRow(table, errorColors, total, statistics.getNbStepSkipped(), "Skipped");
        appendRow(table, errorColors, total, statistics.getNbStepNoMatching(), "No matching");
        appendRow(table, errorColors, total, statistics.getNbStepOther(), "Other");
        appendTotalRow(table, total, "Total");
        return table;
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
        PdfPCell cellValue = new PdfPCell(new Phrase(String.valueOf(count)));
        cellValue.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellValue.setCellEvent(new PercentBackgroundEvent(count, total, colorSelector));
        table.addCell(cellTitle);
        table.addCell(cellValue);
    }
}
