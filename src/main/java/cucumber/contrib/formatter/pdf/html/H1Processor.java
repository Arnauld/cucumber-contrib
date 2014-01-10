package cucumber.contrib.formatter.pdf.html;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.Tag;
import com.itextpdf.tool.xml.WorkerContext;
import com.itextpdf.tool.xml.html.AbstractTagProcessor;
import cucumber.contrib.formatter.BricABrac;
import cucumber.contrib.formatter.pdf.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
* Created with IntelliJ IDEA.
* User: aloyer060113
* Date: 1/9/14
* Time: 3:39 PM
* To change this template use File | Settings | File Templates.
*/
public class H1Processor extends AbstractTagProcessor {
    private Configuration configuration;

    public H1Processor(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public List<Element> content(WorkerContext ctx, Tag tag, final String content) {
        ArrayList<Element> list = new ArrayList<Element>(1);
        list.add(new WritableDirectElement() {

            public void write(final PdfWriter writer, final Document doc) throws DocumentException {
                Chapter chapter;
                if (BricABrac.isBlank(content)) {
                    chapter = new Chapter(new Paragraph(" "), 0);
                    chapter.setNumberDepth(0);
                }
                else {
                    chapter = configuration.createTitledChapter(content);
                }
                doc.add(chapter);
            }
        });
        return list;
    }
}
