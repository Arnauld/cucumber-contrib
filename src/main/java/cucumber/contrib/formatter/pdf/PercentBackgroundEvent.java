package cucumber.contrib.formatter.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class PercentBackgroundEvent implements PdfPCellEvent {
    private final float percent;
    private final ColorThresholdSelector colorThresholdSelectors;

    public PercentBackgroundEvent(int count, int total, ColorThresholdSelector colorThresholdSelectors) {
        this.colorThresholdSelectors = colorThresholdSelectors;
        if (total == 0) {
            this.percent = 0.0f;
        }
        else {
            this.percent = ((float) count) / ((float) total);
        }
    }

    public void cellLayout(PdfPCell cell, Rectangle rect, PdfContentByte[] canvas) {
        BaseColor color = colorThresholdSelectors.colorFor(percent);
        if (color != null) {
            PdfContentByte cb = canvas[PdfPTable.BACKGROUNDCANVAS];
            cb.saveState();
            cb.setColorFill(color);
            cb.rectangle(rect.getLeft(), rect.getBottom(), rect.getWidth() * percent, rect.getHeight());
            cb.fill();
            cb.restoreState();
        }
    }
}