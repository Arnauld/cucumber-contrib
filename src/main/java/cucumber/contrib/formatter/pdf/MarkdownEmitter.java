package cucumber.contrib.formatter.pdf;

import com.itextpdf.text.Element;
import com.itextpdf.tool.xml.*;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.CssFilesImpl;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.TagProcessorFactory;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.ElementHandlerPipeline;
import com.itextpdf.tool.xml.pipeline.html.AbstractImageProvider;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import cucumber.contrib.formatter.BricABrac;
import cucumber.contrib.formatter.FormatterException;
import cucumber.contrib.formatter.pdf.html.H1Processor;
import cucumber.contrib.formatter.pdf.html.TableDataContentProcessor;
import cucumber.contrib.formatter.pdf.html.TableDataHeaderProcessor;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

public class MarkdownEmitter {

    private PegDownProcessor markdown;
    private Configuration configuration;

    public MarkdownEmitter(Configuration configuration) {
        this.configuration = configuration;
        this.markdown = new PegDownProcessor(Extensions.ALL);
    }

    public List<Element> markdownToElements(String markdownText) {
        try {
            ElementList elementList = new ElementList();
            parseXHtml(elementList, formatHtmlAsReader(markdownText));
            return elementList;
        } catch (IOException e) {
            throw new FormatterException("Failed to transform markdown content", e);
        }
    }

    // copied from XMLWorkerHelper
    private void parseXHtml(final ElementHandler d, final Reader in) throws IOException {
        CssFilesImpl cssFiles = new CssFilesImpl();
        cssFiles.add(getDefaultCSS());
        StyleAttrCSSResolver cssResolver = new StyleAttrCSSResolver(cssFiles);
        HtmlPipelineContext hpc = new HtmlPipelineContext(null);
        final String imageRootPath = configuration.getImageRootPath();
        if (imageRootPath != null) {
            hpc.setImageProvider(new AbstractImageProvider() {
                /**
                 * @return a rootpath to set before the src attribute
                 */
                @Override
                public String getImageRootPath() {
                    return imageRootPath;
                }
            });
        }
        hpc.setAcceptUnknown(true).autoBookmark(true).setTagFactory(getDefaultTagProcessorFactory());
        Pipeline<?> pipeline =
                new CssResolverPipeline(cssResolver,
                        new HtmlPipeline(hpc,
                                new ElementAdjusterPipeline(d, null)));
        XMLWorker worker = new XMLWorker(pipeline, true);
        XMLParser p = new XMLParser();
        p.addListener(worker);
        p.parse(in);
    }

    public static class ElementAdjusterPipeline extends ElementHandlerPipeline {

        public ElementAdjusterPipeline(ElementHandler handler, Pipeline next) {
            super(handler, next);
        }
    }

    private CssFile getDefaultCSS() {
        return XMLWorkerHelper.getInstance().getDefaultCSS();
    }

    private TagProcessorFactory getDefaultTagProcessorFactory() {
        TagProcessorFactory tpf = Tags.getHtmlTagProcessorFactory();
        tpf.addProcessor(new H1Processor(configuration), "h1");
        tpf.addProcessor(new com.itextpdf.tool.xml.html.Image(), "img");
        tpf.addProcessor(new TableDataHeaderProcessor(configuration), "th");
        tpf.addProcessor(new TableDataContentProcessor(configuration), "td");
        return tpf;
    }

    StringReader formatHtmlAsReader(String text) {
        return new StringReader(formatHtml(text));
    }

    private String formatHtml(String text) {
        if (BricABrac.isBlank(text)) {
            return "";
        }
        return markdown.markdownToHtml(text);
    }

}
