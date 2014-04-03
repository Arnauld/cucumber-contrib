package cucumber.contrib.formatter.pegdown;

import cucumber.contrib.formatter.util.BricABrac;
import net.sourceforge.plantuml.SourceStringReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class PlantUMLToHtmlPlugin extends ImageBasedToHtmlPlugin {

    private Logger log = LoggerFactory.getLogger(PlantUMLToHtmlPlugin.class);

    public PlantUMLToHtmlPlugin(File generationDirectory) {
        super(generationDirectory);
    }

    @Override
    protected boolean acceptsNode(NamedBlockNode named) {
        return BricABrac.isOneOfIgnoringCase(named.beginTag(), "plantuml", "uml");
    }

    @Override
    protected void generateImage(File pngFile, NamedBlockNode named) throws Exception {
        log.debug("About to generated PlantUML diagram as PNG files {}", pngFile.getAbsolutePath());

        OutputStream png = new FileOutputStream(pngFile);
        String source = named.getBody();

        SourceStringReader reader = new SourceStringReader(source);
        String desc = reader.generateImage(png);

        log.info("PlantUML diagram generated as PNG files {} (description: {})", pngFile.getAbsolutePath(), desc);
    }
}
