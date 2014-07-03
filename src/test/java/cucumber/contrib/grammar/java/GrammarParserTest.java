package cucumber.contrib.grammar.java;

import com.google.gson.GsonBuilder;
import cucumber.api.java.en.*;
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
import java.util.List;
import java.util.Properties;

import static cucumber.contrib.grammar.java.GrammarParser.STEP_KEYWORD_QUALIFIED_NAME;
import static cucumber.contrib.grammar.java.Source.classSource;
import static cucumber.contrib.grammar.java.Source.packageSource;
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
        assertThat(grammar.subGroups()).hasSize(1);

        SentenceGroup subGroup = grammar.subGroups().get(0);
        assertThat(subGroup.source()).isEqualTo(packageSource("cucumber.contrib.grammar.java.stepdefs"));
        assertThat(subGroup.comment()).isNullOrEmpty();
        assertThat(subGroup.subGroups()).hasSize(1);

        SentenceGroup clazzGroup = subGroup.subGroup(0);
        assertThat(clazzGroup.source()).isEqualTo(classSource("cucumber.contrib.grammar.java.stepdefs", "OptionStepdefs"));
        assertThat(clazzGroup.comment()).isNullOrEmpty();

        List<Sentence> sentences = clazzGroup.sentences();
        assertThat(sentences).hasSize(5);
    }

    @Test
    public void parser_should_parse_and_fill_method_information() {
        // Given
        parser.usingSourceDirectory(sourceTree);

        //When
        Grammar grammar = parser.process();

        SentenceGroup clazzGroup = grammar.subGroup(0).subGroup(0);
        List<Sentence> sentences = clazzGroup.sentences();

        Sentence sentence0 = sentences.get(0);
        assertThat(sentence0.patterns()).containsExactly("^I order an? \"([^\"]*)\" with (\\d+) sugar$");
        assertThat(sentence0.comment()).isEqualTo(
                "Order a <b>drink</b> with a number of sugar.\n" +
                        "If the drink does not support the addition of sugar it won't\n" +
                        "be checked here ({@link java.lang.String})."
        );
        assertThat(sentence0.parameters()).hasSize(2);
        assertThat(sentence0.parameter(0).getName()).isEqualTo("drinkType");
        assertThat(sentence0.parameter(0).getDoc()).isEqualTo("type of drink");
        assertThat(sentence0.parameter(1).getName()).isEqualTo("nbSugar");
        assertThat(sentence0.parameter(1).getDoc()).isEqualTo("number of sugar (if applicable)");

        Sentence sentence1 = sentences.get(1);
        assertThat(sentence1.patterns()).containsExactly("^the instruction generated should be \"([^\"]*)\"$");
        assertThat(sentence1.comment()).isNullOrEmpty();
        assertThat(sentence1.parameters()).hasSize(1);
        assertThat(sentence1.parameter(0).getName()).isEqualTo("expectedProtocol");
        assertThat(sentence1.parameter(0).getDoc()).isNullOrEmpty();
    }
}
