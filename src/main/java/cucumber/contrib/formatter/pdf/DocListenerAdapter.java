package cucumber.contrib.formatter.pdf;

import com.itextpdf.text.DocListener;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Rectangle;

/**
 *
 */
public class DocListenerAdapter implements DocListener {
    @Override
    public void open() {
    }

    @Override
    public void close() {
    }

    @Override
    public boolean newPage() {
        return false;
    }

    @Override
    public boolean setPageSize(Rectangle pageSize) {
        return false;
    }

    @Override
    public boolean setMargins(float marginLeft, float marginRight, float marginTop, float marginBottom) {
        return false;
    }

    @Override
    public boolean setMarginMirroring(boolean marginMirroring) {
        return false;
    }

    @Override
    public boolean setMarginMirroringTopBottom(boolean marginMirroringTopBottom) {
        return false;
    }

    @Override
    public void setPageCount(int pageN) {
    }

    @Override
    public void resetPageCount() {
    }

    @Override
    public boolean add(Element element) throws DocumentException {
        return false;
    }
}
