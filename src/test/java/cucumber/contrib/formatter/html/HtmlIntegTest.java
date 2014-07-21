package cucumber.contrib.formatter.html;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.tool.xml.ElementList;
import cucumber.contrib.formatter.pdf.Configuration;
import cucumber.contrib.formatter.pdf.PdfEmitter;
import cucumber.contrib.util.PropertiesLoader;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 *
 */
public class HtmlIntegTest {
    public static final BaseColor PRIMARY_COLOR = new BaseColor(170, 135, 120);

    public static final Font MAIN_TITLE_FONT = FontFactory.getFont("Arial", 16, Font.NORMAL, PRIMARY_COLOR);
    public static final Font CHAPTER_TITLE_FONT = FontFactory.getFont("Arial", 13, Font.BOLD, PRIMARY_COLOR);
    public static final Font SECTION_TITLE_FONT = FontFactory.getFont("Arial", 12, Font.NORMAL, PRIMARY_COLOR);
    public static final Font SUB_TITLE_FONT = FontFactory.getFont("Arial", 20, Font.NORMAL, BaseColor.BLACK);
    public static final Font TABLE_HEADER_FONT = FontFactory.getFont("Arial", 9, Font.BOLD, BaseColor.BLACK);
    public static final Font TABLE_CONTENT_FONT = FontFactory.getFont("Arial", 9, Font.NORMAL, BaseColor.BLACK);

    private PdfEmitter pdfEmitter;
    private Configuration configuration;
    private String basedir;

    @Before
    public void setUp() throws IOException {
        Properties properties = new PropertiesLoader().load(getClass().getResource("/test-settings.properties"));
        basedir = properties.getProperty("basedir");
        configuration = new Configuration()
                .withChapterTitleFont(CHAPTER_TITLE_FONT)
                .withSectionTitleFont(SECTION_TITLE_FONT)
                .withTableHeaderFont(FontFactory.getFont("Arial", 9, Font.BOLD, BaseColor.BLACK))
                .withTableContentFont(FontFactory.getFont("Arial", 9, Font.NORMAL, BaseColor.BLACK));
        pdfEmitter = new PdfEmitter(configuration);
    }

    @Test
    public void simpleTable() throws IOException, DocumentException {
        ElementList elementList = new ElementList();
        configuration.appendMarkdownContent(elementList, IOUtils.toString(HtmlIntegTest.class.getResourceAsStream("preambule.md")));

        pdfEmitter.init(new File(basedir, getClass().getSimpleName() + ".pdf"));
        Document document = pdfEmitter.getDocument();

        for (Element element : elementList) {
            if (element instanceof PdfPTable) {
                ((PdfPTable) element).setTotalWidth(document.right() - document.left());
            }
            document.add(element);
        }


        PdfPTable table = new PdfPTable(3); // 3 columns.
        table.setWidthPercentage(100);
        PdfPCell cell1 = new PdfPCell(new Paragraph("Cell 1"));
        PdfPCell cell2 = new PdfPCell(new Paragraph("Cell 2"));
        PdfPCell cell3 = new PdfPCell(new Paragraph("Cell 3"));
        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        document.add(table);


        pdfEmitter.done();
    }
}
