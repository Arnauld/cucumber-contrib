package cucumber.contrib.grammar.java;

import com.google.common.collect.FluentIterable;
import com.google.gson.GsonBuilder;
import cucumber.api.java.en.And;
import cucumber.api.java.en.But;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import cucumber.api.java.en_tx.Givenyall;
import cucumber.api.java.en_tx.Thenyall;
import cucumber.api.java.en_tx.Whenyall;
import cucumber.api.java.fr.*;
import cucumber.api.java.it.Quando;
import cucumber.contrib.util.PropertiesLoader;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static cucumber.contrib.grammar.java.GrammarParser.STEP_KEYWORD_QUALIFIED_NAME;
import static org.fest.assertions.Assertions.assertThat;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class GrammarParserTest {

    private static String basedir;
    //
    private GrammarParser parser;
    private File sourceTree;

    @BeforeClass
    public static void initProperties() throws IOException {
        Properties properties = new PropertiesLoader().load(GrammarParserTest.class.getResource("/test-settings.properties"));
        basedir = properties.getProperty("basedir");
    }

    @Before
    public void setUp() throws IOException {
        parser = new GrammarParser();
        sourceTree = new File(basedir, "src/test/java/cucumber/contrib/grammar/java/stepdefs");
    }

    @Test
    public void qualified_step_pattern_should_match_regular_steps__en() {
        assertThat(Given.class.getName()).matches(STEP_KEYWORD_QUALIFIED_NAME.pattern());
        assertThat(When.class.getName()).matches(STEP_KEYWORD_QUALIFIED_NAME.pattern());
        assertThat(Then.class.getName()).matches(STEP_KEYWORD_QUALIFIED_NAME.pattern());
        assertThat(And.class.getName()).matches(STEP_KEYWORD_QUALIFIED_NAME.pattern());
        assertThat(But.class.getName()).matches(STEP_KEYWORD_QUALIFIED_NAME.pattern());
    }

    @Test
    public void qualified_step_pattern_should_match_regular_steps__en_tx() {
        assertThat(Givenyall.class.getName()).matches(STEP_KEYWORD_QUALIFIED_NAME.pattern());
        assertThat(Whenyall.class.getName()).matches(STEP_KEYWORD_QUALIFIED_NAME.pattern());
        assertThat(Thenyall.class.getName()).matches(STEP_KEYWORD_QUALIFIED_NAME.pattern());
    }

    @Test
    public void qualified_step_pattern_should_match_regular_steps__fr_it_etc() {
        assertThat(Etantdonné.class.getName()).matches(STEP_KEYWORD_QUALIFIED_NAME.pattern());
        assertThat(Etantdonnée.class.getName()).matches(STEP_KEYWORD_QUALIFIED_NAME.pattern());
        assertThat(Etantdonnées.class.getName()).matches(STEP_KEYWORD_QUALIFIED_NAME.pattern());
        assertThat(Quand.class.getName()).matches(STEP_KEYWORD_QUALIFIED_NAME.pattern());
        assertThat(Alors.class.getName()).matches(STEP_KEYWORD_QUALIFIED_NAME.pattern());
        assertThat(Et.class.getName()).matches(STEP_KEYWORD_QUALIFIED_NAME.pattern());
        assertThat(Mais.class.getName()).matches(STEP_KEYWORD_QUALIFIED_NAME.pattern());

        assertThat(Quando.class.getName()).matches(STEP_KEYWORD_QUALIFIED_NAME.pattern());
    }

    @Test
    public void parser_should_invoke_listener_during_parsing() {
        // Given
        GrammarParserStatisticsListener listener = new GrammarParserStatisticsListener();
        parser.usingSourceDirectory(sourceTree)
                .usingListener(listener);

        //When
        parser.process();

        //Then
        assertThat(listener.numberOfPackagesParsed()).isEqualTo(1);
        assertThat(listener.numberOfClassesParsed()).isEqualTo(1);
        assertThat(listener.numberOfMethodsParsed()).isEqualTo(7);
    }

    @Test
    public void parser_should_return_a_grammar_organized_by_package_and_classes() {
        // Given
        parser.usingSourceDirectory(sourceTree);

        //When
        Grammar grammar = parser.process();

        System.out.println("GrammarParserTest.parser_should_return_a_grammar_organized_by_package_and_classes\n"
                        + new GsonBuilder().setPrettyPrinting().create().toJson(grammar)
                        + "\n"
        );

        //Then
        assertThat(grammar).isNotNull();
        assertThat(grammar.packages()).hasSize(1);

        PackageEntry subGroup = grammar.packages().get(0);
        assertThat(subGroup.name()).isEqualTo("cucumber.contrib.grammar.java.stepdefs");
        assertThat(subGroup.comment()).isNullOrEmpty();
        assertThat(subGroup.classes()).hasSize(1);

        ClassEntry clazzGroup = subGroup.classes().get(0);
        assertThat(clazzGroup.qualifiedName()).isEqualTo("cucumber.contrib.grammar.java.stepdefs.OptionStepdefs");
        assertThat(clazzGroup.comment()).isNullOrEmpty();

        FluentIterable<MethodEntry> methodEntries = clazzGroup.methods();
        assertThat(methodEntries).hasSize(5);
    }

    @Test
    public void parser_should_parse_and_fill_method_information() {
        // Given
        parser.usingSourceDirectory(sourceTree);

        //When
        Grammar grammar = parser.process();

        ClassEntry clazzGroup = grammar.packages().get(0).classes().get(0);
        FluentIterable<MethodEntry> methodEntries = clazzGroup.methods();

        MethodEntry methodEntry0 = methodEntries.get(0);
        assertThat(methodEntry0.patterns().toList()).containsExactly("^I order an? \"([^\"]*)\" with (\\d+) sugar$");
        assertThat(methodEntry0.comment()).isEqualTo(
                "Order a <b>drink</b> with a number of sugar.\n" +
                        "If the drink does not support the addition of sugar it won't\n" +
                        "be checked here ({@link java.lang.String})."
        );
        assertThat(methodEntry0.parameters()).hasSize(2);
        assertThat(methodEntry0.parameter(0).getName()).isEqualTo("drinkType");
        assertThat(methodEntry0.parameter(0).getDoc()).isEqualTo("type of drink");
        assertThat(methodEntry0.parameter(1).getName()).isEqualTo("nbSugar");
        assertThat(methodEntry0.parameter(1).getDoc()).isEqualTo("number of sugar (if applicable)");

        MethodEntry methodEntry1 = methodEntries.get(1);
        assertThat(methodEntry1.patterns().toList()).containsExactly("^the instruction generated should be \"([^\"]*)\"$");
        assertThat(methodEntry1.comment()).isNullOrEmpty();
        assertThat(methodEntry1.parameters()).hasSize(1);
        assertThat(methodEntry1.parameter(0).getName()).isEqualTo("expectedProtocol");
        assertThat(methodEntry1.parameter(0).getDoc()).isNullOrEmpty();
    }
}
