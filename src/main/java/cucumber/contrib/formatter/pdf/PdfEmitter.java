package cucumber.contrib.formatter.pdf;

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
import java.util.List;

/**
 */
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
        return stepKeywordFont();
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
            emit(section, step);
        }
        steps.setIndentationLeft(25.0f);
        steps.setSpacingBefore(25.0f);
        steps.setSpacingAfter(25.0f);
        section.add(steps);
    }

    public void emit(Section section, StepWrapper step) {
        Paragraph stepParagraph = new Paragraph();
        stepParagraph.add(new Chunk(step.getKeyword(), stepKeywordFont()));
        stepParagraph.add(new Chunk(step.getName(), stepDefaultFont()));

        if (step.hasTable()) {
            PdfPTable table = formatTable(step.getTableRows());
            stepParagraph.add(table);
        }

        section.add(stepParagraph);
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

        for (int j = 0; j < tableRows.size(); j++) {
            boolean firstRow = (j == 0);
            Row row = tableRows.get(j);

            List<String> cells = row.getCells();
            if (table == null) {
                table = new PdfPTable(cells.size());
            }

            Font font = getTableFont(firstRow);

            for (int i = 0; i < cells.size(); i++) {
                String content = cells.get(i);
                PdfPCell c = new PdfPCell(new Phrase(content, font));
                c.setUseVariableBorders(true);
                int border = 0;
                if (i > 0) {
                    border += Rectangle.LEFT;
                }

                if (firstRow) {
                    border += Rectangle.BOTTOM;
                    c.setBorderWidthBottom(1.5f);
                    c.setPaddingTop(5);
                    c.setPaddingBottom(5);
                } else {
                    c.setBorderWidthBottom(0.5f);
                    border += Rectangle.TOP;
                }

                // alternate bg
                if (j > 0 && j % 2 == 0) {
                    c.setBackgroundColor(VERY_LIGHT_GRAY);
                }
                c.setBorder(border);
                table.addCell(c);
            }
        }
        return table;
    }

    public void done() {
        System.out.println("PdfEmitter.done!");
        document.close();
    }
}
