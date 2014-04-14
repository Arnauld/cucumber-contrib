package cucumber.contrib.formatter.pegdown;

import cucumber.contrib.formatter.util.BricABrac;
import org.apache.commons.io.IOUtils;
import org.pegdown.Printer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class GralToHtmlPlugin extends ImageBasedToHtmlPlugin {

    private Logger log = LoggerFactory.getLogger(GralToHtmlPlugin.class);

    public GralToHtmlPlugin(File generationDirectory) {
        super(generationDirectory);
    }

    @Override
    protected boolean shouldRegenerate() {
        return true;
    }

    @Override
    protected boolean acceptsNode(NamedBlockNode named) {
        return BricABrac.isOneOfIgnoringCase(named.beginTag(), "chart", "gral");
    }

    @Override
    protected void generateImage(File outFile, NamedBlockNode named) throws Exception {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(outFile);
            String settings = named.getBody();
            IOUtils.write(settings, out);
        } finally {
            IOUtils.closeQuietly(out);
        }
        log.info("Chart plot generated as Gral files {}", outFile.getAbsolutePath());
    }

    @Override
    protected String getImageExtension() {
        return "gral";
    }

    @Override
    protected String getHtmlTagName() {
        return "gral";
    }
}
