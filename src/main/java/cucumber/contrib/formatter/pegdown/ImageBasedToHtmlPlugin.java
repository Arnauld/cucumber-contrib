package cucumber.contrib.formatter.pegdown;

import cucumber.contrib.formatter.util.BricABrac;
import org.pegdown.Printer;
import org.pegdown.ast.Node;
import org.pegdown.ast.Visitor;
import org.pegdown.plugins.ToHtmlSerializerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public abstract class ImageBasedToHtmlPlugin implements ToHtmlSerializerPlugin {

    private Logger log = LoggerFactory.getLogger(ImageBasedToHtmlPlugin.class);

    private final File generationDirectory;

    public ImageBasedToHtmlPlugin(File generationDirectory) {
        this.generationDirectory = generationDirectory;
    }

    @Override
    public boolean visit(Node node, Visitor visitor, Printer printer) {
        log.debug("visiting node '" + node + "'");
        if (!(node instanceof NamedBlockNode))
            return false;

        NamedBlockNode named = (NamedBlockNode) node;
        if (!acceptsNode(named))
            return false;

        File imageFile = null;
        try {
            imageFile = getOrGenerateImage(named);

            if (imageFile != null) {
                printer
                        .println()
                        .print("<p>").indent(+1).println();
                printImageHtml(printer, imageFile);
                printer
                        .println().indent(-1).print("</p>")
                        .println();
            } else {
                // fallback on raw content...
                printer.println()
                        .print("<pre>").indent(+1).println()
                        .print(named.getBody())
                        .println().indent(-1).print("</pre>")
                        .println();
            }
        } catch (Exception e) {
            log.error("Failed to generate image", e);
        }
        return true;
    }

    protected void printImageHtml(Printer printer, File imageFile) throws MalformedURLException {
        printer.print("<").print(getHtmlTagName()).print(" src=\"").print(imageFile.toURI().toURL().toString()).print("\" />");
    }

    protected String getHtmlTagName() {
        return "img";
    }

    protected String getImageExtension() {
        return "png";
    }

    protected abstract boolean acceptsNode(NamedBlockNode named);

    private File getOrGenerateImage(NamedBlockNode named) {
        File imageFile;
        if (!generationDirectory.exists())
            generationDirectory.mkdirs();

        String id = BricABrac.md5Hex(named.getBody());

        imageFile = new File(generationDirectory, id + "." + getImageExtension());
        if (shouldRegenerate() || !imageFile.exists()) {
            try {
                generateImage(imageFile, named);
            } catch (Exception e) {
                return null;
            }
        } else {
            log.debug("Image file already generated {}", imageFile.getAbsolutePath());
        }
        return imageFile;
    }

    protected boolean shouldRegenerate() {
        return false;
    }

    protected abstract void generateImage(File pngFile, NamedBlockNode named) throws Exception;
}
