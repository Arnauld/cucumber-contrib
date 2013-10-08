package cucumber.contrib.formatter.pdf;

import com.google.common.io.ByteSource;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.CMYKColor;
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

import static com.google.common.io.Resources.asByteSource;

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
        document.open();

    }

    private void writePreamble(String featureRootUri) throws DocumentException {
        configuration.writePreambule(document);
    }

    protected Font defaultFont() {
        return FontFactory.getFont(FontFactory.TIMES, 8, Font.NORMAL, new CMYKColor(0, 255, 255, 17));
    }

    public void emit(FeatureWrapper feature) {
        if(firstFeature) {
            firstFeature = false;
            try {
                writePreamble(extractUriParent(feature.getUri()));
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }

        feature.consolidate(statistics);

        //
        // Title
        Paragraph featureTitle = new Paragraph(feature.getName(), configuration.featureTitleFont());
        Chapter featureChap = new Chapter(featureTitle, 1);
        featureChap.setNumberDepth(0);

        // Uri
        Paragraph uri = new Paragraph("Uri: " + feature.getUri(), defaultFont());
        featureChap.add(uri);

        // Description
        Paragraph paragraph = new Paragraph("", defaultFont());
        paragraph.setSpacingBefore(25.0f);
        paragraph.setSpacingAfter(25.0f);
        configuration.appendMarkdownContent(paragraph, feature.getDescription());
        featureChap.add(paragraph);

        // Scenario
        for (ScenarioWrapper scenario : feature.getScenarios()) {
            emit(featureChap, scenario);
        }

        //
        try {
            document.add(featureChap);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private String extractUriParent(String uri) {
        int index = uri.lastIndexOf('/');
        if(index > 0)
            return uri.substring(0, index);
        return null;
    }

    public void emit(Chapter featureChap, ScenarioWrapper scenario) {
        Paragraph scenarioTitle = new Paragraph(scenario.getName(), configuration.scenarioTitleFont());
        Section section = featureChap.addSection(scenarioTitle);

        Paragraph tags = new Paragraph("Tags: ", defaultFont());
        for (Tag tag : scenario.getTags()) {
            tags.add(new Chunk(tag.getName(), configuration.tagsFont()));
        }
        section.add(tags);
        configuration.appendMarkdownContent(section, scenario.getDescription());

        Paragraph steps = new Paragraph("");
        for (StepWrapper step : scenario.getSteps()) {
            emit(steps, step);
        }
        // steps.setIndentationLeft(25.0f);
        steps.setSpacingBefore(25.0f);
        steps.setSpacingAfter(25.0f);
        section.add(steps);
    }

    private float documentContentWidth() {
        return document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
    }

    public void emit(Paragraph steps, StepWrapper step) {

        PdfPTable stepAsTable = new PdfPTable(2);
        try {
            stepAsTable.setTotalWidth(new float[]{16.5f, documentContentWidth() - 16.5f});
        } catch (DocumentException e) {
            // ignore?
            e.printStackTrace();
        }

        Image stepStatus = getStepStatusAsImageOrNull(step);
        PdfPCell cell;
        if (stepStatus != null) {
            stepStatus.scaleAbsolute(16.5f, 10.5f);
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
            PdfPTable table = formatTable(step.getTableRows());
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
            return configuration.tableHeaderFont();
        } else {
            return configuration.tableDataFont();
        }
    }

    private PdfPTable formatTable(List<DataTableRow> tableRows) {
        PdfPTable table = null;

        int[] columnMaxSizes = getTableColumnsContentMaxLength(tableRows);

        for (int j = 0; j < tableRows.size(); j++) {
            boolean firstRow = (j == 0);
            Row row = tableRows.get(j);

            List<String> cells = row.getCells();
            if (table == null) {
                table = new PdfPTable(cells.size());
                try {
                    table.setWidths(columnMaxSizes);
                } catch (DocumentException e) {
                    // Should not append since columnMaxSizes comes from tableRows.
                }
            }

            Font font = getTableFont(firstRow);

            for (int i = 0; i < cells.size(); i++) {
                boolean firstColumn = (i == 0);
                String content = cells.get(i);
                PdfPCell c = new PdfPCell(new Phrase(content, font));

                if (firstRow) {
                    // c.setPaddingBottom(5);
                    c.setHorizontalAlignment(Element.ALIGN_CENTER);
                    BaseColor backgroundColor = configuration.tableHeaderBackground();
                    if (backgroundColor != null)
                        c.setBackgroundColor(backgroundColor);
                }

                // alternate bg
                if (j > 0 && j % 2 == 0) {
                    BaseColor backgroundColor = configuration.tableRowAlternateBackground();
                    if (backgroundColor != null)
                        c.setBackgroundColor(backgroundColor);
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
        System.out.println("PdfEmitter.done!");
        document.close();
    }
}
