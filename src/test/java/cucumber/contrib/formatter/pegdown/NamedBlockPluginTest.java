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
        s.accept(newVisitor());
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
        s.accept(newVisitor());
    }

    public static Visitor newVisitor() {
        final AtomicReference<Visitor> selfRef = new AtomicReference<Visitor>();

        Visitor visitor = (Visitor) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Visitor.class}, new InvocationHandler() {
            int indent;

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println("NamedBlockPluginTest.invoke::" + indent() + method.getName() + ":: " + Arrays.toString(args));
                Object o = args[0];
                if (o instanceof SuperNode) {
                    indent++;
                    visitChildren((SuperNode) o);
                    indent--;
                }
                return null;
            }

            private String indent() {
                StringBuilder b = new StringBuilder();
                for (int i = 0; i < indent; i++)
                    b.append("  ");
                return b.toString();
            }

            protected void visitChildren(SuperNode node) {
                for (Node child : node.getChildren()) {
                    child.accept(selfRef.get());
                }

            }
        });
        selfRef.set(visitor);
        return visitor;
    }
}
