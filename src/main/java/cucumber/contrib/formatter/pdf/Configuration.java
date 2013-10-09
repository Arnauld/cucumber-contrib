package cucumber.contrib.formatter.pdf;

import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.CMYKColor;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import static com.google.common.io.Resources.asByteSource;

/**
 *
 */
public class Configuration {

    private static final BaseColor CYAN = new BaseColor(0, 183, 255);
    private static final BaseColor VERY_LIGHT_GRAY = new BaseColor(215, 215, 215);

    // TODO extract to 'TemplateEngine' thus one can plug an other template engine
    private MarkdownEmitter markdownEmitter;
    private URL preambuleURL;


    private MarkdownEmitter getMarkdownEmitter() {
        if(markdownEmitter == null)
            markdownEmitter = new MarkdownEmitter(this);
        return markdownEmitter;
    }

    public Document createDocument() {
        return new Document(PageSize.A4, 50, 50, 50, 50);
    }

    public Font partFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 18, Font.ITALIC, new CMYKColor(0, 255, 255, 17));
    }

    public Font chapterTitleFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD, new CMYKColor(0, 255, 255, 17));
    }

    public Font sectionTitleFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD, new CMYKColor(0, 255, 255, 17));
    }

    public Font defaultFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, BaseColor.BLACK);
    }

    public Font defaultStrongFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, BaseColor.BLACK);
    }

    protected Font defaultMetaFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, new CMYKColor(0, 255, 255, 17));
    }


    public Font featureTitleFont() {
        return chapterTitleFont();
    }

    public Font scenarioTitleFont() {
        return sectionTitleFont();
    }

    public Font stepKeywordFont() {
        return FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD, new CMYKColor(255, 255, 0, 17));
    }

    public Font stepDefaultFont() {
        return FontFactory.getFont(FontFactory.COURIER, 8, Font.NORMAL, new CMYKColor(255, 255, 0, 17));
    }

    public Font tagsFont() {
        return FontFactory.getFont(FontFactory.COURIER, 8, Font.ITALIC, new CMYKColor(25, 255, 255, 17));
    }

    public Font stepDataTableHeaderFont() {
        return FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD, BaseColor.WHITE);
    }

    public Font stepDataTableContentFont() {
        return stepDefaultFont();
    }

    public BaseColor stepDataTableHeaderBackground() {
        return CYAN;
    }

    public BaseColor stepDataTableRowAlternateBackground() {
        return VERY_LIGHT_GRAY;
    }

    public String getPreambule() {
        return null;
    }

    public void writePreambule(Document document) throws DocumentException {
        String preambule = getPreambule();
        if(preambule == null && preambuleURL != null) {
            preambule = loadResource(preambuleURL);
            if(preambule == null) {
                return;
            }
        }

        Paragraph paragraph = new Paragraph();
        paragraph.addAll(getMarkdownEmitter().markdownToElements(preambule));
        document.add(paragraph);
    }

    private String loadResource(URL resource) {
        try {
            ByteSource source = asByteSource(resource);
            CharSource charSource = source.asCharSource(Charset.forName("UTF-8"));
            return charSource.read();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void appendMarkdownContent(final java.util.List<Element> elements, String markdownText) {
        elements.addAll(getMarkdownEmitter().markdownToElements(markdownText));
    }

    public Configuration withPreambule(URL preambuleURL) {
        this.preambuleURL = preambuleURL;
        return this;
    }

    private int chapterCount = 0;

    public Chapter createTitledChapter(String title) {
        Paragraph titleParagraph = new Paragraph(title, featureTitleFont());
        titleParagraph.setSpacingBefore(10f);
        titleParagraph.setSpacingAfter(10f);
        Chapter chapter = new Chapter(titleParagraph, ++chapterCount);
        chapter.setNumberDepth(2);
        return chapter;
    }

    public boolean shouldEmitSummary() {
        return true;
    }

}
