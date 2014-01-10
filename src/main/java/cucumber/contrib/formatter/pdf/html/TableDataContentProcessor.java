package cucumber.contrib.formatter.pdf.html;

import com.itextpdf.text.*;
import com.itextpdf.tool.xml.Tag;
import com.itextpdf.tool.xml.WorkerContext;
import com.itextpdf.tool.xml.html.pdfelement.HtmlCell;
import com.itextpdf.tool.xml.html.table.TableData;
import cucumber.contrib.formatter.pdf.Configuration;

import java.util.List;

/**
 *
 */
public class TableDataContentProcessor extends TableData {
    private Configuration configuration;

    public TableDataContentProcessor(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public List<Element> end(WorkerContext ctx, Tag tag, List<Element> currentContent) {
        List<Element> elements = super.end(ctx, tag, currentContent);
        for(Element e :elements) {
            if(e instanceof HtmlCell) {
                dressUp((HtmlCell) e);
            }
        }
        return elements;
    }

    private void dressUp(HtmlCell cell) {
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorder(Rectangle.TOP + Rectangle.BOTTOM + Rectangle.LEFT + Rectangle.RIGHT);
        cell.setBorderColor(BaseColor.BLACK);
        cell.setUseBorderPadding(true);

        for(Element element : cell.getCompositeElements()) {
            if(element instanceof Paragraph) {
                adjustParagraphFont((Paragraph) element);
            }
        }
    }

    private void adjustParagraphFont(Paragraph element) {
        Font font = configuration.tableContentFont();
        element.setFont(font);

        for(Chunk c : element.getChunks()) {
            adjustChunkFont(c);
        }
    }

    private void adjustChunkFont(Chunk element) {
        Font font = configuration.tableContentFont();
        element.setFont(font);
    }
}
