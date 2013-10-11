package cucumber.contrib.formatter.pdf;

import static com.google.common.base.Strings.isNullOrEmpty;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.FontProvider;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.WritableDirectElement;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.ElementHandler;
import com.itextpdf.tool.xml.ElementList;
import com.itextpdf.tool.xml.NoCustomContextException;
import com.itextpdf.tool.xml.Pipeline;
import com.itextpdf.tool.xml.Tag;
import com.itextpdf.tool.xml.WorkerContext;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.CssFilesImpl;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.exceptions.RuntimeWorkerException;
import com.itextpdf.tool.xml.html.AbstractTagProcessor;
import com.itextpdf.tool.xml.html.Div;
import com.itextpdf.tool.xml.html.HTML;
import com.itextpdf.tool.xml.html.NonSanitizedTag;
import com.itextpdf.tool.xml.html.ParaGraph;
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

import org.pegdown.PegDownProcessor;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class MarkdownEmitter {

    private PegDownProcessor markdown;
    private Configuration configuration;

    public MarkdownEmitter(Configuration configuration) {
        this.configuration = configuration;
        markdown = new PegDownProcessor();
    }

    public List<Element> markdownToElements(String markdownText) {
        try {
            ElementList elementList = new ElementList();
            parseXHtml(elementList, formatHtmlAsReader(markdownText));
            return elementList;
        }
        catch (IOException e) {
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
        tpf.addProcessor(new com.itextpdf.tool.xml.html.Image(), "img");
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
