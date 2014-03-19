package cucumber.contrib.formatter.pdf.html;

import cucumber.contrib.formatter.pegdown.NamedBlockPluginNode;
import org.apache.commons.io.FileUtils;
import org.pegdown.Printer;
import org.pegdown.ast.Node;
import org.pegdown.ast.Visitor;
import org.pegdown.plugins.ToHtmlSerializerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stathissideris.ascii2image.core.CommandLineConverter;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class AsciiDiagToHtmlPlugin implements ToHtmlSerializerPlugin {

    private Logger log = LoggerFactory.getLogger(AsciiDiagToHtmlPlugin.class);

    private final File generationDirectory;

    public AsciiDiagToHtmlPlugin(File generationDirectory) {
        this.generationDirectory = generationDirectory;
    }

    @Override
    public boolean visit(Node node, Visitor visitor, Printer printer) {
        log.debug("visiting node '" + node + "'");
        if (!(node instanceof NamedBlockPluginNode))
            return false;

        NamedBlockPluginNode named = (NamedBlockPluginNode) node;
        if (!"asciidiag".equalsIgnoreCase(named.beginTag()))
            return false;

        try {
            if (!generationDirectory.exists())
                generationDirectory.mkdirs();

            String id = identifier(named.getBody());

            File pngFile = new File(generationDirectory, id + ".png");
            if (!pngFile.exists()) {
                File diagFile = new File(generationDirectory, id + ".txt");
                FileUtils.write(diagFile, named.getBody(), "UTF8");

                log.debug("About to generated ascii diagram as PNG files {}", pngFile.getAbsolutePath());
                CommandLineConverter.main(new String[]{
                        "-v",//
                        "-encoding", "UTF8",//
                        diagFile.getAbsolutePath(), //
                        pngFile.getAbsolutePath()
                });
                log.info("Ascii diagram generated as PNG files {}", pngFile.getAbsolutePath());
            } else {
                log.debug("PNG file already generated {}", pngFile.getAbsolutePath());
            }

            printer
                    .println()
                    .print("<p>").indent(+1).println()
                    .print("<img src=\"").print(pngFile.toURI().toURL().toString()).print("\" />")
                    .println().indent(-1).print("</p>")
                    .println();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    private static String identifier(String content) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            byte[] digest = md5.digest(content.getBytes());
            return toHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }
}
