package cucumber.contrib.formatter.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.*;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.CssFilesImpl;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.AbstractTagProcessor;
import com.itextpdf.tool.xml.html.TagProcessor;
import com.itextpdf.tool.xml.html.TagProcessorFactory;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.ElementHandlerPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import cucumber.contrib.formatter.FormatterException;
import org.pegdown.PegDownProcessor;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

public class MarkdownEmitter {

    private PegDownProcessor markdown;
    private Configuration configuration;

    public MarkdownEmitter(Configuration configuration) {
        this.configuration = configuration;
        markdown = new PegDownProcessor();
    }

    public List<Element> markdownToElements(String markdownText) {
        return markdownToElement1(markdownText);
    }

    private List<Element> markdownToElement1(String markdownText) {
        try {
            ElementList elementList = new ElementList();
            parseXHtml(elementList, formatHtmlAsReader(markdownText));
            return elementList;
        } catch (IOException e) {
            throw new FormatterException("Failed to transform markdown content", e);
        }
    }

    // copied from XMLWorkerHelper
    private  void parseXHtml(final ElementHandler d, final Reader in) throws IOException {
        CssFilesImpl cssFiles = new CssFilesImpl();
        cssFiles.add(getDefaultCSS());
        StyleAttrCSSResolver cssResolver = new StyleAttrCSSResolver(cssFiles);
        HtmlPipelineContext hpc = new HtmlPipelineContext(null);
        hpc.setAcceptUnknown(true).autoBookmark(true).setTagFactory(getDefaultTagProcessorFactory());
        Pipeline<?> pipeline = new CssResolverPipeline(cssResolver, new HtmlPipeline(hpc, new ElementHandlerPipeline(d,
                null)));
        XMLWorker worker = new XMLWorker(pipeline, true);
        XMLParser p = new XMLParser();
        p.addListener(worker);
        p.parse(in);
    }

    private CssFile getDefaultCSS() {
        return XMLWorkerHelper.getInstance().getDefaultCSS();
    }

    private TagProcessorFactory getDefaultTagProcessorFactory() {
        TagProcessorFactory tpf = Tags.getHtmlTagProcessorFactory();
        tpf.addProcessor(new H1Processor(configuration), "h1");
        return tpf;
    }

    private static class H1Processor extends AbstractTagProcessor {
        private H1Processor(Configuration configuration) {
            this.configuration = configuration;
        }

        private Configuration configuration;

        @Override
        public List<Element> content(WorkerContext ctx, Tag tag, final String content) {
            ArrayList<Element> list = new ArrayList<Element>(1);
            list.add(new WritableDirectElement() {

                public void write(final PdfWriter writer, final Document doc) throws DocumentException {
                    Paragraph featureTitle = new Paragraph(content, configuration.featureTitleFont());
                    Chapter title = new Chapter(featureTitle, 1);
                    title.setNumberDepth(0);
                    doc.add(title);
                }
            });
            return list;
        }
    }

    private List<Element> markdownToElements0(String markdownText) {
        HashMap<String, Object> providers = new HashMap<String, Object>();
        providers.put(HTMLWorker.FONT_PROVIDER, new HtmlFontFactory());
        // providers.put(HTMLWorker.IMG_PROVIDER, new ImageFactory());
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
        if (isNullOrEmpty(text)) {
            return "";
        }
        return markdown.markdownToHtml(text);
    }

    private static class HtmlFontFactory implements FontProvider {
        @Override
        public Font getFont(String fontname, String encoding, boolean embedded, float size, int style, BaseColor color) {
            return FontFactory.getFont(FontFactory.HELVETICA, 8, style, new CMYKColor(255, 255, 255, 17));
        }

        @Override
        public boolean isRegistered(String fontname) {
            return false;
        }
    }
}
