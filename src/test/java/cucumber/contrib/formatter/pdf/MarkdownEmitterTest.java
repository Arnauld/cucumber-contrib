package cucumber.contrib.formatter.pdf;

import static org.fest.assertions.Assertions.assertThat;

import com.itextpdf.text.Element;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class MarkdownEmitterTest {

    private MarkdownEmitter markdownEmitter;

    @Before
    public void setUp() {
        Configuration configuration = new Configuration()
                .withMetaInformationsResources(getClass(), "test.properties");
        markdownEmitter = new MarkdownEmitter(configuration);
    }

    @Test
    public void markdown_with_paragraph__new_lines_are_not_kept() throws IOException {
        String markdownText = "" +
                "Once upon a time \n" +
                "in a kingdom far, \n" +
                "far away, \n" +
                "the king and queen\n" +
                "";

        // html
        StringReader stringReader = markdownEmitter.formatHtmlAsReader(markdownText);
        String html = IOUtils.toString(stringReader);
        assertThat(html).isEqualTo("<p><code>var s = 1; \n" +
                "s = s + 2; \n" +
                "</code></pre>");

        // pdf elements
        List<Element> elements = markdownEmitter.markdownToElements(markdownText);
        assertThat(elements).isNotEmpty();

    }

    @Test
    public void markdown_with_codeBlock() throws IOException {
        String markdownText = "\n" +
                "    var s = 1; \n" +
                "    s = s + 2; \n";

        // html
        StringReader stringReader = markdownEmitter.formatHtmlAsReader(markdownText);
        String html = IOUtils.toString(stringReader);
        assertThat(html).isEqualTo("<pre><code>var s = 1; \n" +
                "s = s + 2; \n" +
                "</code></pre>");

        // pdf elements
        List<Element> elements = markdownEmitter.markdownToElements(markdownText);
        assertThat(elements).isNotEmpty();

    }

    @Test
    public void markdown_with_image() throws IOException {
        String markdownText = "![Alt text](/customer.jpeg \"Customer\")";

        // html
        StringReader stringReader = markdownEmitter.formatHtmlAsReader(markdownText);
        String html = IOUtils.toString(stringReader);
        assertThat(html).isEqualTo("<p><img src=\"/customer.jpeg\"  alt=\"Alt text\"/></p>");

        // pdf elements
        List<Element> elements = markdownEmitter.markdownToElements(markdownText);
        assertThat(elements).isNotEmpty();
    }

    @Test
    public void markdown_with_asciidiag() throws IOException {
        String markdownText = "" + //
                        "# Title\n" + //
                        "\n" + //
                        "{% asciidiag %}\n" + //
                        "/-------+     +-------+\n" + //
                        "|  REQ  |<--->|  REP  |\n" + //
                        "+-------/     +-------+\n" + //
                        "{% asciidiag %}"; //

        // html
        StringReader stringReader = markdownEmitter.formatHtmlAsReader(markdownText);
        String html = IOUtils.toString(stringReader);
        assertThat(html).isEqualTo("<h1>Title</h1><p><img src=\"/customer.jpeg\"  alt=\"Alt text\"/></p>");

        // pdf elements
        List<Element> elements = markdownEmitter.markdownToElements(markdownText);
        assertThat(elements).isNotEmpty();
    }
}
