package cucumber.contrib.discovery.qdox;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaPackage;
import org.junit.Test;

import java.io.File;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class GrammarGenTest {

    @Test
    public void usecase() {
        JavaProjectBuilder builder = new JavaProjectBuilder();
        // Adding all .java files in a source tree (recursively).
        builder.addSourceTree(new File("/Users/Arnauld/Projects/cucumber-contrib/src/test/java/sample/coffeemachine"));

        for(JavaPackage pkg : builder.getPackages()) {
            System.out.println("::: " + pkg.getName());
            for(JavaClass klazz : pkg.getClasses()) {
                System.out.println(" :: " + klazz);
                for(JavaMethod method : klazz.getMethods()) {
                    System.out.println("  : " + method.getAnnotations());
                }
            }
        }
    }
}
