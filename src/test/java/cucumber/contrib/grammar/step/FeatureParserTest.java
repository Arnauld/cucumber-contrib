package cucumber.contrib.grammar.step;

import com.google.gson.GsonBuilder;
import cucumber.contrib.util.PropertiesLoader;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
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