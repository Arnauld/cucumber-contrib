package sample.coffeemachine;

import cucumber.api.junit.Cucumber;
import cucumber.contrib.formatter.pdf.PdfFormatter;

import org.junit.runner.RunWith;
import java.io.File;
import java.io.FileNotFoundException;

@RunWith(Cucumber.class)
@Cucumber.Options(
        format = {"sample.coffeemachine.RunCucumberTest$ConfiguredFormatter:target/coffeemachine"}
)
public class RunCucumberTest {

    public static class ConfiguredFormatter extends PdfFormatter {
        public ConfiguredFormatter(File reportDir) throws FileNotFoundException {
            super(reportDir, defaultConfiguration()
                    .withMetaInformationsResources(RunCucumberTest.class, "00-meta.properties")
                    // override some of the previous meta infomations
                    .withReportFilename("coffee-machine-specs.pdf")
                    .withAuthor("Arnauld")
                    .withTitle("Coffee Machine Specification")
                    .withPreambuleResource(RunCucumberTest.class, "00-preambule.md")
                    .withDocumentMargin(10, 40)
                    .withDescriptionMargin(0, 0, 5, 5)
                    .withScenarioMargin(0, 0, 2, 5)
                    .displayUri(false)
                    .displayTags(false)
            );
        }
    }
}
