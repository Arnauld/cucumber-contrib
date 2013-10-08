package sample.coffeemachine;

import cucumber.contrib.formatter.pdf.Configuration;
import cucumber.contrib.formatter.pdf.PdfFormatter;
import org.junit.runner.RunWith;

import cucumber.api.junit.Cucumber;

import java.io.File;
import java.io.FileNotFoundException;

@RunWith(Cucumber.class)
@Cucumber.Options(
        format = {"sample.coffeemachine.RunCucumberTest$ConfiguredFormatter:target/coffeemachine"}
)
public class RunCucumberTest {

    public static class ConfiguredFormatter extends PdfFormatter {
        public ConfiguredFormatter(File reportDir) throws FileNotFoundException {
            super(reportDir);
        }

        @Override
        protected Configuration createConfiguration() {
            return super.createConfiguration().withPreambule(getClass().getResource("00-preambule.md"));
        }
    }
}
