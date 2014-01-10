package cucumber.contrib.formatter.pdf.html;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.tool.xml.Tag;
import com.itextpdf.tool.xml.WorkerContext;
import com.itextpdf.tool.xml.html.table.Table;
import cucumber.contrib.formatter.pdf.Configuration;

import java.util.List;

/**
 *
 */
public class TableProcessor extends Table {

    private Configuration configuration;

    public TableProcessor(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public List<Element> end(WorkerContext ctx, Tag tag, List<Element> currentContent) {
        List<Element> elements = super.end(ctx, tag, currentContent);
        for(Element element : elements) {
            if(element instanceof PdfPTable) {
                adjustTable((PdfPTable)element);
            }
            System.out.println("TableProcessor.end~~~~> " + element);
        }
        return elements;
    }

    private void adjustTable(PdfPTable table) {
        int numberOfColumns = table.getNumberOfColumns();
        int[] widths = new int[numberOfColumns];
        for(int i=0; i<numberOfColumns; i++)
            widths[i] = 1;
        table.setWidthPercentage(100);
        try {
            table.setWidths(widths);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
