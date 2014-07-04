package cucumber.contrib.grammar.step;

import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;
import cucumber.contrib.grammar.step.Feature;
import cucumber.contrib.util.PropertiesLoader;
import cucumber.runtime.FeatureBuilder;
import cucumber.runtime.model.CucumberFeature;
import gherkin.formatter.Formatter;
import gherkin.parser.Parser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class FeatureParserTest {

    private static String basedir;
    //
    private File sourceTree;

    @BeforeClass
    public static void initProperties() throws IOException {
        Properties properties = new PropertiesLoader().load(FeatureParserTest.class.getResource("/test-settings.properties"));
        basedir = properties.getProperty("basedir");
    }

    @Before
    public void setUp() throws IOException {
        // /Users/Arnauld/Projects/cucumber-contrib/src/test/resources/cucumber.contrib.grammar.step
        sourceTree = new File(basedir, "src/test/resources/cucumber/contrib/grammar/step");
    }

    @Test
    public void usecase() {
        // Given
        FeatureParser parser = new FeatureParser().usingSourceDirectory(sourceTree);

        //When
        Features features = parser.process();
        String s = new GsonBuilder().setPrettyPrinting().create().toJson(features);
        System.out.println("FeatureParserTest.usecase\n" + s);

    }

}