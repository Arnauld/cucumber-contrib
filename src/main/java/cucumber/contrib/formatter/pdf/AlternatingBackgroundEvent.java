package cucumber.contrib.formatter.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPTableEvent;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class AlternatingBackgroundEvent implements PdfPTableEvent {

    private final BaseColor alternateBackground;

    public AlternatingBackgroundEvent(BaseColor alternateBackground) {
        this.alternateBackground = alternateBackground;
    }

    public void tableLayout(PdfPTable table,
                            float[][] widths, float[] heights,
                            int headerRows, int rowStart, PdfContentByte[] canvases)
    {
        int footer = widths.length - table.getFooterRows();
        int header = table.getHeaderRows() - table.getFooterRows() + 1;
        for (int row = header; row < footer; row += 2) {
            int columns = widths[row].length - 1;
            Rectangle rect = new Rectangle(widths[row][0], heights[row], widths[row][columns], heights[row + 1]);
            rect.setBackgroundColor(alternateBackground);
            rect.setBorder(Rectangle.NO_BORDER);
            canvases[PdfPTable.BASECANVAS].rectangle(rect);
        }
    }
}
