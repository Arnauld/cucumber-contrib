package cucumber.contrib.formatter.pdf.html;

import com.itextpdf.text.Chapter;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Section;
import com.itextpdf.tool.xml.Tag;
import com.itextpdf.tool.xml.WorkerContext;
import com.itextpdf.tool.xml.html.Header;
import cucumber.contrib.formatter.pdf.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class HeaderProcessor extends Header {
    private final Configuration configuration;
    private final int level;

    public HeaderProcessor(Configuration configuration, int level) {
        this.configuration = configuration;
        this.level = level;
    }

    @Override
    public List<Element> end(WorkerContext ctx, Tag tag, List<Element> currentContent) {
        List<Element> elements = super.end(ctx, tag, currentContent);

        Paragraph paragraph = createParagraph();
        paragraph.addAll(elements);
        String content = paragraph.getContent();

        if (level == 1) {
            final Chapter chapter = configuration.createTitledChapter(content);
            return new ArrayList<Element>(1) {{
                add(chapter);
            }};
        }
        return elements;
    }
}
