package cucumber.contrib.formatter.html;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.tool.xml.ElementList;
import cucumber.contrib.formatter.pdf.Configuration;
import cucumber.contrib.formatter.pdf.PdfEmitter;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class HtmlIntegTest {

    private PdfEmitter pdfEmitter;
    private Configuration configuration;

    @Before
    public void setUp() {
        configuration = new Configuration()
                            .withTableHeaderFont(FontFactory.getFont("Arial", 9, Font.BOLD, BaseColor.BLACK))
                            .withTableContentFont(FontFactory.getFont("Arial", 9, Font.NORMAL, BaseColor.BLACK));
        pdfEmitter = new PdfEmitter(configuration);
    }

    @Test
    public void simpleTable() throws IOException, DocumentException {
        ElementList elementList = new ElementList();
        configuration.appendMarkdownContent(elementList, IOUtils.toString(HtmlIntegTest.class.getResourceAsStream("preambule.md")));

        pdfEmitter.init(new File(getClass().getSimpleName() + ".pdf"));
        Document document = pdfEmitter.getDocument();

        for(Element element : elementList) {
            System.out.println("HtmlIntegTest.simpleTable:: " + element);
            if(element instanceof PdfPTable) {
                ((PdfPTable)element).setTotalWidth(document.right() - document.left());
                ((PdfPTable)element).setWidths(new int[] {1,1,1,1,1});
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
