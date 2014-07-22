package cucumber.contrib.grammar.pdf;

import com.google.common.collect.Sets;
import com.google.gson.GsonBuilder;
import com.itextpdf.text.DocumentException;
import com.thoughtworks.qdox.model.JavaPackage;
import cucumber.contrib.grammar.GrammarConsolidation;
import cucumber.contrib.grammar.java.Grammar;
import cucumber.contrib.grammar.java.GrammarParser;
import cucumber.contrib.grammar.java.GrammarParserListenerAdapter;
import cucumber.contrib.grammar.step.FeatureParser;
import cucumber.contrib.grammar.step.Features;
import cucumber.contrib.util.PropertiesLoader;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

public class GrammarPdfReportTest {

    private String basedir;

    @Before
    public void setUp() throws IOException {
        Properties properties = new PropertiesLoader().load(getClass().getResource("/test-settings.properties"));
        basedir = properties.getProperty("basedir");
    }

    @Test
    public void usecase() throws FileNotFoundException, DocumentException {
        Grammar grammar = generate();

        String json = new GsonBuilder().setPrettyPrinting().create().toJson(grammar);
        System.out.println("GrammarPdfReportTest.usecase(" + json + ")");

        GrammarPdfReport report = new GrammarPdfReport(new Configuration());
        report.init(new File(basedir, getClass().getSimpleName() + "_usecase.pdf"));
        report.emit(grammar);
        report.close();
    }

    private Grammar generate() {
        File stepTree = new File(basedir, "src/test/resources/cucumber/contrib/grammar/step");
        File javaTree = new File(basedir, "src/test/java/cucumber/contrib/grammar/pdf/stepdefs");

        Features features = new FeatureParser().usingSourceDirectory(stepTree).process();
        Grammar grammar = new GrammarParser()
                .usingSourceDirectory(javaTree)
                .usingListener(new GrammarParserListenerAdapter() {
                    private Set<String> visitedPkgs = Sets.newHashSet();
                    @Override
                    public void enteringPackage(JavaPackage pkg) {
                        if(!visitedPkgs.add(pkg.getName()))
                            throw new IllegalStateException("Package already visited!");
                        System.out.println("enteringPackage: pkg = [" + pkg + "]");
                    }

                    @Override
                    public void exitingPackage(JavaPackage pkg) {
                        System.out.println("exitingPackage: pkg = [" + pkg + "]");
                    }
                })
                .process();

        new GrammarConsolidation(grammar, features).consolidate();
        return grammar;
    }
}