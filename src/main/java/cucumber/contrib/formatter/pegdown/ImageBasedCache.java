package cucumber.contrib.formatter.pegdown;

import cucumber.contrib.formatter.util.BricABrac;
import org.pegdown.Printer;
import org.pegdown.ast.Node;
import org.pegdown.ast.Visitor;
import org.pegdown.plugins.ToHtmlSerializerPlugin;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public abstract class ImageBasedCache implements ToHtmlSerializerPlugin {

    private Logger log = LoggerFactory.getLogger(ImageBasedCache.class);

    private final File generationDirectory;

    public ImageBasedCache(File generationDirectory) {
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

        File pngFile = null;
        try {
            pngFile = getOrGenerateImage(named);

            if (pngFile != null) {
                printer
                        .println()
                        .print("<p>").indent(+1).println()
                        .print("<img src=\"").print(pngFile.toURI().toURL().toString()).print("\" />")
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
        } catch (IOException e) {
            log.error("Failed to generate image", e);
        }
        return true;
    }

    protected abstract boolean acceptsNode(NamedBlockNode named);

    private File getOrGenerateImage(NamedBlockNode named) {
        File pngFile;
        if (!generationDirectory.exists())
            generationDirectory.mkdirs();

        String id = BricABrac.md5Hex(named.getBody());

        pngFile = new File(generationDirectory, id + ".png");
        if (!pngFile.exists()) {
            try {
                generateImage(pngFile, named);
            } catch (Exception e) {
                return null;
            }
        } else {
            log.debug("PNG file already generated {}", pngFile.getAbsolutePath());
        }
        return pngFile;
    }

    protected abstract void generateImage(File pngFile, NamedBlockNode named) throws Exception;
}
