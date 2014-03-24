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
        Rectangle rect = writer.getBoxSize("art");
        float top = rect.getTop() + 20;
        float bottom = rect.getBottom() - 20;

        if (lineColor != null) {
            PdfContentByte canvas = writer.getDirectContentUnder();
            canvas.saveState();
            canvas.setColorStroke(lineColor);
            canvas.setLineWidth(1.2f);
            canvas.moveTo(rect.getLeft(), rect.getBottom() - 6);
            canvas.lineTo(rect.getRight(), rect.getBottom() - 6);
            canvas.stroke();
            canvas.restoreState();
        }

        Phrase header = null;
        Phrase footer = null;
        if (currentPage == 1) {
            if (firstPageHeaderTemplateText != null)
                header = new Phrase(firstPageHeaderTemplateText, headerFont);
            if (firstPageFooterTemplateText != null)
                footer = new Phrase(firstPageFooterTemplateText, footerFont);
        } else {
            if (pageHeader != null)
                header = pageHeader;
            else if (pageHeaderTemplateText != null)
                header = new Phrase(pageHeaderTemplateText, headerFont);

            if (pageFooter != null)
                footer = pageFooter;
            else if (pageFooterTemplateText != null)
                footer = new Phrase(pageFooterTemplateText, footerFont);
        }

        showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, header, (rect.getLeft() + rect.getRight()) / 2, top, 0);

        Phrase page = new Phrase(pageNumber.formatPageNumber(), footerFont);
        showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, footer, rect.getLeft(), bottom, 0);
        showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT, page, rect.getRight(), bottom, 0);
    }
}