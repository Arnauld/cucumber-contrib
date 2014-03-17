package cucumber.contrib.formatter.pegdown;

import org.apache.commons.io.FileUtils;
import org.stathissideris.ascii2image.core.CommandLineConverter;

import java.io.File;
import java.io.IOException;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class AsciiDiagProcessor {

    public void process(String header, String diag) {

        try {
            File tempFile = File.createTempFile("ditaa", "txt");
            FileUtils.write(tempFile, diag, "UTF8");

            CommandLineConverter.main(new String[]{
                    "-v",//
                    "-encoding", "UTF8",//
                    tempFile.getAbsolutePath(), //
                    "/Users/arnauld/Projects/cucumber-jvm-series/cucumber-contrib/tmp/diag.png"
            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
