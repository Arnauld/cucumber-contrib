package cucumber.contrib.formatter.pegdown;

import org.junit.Before;
import org.junit.Test;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.Node;
import org.pegdown.ast.RootNode;
import org.pegdown.ast.SuperNode;
import org.pegdown.ast.Visitor;
import org.pegdown.plugins.PegDownPlugins;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class NamedBlockPluginTest {

    private PegDownProcessor processor;

    @Before
    public void setUp() {
        PegDownPlugins plugins = PegDownPlugins
                .builder()
                .withPlugin(NamedBlockPlugin.class)
                .build();
        processor = new PegDownProcessor(Extensions.TABLES, plugins);
    }

    @Test
    public void parse() {
        String text = "" + //
                "# Title\n" + //
                "\n" + //
                "{% asciidiag %}\n" + //
                "/-------+     +-------+\n" + //
                "|  REQ  |<--->|  REP  |\n" + //
                "+-------/     +-------+\n" + //
                "{% asciidiag %}"; //
        RootNode s = processor.parseMarkdown(text.toCharArray());
        s.accept(Visitors.dump());
    }

    @Test
    public void parse_with_trailing_whitespaces() {
        String text = "" + //
                "# Title\n" + //
                "\n" + //
                "  {% asciidiag %}\n" + //
                "  /-------+     +-------+\n" + //
                "  |  REQ  |<--->|  REP  |\n" + //
                "  +-------/     +-------+\n" + //
                "  {% asciidiag %}"; //
        RootNode s = processor.parseMarkdown(text.toCharArray());
        s.accept(Visitors.dump());
    }

    @Test
    public void parse2() {
        String text = "" + //
                "# Title\n" + //
                "\n" + //
                "{% asciidiag %}\n" +
                "/--------+  /--------+  /--------+\n" +
                "| Client |  | Client |  | Client |\n" +
                "+--------+  +--------+  +--------+\n" +
                "|  REQ   |  |  REQ   |  |  REQ   |\n" +
                "'---+----'  '---+----'  '---+----'\n" +
                "    |           |           |\n" +
                "    '-----------+-----------'\n" +
                "                |\n" +
                "            .---+----.\n" +
                "            | ROUTER |  Frontend\n" +
                "            +--------+\n" +
                "            | Proxy  |  Load balancer\n" +
                "            +--------+\n" +
                "            | ROUTER |  Backend\n" +
                "            '---+----'\n" +
                "                |\n" +
                "    .-----------+-----------.\n" +
                "    |           |           |\n" +
                ".---+----.  .---+----.  .---+----.\n" +
                "|  REQ   |  |  REQ   |  |  REQ   |\n" +
                "+--------+  +--------+  +--------+\n" +
                "| Worker |  | Worker |  | Worker |\n" +
                "+--------/  +--------/  +--------/\n" + //
                "{% asciidiag %}"; //
        RootNode s = processor.parseMarkdown(text.toCharArray());
        s.accept(Visitors.dump());
    }


}
