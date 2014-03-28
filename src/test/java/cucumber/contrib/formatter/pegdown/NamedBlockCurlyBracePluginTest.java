package cucumber.contrib.formatter.pegdown;

import org.junit.Before;
import org.junit.Test;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.pegdown.ast.RootNode;
import org.pegdown.plugins.PegDownPlugins;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class NamedBlockCurlyBracePluginTest {

    private PegDownProcessor processor;

    @Before
    public void setUp() {
        PegDownPlugins plugins = PegDownPlugins
                .builder()
                .withPlugin(NamedBlockCurlyBracePlugin.class)
                .build();
        processor = new PegDownProcessor(Extensions.TABLES, plugins);
    }

    @Test
    public void parse() {
        String text = "" + //
                "# Title\n" + //
                "\n" + //
                "[asciidiag]\n" + //
                "----\n" + //
                "  /-------+     +-------+\n" + //
                "  |  REQ  |<--->|  REP  |\n" + //
                "  +-------/     +-------+\n" + //
                "----"; //
        RootNode s = processor.parseMarkdown(text.toCharArray());
        s.accept(Visitors.dump());
    }

    @Test
    public void parse_with_trailing_whitespaces() {
        String text = "" + //
                "# Title\n" + //
                "\n" + //
                "  [ asciidiag ]\n" + //
                "  ----\n" + //
                "  /-------+     +-------+\n" + //
                "  |  REQ  |<--->|  REP  |\n" + //
                "  +-------/     +-------+\n" + //
                "  ----";
        RootNode s = processor.parseMarkdown(text.toCharArray());
        s.accept(Visitors.dump());
    }

    @Test
    public void parse2() {
        String text = "" + //
                "# Title\n" + //
                "\n" + //
                "[ asciidiag ]\n" +
                " ----\n" +
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
                " ----"; //
        RootNode s = processor.parseMarkdown(text.toCharArray());
        s.accept(Visitors.dump());
    }


}
