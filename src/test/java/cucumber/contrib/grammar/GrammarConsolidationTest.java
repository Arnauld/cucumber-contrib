package cucumber.contrib.grammar;

import com.google.gson.GsonBuilder;
import cucumber.contrib.grammar.java.Grammar;
import cucumber.contrib.grammar.java.GrammarParser;
import cucumber.contrib.grammar.step.FeatureParser;
import cucumber.contrib.grammar.step.Features;
import cucumber.contrib.util.PropertiesLoader;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class GrammarConsolidationTest {

    private static String basedir;
    //
    private File stepTree;
    private File javaTree;

    @BeforeClass
    public static void initProperties() throws IOException {
        Properties properties = new PropertiesLoader().load(GrammarConsolidationTest.class.getResource("/test-settings.properties"));
        basedir = properties.getProperty("basedir");
    }

    @Before
    public void setUp() throws IOException {
        stepTree = new File(basedir, "src/test/resources/cucumber/contrib/grammar/step");
        javaTree = new File(basedir, "src/test/java/cucumber/contrib/grammar/java/stepdefs");
    }

    @Test
    public void usecase() {
        // Given
        FeatureParser featureParser = new FeatureParser().usingSourceDirectory(stepTree);
        Features features = featureParser.process();

        GrammarParser grammarParser = new GrammarParser().usingSourceDirectory(javaTree);
        Grammar grammar = grammarParser.process();

        GrammarConsolidation consolidation = new GrammarConsolidation(grammar, features);

        //When
        consolidation.consolidate();

        String json = new GsonBuilder().setPrettyPrinting().create().toJson(grammar);
        System.out.println("GrammarConsolidationTest.usecase:::\n" + json);

    }
}