package cucumber.contrib.formatter.pdf;

import com.itextpdf.text.Element;
import com.itextpdf.tool.xml.*;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.CssFileImpl;
import com.itextpdf.tool.xml.css.CssFilesImpl;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.exceptions.CssResolverException;
import com.itextpdf.tool.xml.html.TagProcessorFactory;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.ElementHandlerPipeline;
import com.itextpdf.tool.xml.pipeline.html.AbstractImageProvider;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import cucumber.contrib.formatter.BricABrac;
import cucumber.contrib.formatter.FormatterException;
import cucumber.contrib.formatter.pdf.html.ImageProcessor;
import cucumber.contrib.formatter.pdf.html.TableDataContentProcessor;
import cucumber.contrib.formatter.pdf.html.TableDataHeaderProcessor;
import cucumber.contrib.formatter.pegdown.Visitors;
import org.pegdown.LinkRenderer;
import org.pegdown.PegDownProcessor;
import org.pegdown.ToHtmlSerializer;
import org.pegdown.ast.RootNode;
import org.pegdown.plugins.ToHtmlSerializerPlugin;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

public class MarkdownEmitter {

    private Configuration configuration;

    public MarkdownEmitter(Configuration configuration) {
        this.configuration = configuration;
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
        cssFiles.add(getHtmlCSS());
        StyleAttrCSSResolver cssResolver = new StyleAttrCSSResolver(cssFiles) {
            @Override
            public void resolveStyles(Tag t) {
                configuration.resolveDefaultStyles(t);
                super.resolveStyles(t);
            }

            @Override
            public CSSResolver clear() throws CssResolverException {
                // prevent css files from being been removed
                return this;
            }
        };
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
                                new ElementHandlerPipeline(d, null)));
        XMLWorker worker = new XMLWorker(pipeline, true);
        XMLParser p = new XMLParser();
        p.addListener(worker);
        p.parse(in);
    }

    private CssFile getDefaultCSS() {
        return XMLWorkerHelper.getInstance().getDefaultCSS();
    }

    private CssFile getHtmlCSS() {
        CssFileImpl cssFile = new CssFileImpl();
        configuration.fillDefaultHtmlCSS(cssFile);
        return cssFile;
    }

    private TagProcessorFactory getDefaultTagProcessorFactory() {
        TagProcessorFactory tpf = Tags.getHtmlTagProcessorFactory();
        //tpf.addProcessor(new H1Processor(configuration), "h1");
        //tpf.addProcessor(new H2Processor(configuration), "h2");
        tpf.addProcessor(new ImageProcessor(), "img");
        tpf.addProcessor(new TableDataHeaderProcessor(configuration), "th");
        tpf.addProcessor(new TableDataContentProcessor(configuration), "td");
        return tpf;
    }

    StringReader formatHtmlAsReader(String text) {
        String html = formatHtml(text);
        return new StringReader(html);
    }

    private String formatHtml(String text) {
        if (BricABrac.isBlank(text)) {
            return "";
        }
        PegDownProcessor markdownProcessor = configuration.getMarkdownProcessor();
        RootNode astRoot = markdownProcessor.parseMarkdown(text.toCharArray());

        //astRoot.accept(Visitors.dump());

        List<ToHtmlSerializerPlugin> htmlPlugins = configuration.htmlSerializerPlugins();
        ToHtmlSerializer htmlSerializer = new ToHtmlSerializer(new LinkRenderer(), htmlPlugins);
        return htmlSerializer.toHtml(astRoot);
    }

}
