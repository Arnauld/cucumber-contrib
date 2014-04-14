package cucumber.contrib.formatter.pdf.html;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ImgTemplate;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.Tag;
import com.itextpdf.tool.xml.WorkerContext;
import com.itextpdf.tool.xml.html.AbstractTagProcessor;
import com.itextpdf.tool.xml.html.HTML;
import cucumber.contrib.formatter.renderer.ChartDescriptor;
import cucumber.contrib.formatter.renderer.ChartDescriptorParser;
import cucumber.contrib.formatter.renderer.GralRenderer;
import cucumber.contrib.util.Provider;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class GralProcessor extends AbstractTagProcessor {

    private Logger log = LoggerFactory.getLogger(GralProcessor.class);
    private final Provider<PdfWriter> writer;

    public GralProcessor(Provider<PdfWriter> writer) {
        this.writer = writer;
    }

    @Override
    public List<Element> end(WorkerContext ctx, Tag tag, List<Element> currentContent) {
        Map<String, String> attributes = tag.getAttributes();

        String src = attributes.get(HTML.Attribute.SRC);
        if (src == null || src.trim().isEmpty()) {
            return new ArrayList<Element>(1);
        }

        src = src.trim();

        InputStream stream = null;
        try {
            URL url = new URL(src);
            stream = url.openStream();
            String raw = IOUtils.toString(stream);

            log.debug("About to render GRAL image");
            return renderGral(raw);
        } catch (Exception e) {
            log.debug("Unable to retrieve image from url {}, fallback on standard behavior", src);
            return new ArrayList<Element>(1);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    private List<Element> renderGral(String raw) throws BadElementException, IOException {
        ChartDescriptor descriptor = new ChartDescriptorParser().parse(raw);

        PdfContentByte cb = writer.get().getDirectContent();
        float width = (float) descriptor.getWidth();
        float height = (float) descriptor.getHeight();

        PdfTemplate template = cb.createTemplate(width, height);
        Graphics2D g2 = new PdfGraphics2D(template, width, height);

        GralRenderer renderer = new GralRenderer();
        renderer.render(g2, descriptor);

        ArrayList<Element> elements = new ArrayList<Element>(1);
        elements.add(new ImgTemplate(template));
        return elements;
    }
}
