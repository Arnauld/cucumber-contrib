package cucumber.contrib.formatter.pdf;

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import cucumber.contrib.formatter.model.FeatureWrapper;
import cucumber.contrib.formatter.model.ScenarioWrapper;
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
    private MarkdownEmitter markdownEmitter;

    public void init(File file) throws FileNotFoundException, DocumentException {
        markdownEmitter = new MarkdownEmitter();
        document = new Document(PageSize.A4, 50, 50, 50, 50);
        writer = PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();
        writePreamble();
    }

    protected Font partFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 18, Font.ITALIC, new CMYKColor(0, 255, 255, 17));
    }

    protected Font featureTitleFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD, new CMYKColor(0, 255, 255, 17));
    }

    protected Font scenarioTitleFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD, new CMYKColor(0, 255, 255, 17));
    }

    protected Font stepKeywordFont() {
        return FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD, new CMYKColor(255, 255, 0, 17));
    }

    protected Font stepDefaultFont() {
        return FontFactory.getFont(FontFactory.COURIER, 8, Font.NORMAL, new CMYKColor(255, 255, 0, 17));
    }

    protected Font tagsFont() {
        return FontFactory.getFont(FontFactory.COURIER, 8, Font.ITALIC, new CMYKColor(25, 255, 255, 17));
    }

    protected Font tableHeaderFont() {
        return FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD, BaseColor.WHITE);
    }

    protected Font tableDataFont() {
        return stepDefaultFont();
    }

    protected void writePreamble() {

    }

    protected Font defaultFont() {
        return FontFactory.getFont(FontFactory.TIMES, 8, Font.NORMAL, new CMYKColor(0, 255, 255, 17));
    }

    public void emit(FeatureWrapper feature) {
        Paragraph featureTitle = new Paragraph(feature.getName(), featureTitleFont());
        Chapter featureChap = new Chapter(featureTitle, 1);
        featureChap.setNumberDepth(0);

        Paragraph paragraph = new Paragraph("", defaultFont());
        paragraph.setSpacingBefore(25.0f);
        paragraph.setSpacingAfter(25.0f);
        appendMarkdownContent(paragraph, feature.getDescription());
        featureChap.add(paragraph);

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

    private void appendMarkdownContent(final java.util.List<Element> elements, String markdownText) {
        elements.addAll(markdownEmitter.markdownToElements(markdownText));
    }

    public void emit(Chapter featureChap, ScenarioWrapper scenario) {
        Paragraph scenarioTitle = new Paragraph(scenario.getName(), scenarioTitleFont());
        Section section = featureChap.addSection(scenarioTitle);

        Paragraph tags = new Paragraph("Tags: ", defaultFont());
        for (Tag tag : scenario.getTags()) {
            tags.add(new Chunk(tag.getName(), tagsFont()));
        }
        section.add(tags);
        appendMarkdownContent(section, scenario.getDescription());

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
            stepAsTable.setTotalWidth(new float[] { 16.5f, documentContentWidth() - 16.5f });
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
        stepParagraph.add(new Chunk(step.getKeyword(), stepKeywordFont()));
        stepParagraph.add(new Chunk(step.getName(), stepDefaultFont()));

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
        ByteSource byteSource = null;
        try {
            String resourceName = getStepStatusResourceName(step);
            byteSource = Resources.asByteSource(getClass().getResource(resourceName));
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

    private BaseColor VERY_LIGHT_GRAY = new BaseColor(215, 215, 215);

    private Font getTableFont(boolean firstRow) {
        if (firstRow) {
            return tableHeaderFont();
        } else {
            return tableDataFont();
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
                    c.setPaddingBottom(5);
                    c.setHorizontalAlignment(Element.ALIGN_CENTER);
                    c.setBackgroundColor(new BaseColor(0, 183, 255));
                }

                // alternate bg
                if (j > 0 && j % 2 == 0) {
                    c.setBackgroundColor(VERY_LIGHT_GRAY);
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
        for (int j = 0; j < tableRows.size(); j++) {
            List<String> cells = tableRows.get(j).getCells();
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
