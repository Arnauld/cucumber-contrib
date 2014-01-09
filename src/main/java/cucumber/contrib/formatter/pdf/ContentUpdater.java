package cucumber.contrib.formatter.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;

public interface ContentUpdater {
    void update(Configuration configuration, Document document) throws DocumentException;
}
