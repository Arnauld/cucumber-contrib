package cucumber.contrib.junit;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class CommentDocBlockFilterTest {

    private CommentDocBlockFilter filter;

    @Before
    public void setUp() {
        filter = new CommentDocBlockFilter();
    }

    private String filter(String raw) throws IOException {
        InputStream filtered = filter.filter(toStreamUTF8(raw));
        return IOUtils.toString(filtered, "UTF8");
    }

    @Test
    public void regex_returns_multiple_tokens_when_pattern_is_encountered_multiple_times() {
        String[] fragments = ("" +
                "\n" +
                "\n" +
                "--\n" +
                "\n").split("[\r\n]", -1);
        assertThat(fragments).isEqualTo(new String[] {"", "", "--", "", ""});
    }

    @Test
    public void docblock_should_be_replace_delimiter_with_comment() throws IOException {

        String raw =
                "" +
                        "--\n" +
                        "Dooo! this is really interesting\n" +
                        "--\n";
        String utf8 = filter(raw);
        assertThat(utf8).isEqualTo("" +
                "#\n" +
                "#Dooo! this is really interesting\n" +
                "#\n");
    }

    @Test
    public void docblock_should_preserve_multiple_newlines_within_block() throws IOException {

        String raw =
                "" +
                        "--\n" +
                        "\n" +
                        "\n" +
                        "Dooo! this is really interesting\n" +
                        "--\n";
        String utf8 = filter(raw);
        assertThat(utf8).isEqualTo("" +
                "#\n" +
                "#\n" +
                "#\n" +
                "#Dooo! this is really interesting\n" +
                "#\n");
    }

    @Test
    public void docblock_should_preserve_multiple_newlines_outside_block__end_of_file_case() throws IOException {

        String raw =
                "" +
                        "--\n" +
                        "Dooo! this is really interesting\n" +
                        "--\n" +
                        "\n" +
                        "\n";
        String utf8 = filter(raw);
        assertThat(utf8).isEqualTo("" +
                "#\n" +
                "#Dooo! this is really interesting\n" +
                "#\n" +
                "\n" +
                "\n");
    }

    @Test
    public void docblock_should_preserve_multiple_newlines_outside_block__start_of_file_case() throws IOException {

        String raw =
                "" +
                        "\n" +
                        "\n" +
                        "--\n" +
                        "Dooo! this is really interesting\n" +
                        "--\n" +
                        "\n" +
                        "\n";
        String utf8 = filter(raw);
        assertThat(utf8).isEqualTo("" +
                "\n" +
                "\n" +
                "#\n" +
                "#Dooo! this is really interesting\n" +
                "#\n" +
                "\n" +
                "\n");
    }

    @Test
    public void docblock_should_support_keywords() throws IOException {

        String raw =
                "" +
                        "Feature: some very useful feature\n" +
                        "\n" +
                        "--\n" +
                        "  A scenario is tructured as follow:\n" +
                        "\n" +
                        "  Scenario: my scenario title\n" +
                        "\n" +
                        "  Given <an initial context>\n" +
                        "  When <an action is performed>\n" +
                        "  Then <an outcome should be expected>\n" +
                        "--\n" +
                        "\n" +
                        "  @ProtocolOrder\n" +
                        "  Scenario: A tea with 1 sugar and a stick\n" +
                        "\n" +
                        "    When I order a Tea with 1 sugar\n" +
                        "    Then the instruction generated should be 'T:1:0'\n" +
                        "\n";

        String utf8 = filter(raw);
        assertThat(utf8).isEqualTo("" +
                "Feature: some very useful feature\n" +
                "\n" +
                "#\n" +
                "#  A scenario is tructured as follow:\n" +
                "#\n" +
                "#  Scenario: my scenario title\n" +
                "#\n" +
                "#  Given <an initial context>\n" +
                "#  When <an action is performed>\n" +
                "#  Then <an outcome should be expected>\n" +
                "#\n" +
                "\n" +
                "  @ProtocolOrder\n" +
                "  Scenario: A tea with 1 sugar and a stick\n" +
                "\n" +
                "    When I order a Tea with 1 sugar\n" +
                "    Then the instruction generated should be 'T:1:0'\n" +
                "\n");
    }

    @Test
    public void docblock_could_be_empty() throws IOException {

        String raw =
                "" +
                        "Feature: some very useful feature\n" +
                        "\n" +
                        "--\n" +
                        "--\n" +
                        "\n" +
                        "  @ProtocolOrder\n" +
                        "  Scenario: A tea with 1 sugar and a stick\n" +
                        "\n" +
                        "    When I order a Tea with 1 sugar\n" +
                        "    Then the instruction generated should be 'T:1:0'\n" +
                        "\n";

        String utf8 = filter(raw);
        assertThat(utf8).isEqualTo("" +
                "Feature: some very useful feature\n" +
                "\n" +
                "#\n" +
                "#\n" +
                "\n" +
                "  @ProtocolOrder\n" +
                "  Scenario: A tea with 1 sugar and a stick\n" +
                "\n" +
                "    When I order a Tea with 1 sugar\n" +
                "    Then the instruction generated should be 'T:1:0'\n" +
                "\n");
    }

    @Test
    public void docblock_doesnot_end_by_itself() throws IOException {

        String raw =
                "" +
                        "Feature: some very useful feature\n" +
                        "\n" +
                        "--\n" +
                        "\n" +
                        "  @ProtocolOrder\n" +
                        "  Scenario: A tea with 1 sugar and a stick\n" +
                        "\n" +
                        "    When I order a Tea with 1 sugar\n" +
                        "    Then the instruction generated should be 'T:1:0'\n" +
                        "\n";

        String utf8 = filter(raw);
        assertThat(utf8).isEqualTo("" +
                "Feature: some very useful feature\n" +
                "\n" +
                "#\n" +
                "#\n" +
                "#  @ProtocolOrder\n" +
                "#  Scenario: A tea with 1 sugar and a stick\n" +
                "#\n" +
                "#    When I order a Tea with 1 sugar\n" +
                "#    Then the instruction generated should be 'T:1:0'\n" +
                "#\n" +
                "#");
    }

    @Test
    public void docblock_could_happen_multiple_times() throws IOException {

        String raw =
                "" +
                        "Feature: some very useful feature\n" +
                        "\n" +
                        "--\n" +
                        "Hey!\n" +
                        "--\n" +
                        " Nog:\n" +
                        "--\n" +
                        "Hey!\n" +
                        "--\n" +
                        "\n" +
                        "  @ProtocolOrder\n" +
                        "  Scenario: A tea with 1 sugar and a stick\n" +
                        "\n" +
                        "    When I order a Tea with 1 sugar\n" +
                        "    Then the instruction generated should be 'T:1:0'\n" +
                        "\n";

        String utf8 = filter(raw);
        assertThat(utf8).isEqualTo("" +
                "Feature: some very useful feature\n" +
                "\n" +
                "#\n" +
                "#Hey!\n" +
                "#\n" +
                " Nog:\n" +
                "#\n" +
                "#Hey!\n" +
                "#\n" +
                "\n" +
                "  @ProtocolOrder\n" +
                "  Scenario: A tea with 1 sugar and a stick\n" +
                "\n" +
                "    When I order a Tea with 1 sugar\n" +
                "    Then the instruction generated should be 'T:1:0'\n" +
                "\n");
    }

    @Test
    public void docblock_could_happen_between_steps() throws IOException {

        String raw =
                "" +
                        "Feature: some very useful feature\n" +
                        "\n" +
                        "  @ProtocolOrder\n" +
                        "  Scenario: A tea with 1 sugar and a stick\n" +
                        "\n" +
                        "    When I order a Tea with 1 sugar\n" +
                        "--\n" +
                        " Nog:\n" +
                        "--\n" +
                        "    Then the instruction generated should be 'T:1:0'\n" +
                        "\n";

        String utf8 = filter(raw);
        assertThat(utf8).isEqualTo("" +
                "Feature: some very useful feature\n" +
                "\n" +
                "  @ProtocolOrder\n" +
                "  Scenario: A tea with 1 sugar and a stick\n" +
                "\n" +
                "    When I order a Tea with 1 sugar\n" +
                "#\n" +
                "# Nog:\n" +
                "#\n" +
                "    Then the instruction generated should be 'T:1:0'\n" +
                "\n");
    }

    private static InputStream toStreamUTF8(String raw) throws UnsupportedEncodingException {
        return new ByteArrayInputStream(raw.getBytes("utf8"));
    }

}
