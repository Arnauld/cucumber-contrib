package cucumber.contrib.formatter.pdf;

import com.google.common.collect.Lists;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class TableOfContents extends PdfPageEventHelper {
    private final PageNumber pageNumber;
    private List<Entry> entries;
    private int currentPage;
    private int anchorId = 0;

    public TableOfContents(PageNumber pageNumber) {
        this.pageNumber = pageNumber;
        this.entries = Lists.newArrayList();
    }

    public void addEntry(String text, int level, String page, String anchorDst) {
        entries.add(new Entry(text, level, page, anchorDst));
    }

    public List<Entry> getEntries() {
        return entries;
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        pageNumber.notifyPageChange(currentPage++);
    }

    /**
     * Called when a Section is written.
     * <p/>
     * <CODE>position</CODE> will hold the height at which the
     * section will be written to.
     *
     * @param writer            the <CODE>PdfWriter</CODE> for this document
     * @param document          the document
     * @param paragraphPosition the position the section will be written to
     * @param depth             the number depth of the Section
     * @param title             the title of the section
     */
    @Override
    public void onSection(PdfWriter writer, Document document, float paragraphPosition, int depth, Paragraph title) {
        String anchorDst = "w" + (anchorId++);
        defineLocalDestinationOnFirstChunk(title, anchorDst);
        addEntry(title.getContent(), 2, pageNumber.formatPageNumber(), anchorDst);
    }

    /**
     * Called when a Chapter is written.
     * <p/>
     * <CODE>position</CODE> will hold the height at which the
     * chapter will be written to.
     *
     * @param writer            the <CODE>PdfWriter</CODE> for this document
     * @param document          the document
     * @param paragraphPosition the position the chapter will be written to
     * @param title             the title of the Chapter
     */
    @Override
    public void onChapter(PdfWriter writer, Document document, float paragraphPosition, Paragraph title) {
        String anchorDst = "w" + (anchorId++);
        defineLocalDestinationOnFirstChunk(title, anchorDst);
        addEntry(title.getContent(), 1, pageNumber.formatPageNumber(), anchorDst);
    }

    private void defineLocalDestinationOnFirstChunk(Paragraph p, String anchor) {
        for(Element e : p) {
            if(e instanceof Chunk) {
                Chunk c = (Chunk)e;
                c.setLocalDestination(anchor);
                return;
            }
        }
        throw new IllegalStateException("No chunk found");
    }

    public static class Entry {
        private final String text;
        private final int level;
        private final String page;
        private final String anchorDst;

        public Entry(String text, int level, String page, String anchorDst) {
            this.text = text;
            this.level = level;
            this.page = page;
            this.anchorDst = anchorDst;
        }

        public String getAnchorDst() {
            return anchorDst;
        }

        public String getText() {
            return text;
        }

        public int getLevel() {
            return level;
        }

        public String getPage() {
            return page;
        }
    }
}
