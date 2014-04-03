package cucumber.contrib.junit;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class DocumentThroughStoreFilterTest {

    private InMemoryDocumentStore store;
    private DocumentThroughStoreFilter filter;

    @Before
    public void setUp() {
        store = new InMemoryDocumentStore();
        filter = new DocumentThroughStoreFilter(store);
    }

    @Test
    @Ignore("Raw strategy is not directly supported")
    public void filter_should_preserve_line_number() throws IOException {

        String raw =
                "" +
                        "Feature: some very useful feature\n" +
                        "\n" +
                        "  **In order** to send commands to the drink maker\n" +
                        "\n" +
                        "  **As a** developer\n" +
                        "\n" +
                        "  **I want to** implement the logic that translates orders\n" +
                        "  from customers of the coffee machine to the drink maker\n" +
                        "\n" +
                        "\n" +
                        "  @ProtocolOrder\n" +
                        "  Scenario: A tea with 1 sugar and a stick\n" +
                        "\n" +
                        "    When I order a Tea with 1 sugar\n" +
                        "    Then the instruction generated should be 'T:1:0'\n" +
                        "\n";

        InputStream filtered = filter.filter(toStreamUTF8(raw));
        String utf8 = IOUtils.toString(filtered, "UTF8");
        assertThat(utf8).isEqualTo("" +
                "Feature: some very useful feature\n" +
                "{{1}}\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "  @ProtocolOrder\n" +
                "  Scenario: A tea with 1 sugar and a stick\n" +
                "\n" +
                "    When I order a Tea with 1 sugar\n" +
                "    Then the instruction generated should be 'T:1:0'\n");
    }

    @Test
    public void docblock_should_be_appear_as_comment() throws IOException {

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

        InputStream filtered = filter.filter(toStreamUTF8(raw));
        String utf8 = IOUtils.toString(filtered, "UTF8");
        assertThat(utf8).isEqualTo("" +
                "Feature: some very useful feature\n" +
                "\n" +
                "--\n" +
                "{{1}}\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "--\n" +
                "\n" +
                "  @ProtocolOrder\n" +
                "  Scenario: A tea with 1 sugar and a stick\n" +
                "\n" +
                "    When I order a Tea with 1 sugar\n" +
                "    Then the instruction generated should be 'T:1:0'\n");
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

        InputStream filtered = filter.filter(toStreamUTF8(raw));
        String utf8 = IOUtils.toString(filtered, "UTF8");
        assertThat(utf8).isEqualTo("" +
                "Feature: some very useful feature\n" +
                "\n" +
                "--\n" +
                "{{1}}\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "--\n" +
                "\n" +
                "  @ProtocolOrder\n" +
                "  Scenario: A tea with 1 sugar and a stick\n" +
                "\n" +
                "    When I order a Tea with 1 sugar\n" +
                "    Then the instruction generated should be 'T:1:0'\n");
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

        InputStream filtered = filter.filter(toStreamUTF8(raw));
        String utf8 = IOUtils.toString(filtered, "UTF8");
        assertThat(utf8).isEqualTo("" +
                "Feature: some very useful feature\n" +
                "\n" +
                "--\n" +
                "--\n" +
                "\n" +
                "  @ProtocolOrder\n" +
                "  Scenario: A tea with 1 sugar and a stick\n" +
                "\n" +
                "    When I order a Tea with 1 sugar\n" +
                "    Then the instruction generated should be 'T:1:0'\n");
    }

    @Test
    public void docblock_should_end() throws IOException {

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

        InputStream filtered = filter.filter(toStreamUTF8(raw));
        String utf8 = IOUtils.toString(filtered, "UTF8");
        assertThat(utf8).isEqualTo("" +
                "Feature: some very useful feature\n" +
                "\n" +
                "--\n" +
                "{{1}}\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n");
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
                        "--\n" +
                        " Nog:\n" +
                        "--\n" +
                        "    Then the instruction generated should be 'T:1:0'\n" +
                        "\n";

        InputStream filtered = filter.filter(toStreamUTF8(raw));
        String utf8 = IOUtils.toString(filtered, "UTF8");
        assertThat(utf8).isEqualTo("" +
                "Feature: some very useful feature\n" +
                "\n" +
                "--\n" +
                "{{1}}\n" +
                "--\n" +
                " Nog:\n" +
                "--\n" +
                "{{2}}\n" +
                "--\n" +
                "\n" +
                "  @ProtocolOrder\n" +
                "  Scenario: A tea with 1 sugar and a stick\n" +
                "\n" +
                "    When I order a Tea with 1 sugar\n" +
                "--\n" +
                "{{3}}\n" +
                "--\n" +
                "    Then the instruction generated should be 'T:1:0'\n");
    }

    @Test
    public void docblock_could_happen_between_steps() throws IOException {

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
                        "--\n" +
                        " Nog:\n" +
                        "--\n" +
                        "    Then the instruction generated should be 'T:1:0'\n" +
                        "\n";

        InputStream filtered = filter.filter(toStreamUTF8(raw));
        String utf8 = IOUtils.toString(filtered, "UTF8");
        assertThat(utf8).isEqualTo("" +
                "Feature: some very useful feature\n" +
                "\n" +
                "--\n" +
                "{{1}}\n" +
                "--\n" +
                " Nog:\n" +
                "--\n" +
                "{{2}}\n" +
                "--\n" +
                "\n" +
                "  @ProtocolOrder\n" +
                "  Scenario: A tea with 1 sugar and a stick\n" +
                "\n" +
                "    When I order a Tea with 1 sugar\n" +
                "--\n" +
                "{{3}}\n" +
                "--\n" +
                "    Then the instruction generated should be 'T:1:0'\n");
    }

    private static InputStream toStreamUTF8(String raw) throws UnsupportedEncodingException {
        return new ByteArrayInputStream(raw.getBytes("utf8"));
    }

}
