package cucumber.contrib.formatter.pegdown;

import org.pegdown.ast.Node;
import org.pegdown.ast.SuperNode;
import org.pegdown.ast.Visitor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class Visitors {
    public static Visitor dump() {
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
