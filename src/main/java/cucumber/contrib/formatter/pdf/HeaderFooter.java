package cucumber.contrib.formatter.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import static com.itextpdf.text.pdf.ColumnText.showTextAligned;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class HeaderFooter extends PdfPageEventHelper {

    private final PageNumber pageNumber;
    private final String firstPageHeaderTemplateText;
    private final String pageHeaderTemplateText;
    private final Font headerFont;
    private final Phrase pageHeader;
    private final String firstPageFooterTemplateText;
    private final String pageFooterTemplateText;
    private final Font footerFont;
    private final Phrase pageFooter;
    private final BaseColor lineColor;

    private int currentPage = 0;
    private Rectangle rect;


    public HeaderFooter(PageNumber pageNumber,
                        String firstPageHeaderTemplateText,
                        String pageHeaderTemplateText,
                        Font headerFont,
                        Phrase pageHeader,
                        String firstPageFooterTemplateText,
                        String pageFooterTemplateText,
                        Font footerFont,
                        Phrase pageFooter,
                        BaseColor lineColor) {
        this.pageNumber = pageNumber;
        this.firstPageHeaderTemplateText = firstPageHeaderTemplateText;
        this.pageHeaderTemplateText = pageHeaderTemplateText;
        this.headerFont = headerFont;
        this.pageHeader = pageHeader;
        this.firstPageFooterTemplateText = firstPageFooterTemplateText;
        this.pageFooterTemplateText = pageFooterTemplateText;
        this.footerFont = footerFont;
        this.pageFooter = pageFooter;
        this.lineColor = lineColor;
    }

    public void onOpenDocument(PdfWriter writer, Document document) {
    }

    public void onChapter(PdfWriter writer, Document document, float paragraphPosition, Paragraph title) {
    }

    public void onStartPage(PdfWriter writer, Document document) {
        pageNumber.notifyPageChange(currentPage++);
    }

    public void onEndPage(PdfWriter writer, Document document) {
        if (rect == null)
            rect = writer.getBoxSize("art");

        PdfContentByte canvas = writer.getDirectContent();
        drawHeader(canvas);
        drawFooter(canvas, pageNumber.pageInfos());
    }

    public void drawHeader(PdfContentByte canvas) {
        float top = rect.getTop() + 20;
        Phrase header = headerText();
        if (header != null) {
            showTextAligned(canvas, Element.ALIGN_CENTER, header, (rect.getLeft() + rect.getRight()) / 2, top, 0);
        }
    }

    public void drawFooter(PdfContentByte canvas, PageInfos pageInfos) {
        if (lineColor != null) {
            canvas.saveState();
            canvas.setColorStroke(lineColor);
            canvas.setLineWidth(1.2f);
            canvas.moveTo(rect.getLeft(), rect.getBottom() - 6);
            canvas.lineTo(rect.getRight(), rect.getBottom() - 6);
            canvas.stroke();
            canvas.restoreState();
        }

        float bottom = rect.getBottom() - 20;
        Phrase footer = footerText();
        if (footer != null) {
            showTextAligned(canvas, Element.ALIGN_LEFT, footer, rect.getLeft(), bottom, 0);
        }

        Phrase page = new Phrase(pageInfos.getFormattedPageNumber(), footerFont);
        showTextAligned(canvas, Element.ALIGN_RIGHT, page, rect.getRight(), bottom, 0);
    }

    private Phrase headerText() {
        if (currentPage == 1) {
            if (firstPageHeaderTemplateText != null)
                return new Phrase(firstPageHeaderTemplateText, headerFont);
        } else {
            if (pageHeader != null)
                return pageHeader;
            else if (pageHeaderTemplateText != null)
                return new Phrase(pageHeaderTemplateText, headerFont);
        }
        return null;
    }

    private Phrase footerText() {
        if (currentPage == 1) {
            if (firstPageFooterTemplateText != null)
                return new Phrase(firstPageFooterTemplateText, footerFont);
        } else {
            if (pageFooter != null)
                return pageFooter;
            else if (pageFooterTemplateText != null)
                return new Phrase(pageFooterTemplateText, footerFont);
        }
        return null;
    }


}