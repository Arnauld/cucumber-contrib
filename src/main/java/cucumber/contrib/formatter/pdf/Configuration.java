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

    public Font featureTitleFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD, new CMYKColor(0, 255, 255, 17));
    }

    public Font scenarioTitleFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD, new CMYKColor(0, 255, 255, 17));
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

    public Font tableHeaderFont() {
        return FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD, BaseColor.WHITE);
    }

    public Font tableDataFont() {
        return stepDefaultFont();
    }

    public BaseColor tableHeaderBackground() {
        return CYAN;
    }

    public BaseColor tableRowAlternateBackground() {
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
        chapter.setNumberDepth(0);
        return chapter;
    }
}
