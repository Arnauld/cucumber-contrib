package cucumber.contrib.grammar.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import cucumber.contrib.grammar.java.Grammar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class GrammarPdfReport {

    private final Configuration configuration;
    //
    private File fileDst;
    private Document document;
    private FileOutputStream fileOutputStream;
    private PdfWriter pdfWriter;

    public GrammarPdfReport(Configuration configuration) {
        this.configuration = configuration;
    }

    public void init(File fileDst) throws FileNotFoundException, DocumentException {
        this.fileDst = new File(fileDst.getParentFile(), fileDst.getName() + ".tmp");
        this.document = configuration.createDocument();
        this.fileOutputStream = new FileOutputStream(fileDst);

        //
        pdfWriter = PdfWriter.getInstance(document, fileOutputStream);
        pdfWriter.setBoxSize("art", configuration.getDocumentArtBox());
        document.open();
    }

    public void emit(Grammar grammar) {
        emitFirstPages();
    }


    protected void emitFirstPages() {
    }

    public Document getDocument() {
        return document;
    }
}
