package cucumber.contrib.formatter.pdf.html;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.tool.xml.Tag;
import com.itextpdf.tool.xml.WorkerContext;
import com.itextpdf.tool.xml.html.pdfelement.HtmlCell;
import com.itextpdf.tool.xml.html.table.TableData;
import cucumber.contrib.formatter.pdf.Configuration;

import java.util.Collections;
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
        for (Element e : notNull(elements)) {
            if (e instanceof HtmlCell) {
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

        for (Element element : notNull(cell.getCompositeElements())) {
            if (element instanceof Paragraph) {
                adjustParagraphFont((Paragraph) element);
            }
        }
    }

    private void adjustParagraphFont(Paragraph element) {
        Font font = configuration.tableContentFont();
        element.setFont(font);

        for (Chunk c : notNull(element.getChunks())) {
            adjustChunkFont(c);
        }
    }

    private void adjustChunkFont(Chunk element) {
        Font font = configuration.tableContentFont();
        element.setFont(font);
    }

    private static <T> List<T> notNull(List<T> elements) {
        if (elements == null)
            return Collections.emptyList();
        return elements;
    }

}
