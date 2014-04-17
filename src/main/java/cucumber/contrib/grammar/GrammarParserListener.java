package cucumber.contrib.grammar;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaPackage;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class GrammarParserListener {
    private int packagesParsed = 0;
    private int classesParsed = 0;
    private int methodsParsed = 0;

    public int numberOfClassesParsed() {
        return classesParsed;
    }

    public int numberOfPackagesParsed() {
        return packagesParsed;
    }

    public int numberOfMethodsParsed() {
        return methodsParsed;
    }

    public void enteringPackage(JavaPackage pkg) {
    }

    public void exitingPackage(JavaPackage pkg) {
        packagesParsed++;
    }

    public void enteringClass(JavaClass klazz) {
    }

    public void exitingClass(JavaClass klazz) {
        classesParsed++;
    }

    public void enteringMethod(JavaMethod method) {

    }

    public void exitingMethod(JavaMethod method) {
        methodsParsed++;
    }
}
