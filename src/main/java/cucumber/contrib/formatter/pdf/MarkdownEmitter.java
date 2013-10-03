package cucumber.contrib.formatter.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.CMYKColor;
import cucumber.contrib.formatter.BricABrac;
import org.pegdown.PegDownProcessor;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class MarkdownEmitter {


    private PegDownProcessor markdown;

    public MarkdownEmitter() {
        markdown = new PegDownProcessor();
    }

    public List<Element> markdownToElements(String markdownText) {
        HashMap<String, Object> providers = new HashMap<String, Object>();
        providers.put(HTMLWorker.FONT_PROVIDER, new HtmlFontFactory());
        //providers.put(HTMLWorker.IMG_PROVIDER, new ImageFactory());
        StyleSheet stylesheet = null;
        try {
            return HTMLWorker.parseToList(formatHtmlAsReader(markdownText), stylesheet, providers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    private StringReader formatHtmlAsReader(String text) {
        return new StringReader(formatHtml(text));
    }

    private String formatHtml(String text) {
        if (BricABrac.isEmpty(text)) {
            return "";
        }
        return markdown.markdownToHtml(text);
    }

    private static class HtmlFontFactory implements FontProvider {
        public Font getFont(String fontname,
                            String encoding, boolean embedded, float size,
                            int style, BaseColor color) {
            return FontFactory.getFont(FontFactory.HELVETICA, 8, style, new CMYKColor(255, 255, 255, 17));
        }

        public boolean isRegistered(String fontname) {
            return false;
        }
    }
}
