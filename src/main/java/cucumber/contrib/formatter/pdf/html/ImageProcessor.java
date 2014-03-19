package cucumber.contrib.formatter.pdf.html;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.tool.xml.NoCustomContextException;
import com.itextpdf.tool.xml.Tag;
import com.itextpdf.tool.xml.WorkerContext;
import com.itextpdf.tool.xml.css.CssUtils;
import com.itextpdf.tool.xml.exceptions.RuntimeWorkerException;
import com.itextpdf.tool.xml.html.HTML;
import com.itextpdf.tool.xml.html.Image;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Modified version of {@link Image} that accepts arbitrary {@link URL} for <code>src</code> attribute.
 *
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class ImageProcessor extends Image {

    private final CssUtils utils = CssUtils.getInstance();
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(ImageProcessor.class);

    public List<Element> end(final WorkerContext ctx, final Tag tag, final List<Element> currentContent) {
        Map<String, String> attributes = tag.getAttributes();

        String src = attributes.get(HTML.Attribute.SRC);
        if (isBlank(src)) {
            return new ArrayList<Element>(1);
        }

        src = src.trim();

        com.itextpdf.text.Image img = null;
        try {
            URL url = new URL(src);
            img = com.itextpdf.text.Image.getInstance(url);
        } catch (Exception e) {
            logger.debug("Unable to retrieve image from url {}, fallback on standard behavior", src);
        }

        if (null == img) {
            return super.end(ctx, tag, currentContent);
        }

        String width = attributes.get(HTML.Attribute.WIDTH);
        String height = attributes.get(HTML.Attribute.HEIGHT);
        if (width == null || height == null)
            img.setScaleToFitLineWhenOverflow(true);
        else
            img.setScaleToFitLineWhenOverflow(false);

        float widthInPoints = utils.parsePxInCmMmPcToPt(width);
        float heightInPoints = utils.parsePxInCmMmPcToPt(height);
        if (widthInPoints > 0 && heightInPoints > 0) {
            img.scaleAbsolute(widthInPoints, heightInPoints);
        } else if (widthInPoints > 0) {
            heightInPoints = img.getHeight() * widthInPoints / img.getWidth();
            img.scaleAbsolute(widthInPoints, heightInPoints);
        } else if (heightInPoints > 0) {
            widthInPoints = img.getWidth() * heightInPoints / img.getHeight();
            img.scaleAbsolute(widthInPoints, heightInPoints);
        }
        try {
            HtmlPipelineContext htmlPipelineContext = getHtmlPipelineContext(ctx);

            ArrayList<Element> l = new ArrayList<Element>(1);
            l.add(getCssAppliers().apply(new Chunk((com.itextpdf.text.Image) getCssAppliers().apply(img, tag, htmlPipelineContext), 0, 0, true), tag, htmlPipelineContext));
            return l;
        } catch (NoCustomContextException e) {
            throw new RuntimeWorkerException(e);
        }
    }

    private static boolean isBlank(String src) {
        return src == null || src.trim().isEmpty();
    }
}
