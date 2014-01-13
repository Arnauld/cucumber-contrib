package cucumber.contrib.formatter.pdf;

import static com.itextpdf.text.pdf.ColumnText.showTextAligned;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class HeaderFooter extends PdfPageEventHelper {

    private final String firstPageHeaderTemplateText;
    private final String pageHeaderTemplateText;
    private final Font headerFont;
    private final Phrase pageHeader;
    private final String firstPageFooterTemplateText;
    private final String pageFooterTemplateText;
    private final Font footerFont;
    private final Phrase pageFooter;
    private final BaseColor lineColor;

    private int pagenumber;

    public HeaderFooter(String firstPageHeaderTemplateText,
                        String pageHeaderTemplateText,
                        Font headerFont,
                        Phrase pageHeader,
                        String firstPageFooterTemplateText,
                        String pageFooterTemplateText,
                        Font footerFont,
                        Phrase pageFooter,
                        BaseColor lineColor) {
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
        pagenumber++;
    }

    public void onEndPage(PdfWriter writer, Document document) {
        Rectangle rect = writer.getBoxSize("art");
        float top = rect.getTop() + 20;
        float bottom = rect.getBottom() - 20;

        if(lineColor != null) {
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
        if (pagenumber == 1) {
            if(firstPageHeaderTemplateText != null)
                header = new Phrase(firstPageHeaderTemplateText, headerFont);
            if(firstPageFooterTemplateText != null)
                footer = new Phrase(firstPageFooterTemplateText, footerFont);
        }
        else {
            if(pageHeader != null)
                header = pageHeader;
            else if(pageHeaderTemplateText != null)
                header = new Phrase(pageHeaderTemplateText, headerFont);

            if(pageFooter != null)
                footer = pageFooter;
            else if(pageFooterTemplateText != null)
                footer = new Phrase(pageFooterTemplateText, footerFont);
        }

        showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER, header, (rect.getLeft() + rect.getRight())/2 , top, 0);

        Phrase page = new Phrase(String.format("%d", pagenumber), footerFont);
        showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, footer, rect.getLeft(), bottom, 0);
        showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT, page, rect.getRight(), bottom, 0);
    }
}