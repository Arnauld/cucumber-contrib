package cucumber.contrib.formatter.pdf;

import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfWriter;
import cucumber.contrib.util.Provider;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class MarkdownEmitterTest {

    private MarkdownEmitter markdownEmitter;

    @Before
    public void setUp() {
        Configuration configuration = new Configuration()
                .withMetaInformationsResources(getClass(), "test.properties");
        markdownEmitter = new MarkdownEmitter(configuration, new Provider<PdfWriter>() {
            @Override
            public PdfWriter get() {
                throw new UnsupportedOperationException();
            }
        });
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
        assertThat(html).isEqualTo("<p>Once upon a time in a kingdom far, far away, the king and queen</p>");

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
        assertThat(html).matches(
                literal("<h1>Title</h1>\n" +
                        "<p>\n" +
                        " <img src=\"file:") +
                        "(.+)" +
                        literal(".png\" />\n" +
                                " </p>\n"));
        // pdf elements
        List<Element> elements = markdownEmitter.markdownToElements(markdownText);
        assertThat(elements).isNotEmpty();
    }

    private static String literal(String expr) {
        return "\\Q" + expr + "\\E";

    }
}
