package cucumber.contrib.formatter.pegdown;

import cucumber.contrib.formatter.util.BricABrac;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stathissideris.ascii2image.core.CommandLineConverter;

import java.io.File;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class AsciiDiagToHtmlPlugin extends ImageBasedToHtmlPlugin {

    private Logger log = LoggerFactory.getLogger(AsciiDiagToHtmlPlugin.class);

    public AsciiDiagToHtmlPlugin(File generationDirectory) {
        super(generationDirectory);
    }

    @Override
    protected boolean acceptsNode(NamedBlockNode named) {
        return BricABrac.isOneOfIgnoringCase(named.beginTag(), "asciidiag", "ditaa");
    }

    @Override
    protected void generateImage(File pngFile, NamedBlockNode named) throws Exception {
        File diagFile = new File(pngFile.getParentFile(), pngFile.getName() + ".txt");
        FileUtils.write(diagFile, named.getBody(), "UTF8");

        log.debug("About to generated ascii diagram (ditaa) as PNG files {}", pngFile.getAbsolutePath());
        CommandLineConverter.main(new String[]{
                "-v",//
                "-encoding", "UTF8",//
                diagFile.getAbsolutePath(), //
                pngFile.getAbsolutePath()
        });
        log.info("Ascii diagram generated as PNG files {}", pngFile.getAbsolutePath());
    }

}
