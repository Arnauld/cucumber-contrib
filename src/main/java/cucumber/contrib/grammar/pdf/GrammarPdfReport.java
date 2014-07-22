package cucumber.contrib.grammar.pdf;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import cucumber.contrib.grammar.java.ClassEntry;
import cucumber.contrib.grammar.java.Grammar;
import cucumber.contrib.grammar.java.MethodEntry;
import cucumber.contrib.grammar.java.PackageEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.function.Consumer;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class GrammarPdfReport {
    private Logger log = LoggerFactory.getLogger(GrammarPdfReport.class);


    private static final int SUMMARY_NB_COLS = 4;
    //
    private BaseColor WHITE = new BaseColor(255, 255, 255);
    private BaseColor BLACK = new BaseColor(0, 0, 0);
    //
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
        this.fileDst = fileDst;
        this.document = configuration.createDocument();
        this.fileOutputStream = new FileOutputStream(fileDst);

        //
        pdfWriter = PdfWriter.getInstance(document, fileOutputStream);
        pdfWriter.setBoxSize("art", configuration.getDocumentArtBox());
        document.open();
    }

    public void emit(Grammar grammar) {
        emitFirstPages();


        try {
            PdfPTable summaryTable = new PdfPTable(SUMMARY_NB_COLS);
            summaryTable.setWidths(new float[]{0.5f, 0.5f, 10.0f, 10.0f});
            grammar.packages().forEach(emitPackageSummary(summaryTable));
            getDocument().add(summaryTable);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    private void emitPackageSummary(PdfPTable summaryTable, PackageEntry packageEntry) {
        log.debug("Emitting summary for package {}", packageEntry.name());

        if (packageEntry.hasClassEntries()) {
            Phrase phrase = new Phrase(packageEntry.name(), FontFactory.getFont("Arial", 8, WHITE));
            PdfPCell packageCell = new PdfPCell(phrase);
            packageCell.setBackgroundColor(BLACK);
            packageCell.setColspan(SUMMARY_NB_COLS);
            summaryTable.addCell(packageCell);

            packageEntry.classes().forEach(emitClassSummary(summaryTable));
        }

        packageEntry.subPackages().forEach(emitPackageSummary(summaryTable));
    }

    private void emitClassSummary(PdfPTable summaryTable, ClassEntry classEntry) {
        log.debug("Emitting summary for class {}", classEntry.name());

        Phrase phrase = new Phrase(classEntry.name(), FontFactory.getFont("Arial", 12, WHITE));
        PdfPCell classCell = new PdfPCell(phrase);
        classCell.setColspan(SUMMARY_NB_COLS - 1);
        classCell.setBackgroundColor(BaseColor.DARK_GRAY);
        classCell.setBorder(Rectangle.NO_BORDER);

        summaryTable.addCell(emptyCell());
        summaryTable.addCell(classCell);

        classEntry.methods().forEach(emitMethodSummary(summaryTable));
    }

    private void emitMethodSummary(final PdfPTable summaryTable, MethodEntry methodEntry) {
        log.debug("Emitting summary for method {}", methodEntry.signature());

        methodEntry.patterns().forEach(new Consumer<String>() {
            @Override
            public void accept(String pattern) {
                Phrase phrase = new Phrase(pattern, FontFactory.getFont("Arial", 10, BLACK));
                PdfPCell patternCell = new PdfPCell(phrase);
                patternCell.setColspan(SUMMARY_NB_COLS - 2);
                patternCell.setBorder(Rectangle.BOTTOM);
                patternCell.setBorderColor(BaseColor.LIGHT_GRAY);

                summaryTable.addCell(emptyCell(2));
                summaryTable.addCell(patternCell);
            }
        });
    }

    private PdfPCell emptyCell(int colspan) {
        PdfPCell pdfPCell = new PdfPCell(new Phrase(""));
        pdfPCell.setColspan(colspan);
        pdfPCell.setBorder(Rectangle.NO_BORDER);
        return pdfPCell;
    }

    private PdfPCell emptyCell() {
        return emptyCell(1);
    }

    protected void emitFirstPages() {
    }

    public Document getDocument() {
        return document;
    }

    private Consumer<? super MethodEntry> emitMethodSummary(final PdfPTable summaryTable) {
        return new Consumer<MethodEntry>() {
            @Override
            public void accept(MethodEntry methodEntry) {
                emitMethodSummary(summaryTable, methodEntry);
            }
        };
    }

    private Consumer<? super ClassEntry> emitClassSummary(final PdfPTable summaryTable) {
        return new Consumer<ClassEntry>() {
            @Override
            public void accept(ClassEntry classEntry) {
                emitClassSummary(summaryTable, classEntry);
            }
        };
    }

    private Consumer<? super PackageEntry> emitPackageSummary(final PdfPTable summaryTable) {
        return new Consumer<PackageEntry>() {
            @Override
            public void accept(PackageEntry packageEntry) {
                emitPackageSummary(summaryTable, packageEntry);
            }
        };
    }

    public void close() {
        try {
            if (document != null) {
                document.close();
            }

            if (fileOutputStream != null) {
                fileOutputStream.close();
            }

            log.info("Report generated {}", fileDst.getAbsolutePath());
        } catch (Exception e) {
            throw new RuntimeException("Error while flushing report to disk", e);
        }
    }
}
