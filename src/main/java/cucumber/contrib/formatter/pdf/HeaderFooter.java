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
    private int pagenumber;
    private final String firstPageFooter;
    private final String pageFooter;
    private final BaseColor lineColor;
    private final Font textFont;

    public HeaderFooter(String firstPageFooter,
                        String pageFooter,
                        BaseColor lineColor,
                        Font textFont) {
        this.firstPageFooter = firstPageFooter;
        this.pageFooter = pageFooter;
        this.lineColor = lineColor;
        this.textFont = textFont;
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
        float y = rect.getBottom() - 20;

        PdfContentByte canvas = writer.getDirectContentUnder();
        canvas.saveState();
        canvas.setColorStroke(lineColor);
        canvas.setLineWidth(1.2f);
        canvas.moveTo(rect.getLeft(), rect.getBottom() - 6);
        canvas.lineTo(rect.getRight(), rect.getBottom() - 6);
        canvas.stroke();
        canvas.restoreState();


        String textContent = pageFooter;
        if (pagenumber == 1) {
            textContent = firstPageFooter;
        }
        Phrase page = new Phrase(String.format("%d", pagenumber), textFont);
        Phrase text = new Phrase(textContent, textFont);

        showTextAligned(writer.getDirectContent(), Element.ALIGN_LEFT, text, rect.getLeft(), y, 0);
        showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT, page, rect.getRight(), y, 0);

    }
}