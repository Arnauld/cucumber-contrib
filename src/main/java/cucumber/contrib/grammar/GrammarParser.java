package cucumber.contrib.grammar;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.*;

import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cucumber.contrib.grammar.Source.*;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class GrammarParser {
    public static Pattern STEP_KEYWORD_QUALIFIED_NAME = Pattern.compile("cucumber\\.api\\.java\\.[^.]+\\.(.+)");
    //
    private GrammarParserListener listener = new GrammarParserListener();
    private JavaProjectBuilder builder = new JavaProjectBuilder();

    public GrammarParser usingSourceDirectory(File sourceTree) {
        builder.addSourceTree(sourceTree);
        return this;
    }

    public GrammarParser usingListener(GrammarParserListener listener) {
        this.listener = listener;
        return this;
    }

    public Grammar process() {
        Grammar grammar = new Grammar();
        for (JavaPackage pkg : builder.getPackages()) {
            SentenceGroup pkgGroup = analyzePackage(pkg);
            addIfNotEmpty(grammar, pkgGroup);
        }
        return grammar;
    }

    private SentenceGroup analyzePackage(JavaPackage pkg) {
        listener.enteringPackage(pkg);

        SentenceGroup pkgGroup = new SentenceGroup();
        pkgGroup.defineSource(packageSource(pkg.getName()));
        describe(pkgGroup, pkg);

        for (JavaClass klazz : pkg.getClasses()) {
            SentenceGroup klazzGroup = analyzeClass(klazz);
            addIfNotEmpty(pkgGroup, klazzGroup);
        }
        for (JavaPackage subPkg : pkg.getSubPackages()) {
            SentenceGroup subGroup = analyzePackage(subPkg);
            addIfNotEmpty(pkgGroup, subGroup);
        }
        listener.exitingPackage(pkg);
        return pkgGroup;
    }

    private SentenceGroup analyzeClass(JavaClass klazz) {
        listener.enteringClass(klazz);

        SentenceGroup group = new SentenceGroup();
        group.defineSource(classSource(klazz.getPackageName(), klazz.getName()));
        describe(group, klazz);

        for (JavaMethod method : klazz.getMethods()) {
            Sentence sentence = analyzeMethod(method);
            if (!sentence.isEmpty()) {
                group.declareSentence(sentence);
            }
        }
        listener.exitingClass(klazz);
        return group;
    }

    private static void describe(Describable describable, JavaAnnotatedElement klazz) {
        describable.describeWith(klazz.getComment());
    }

    private Sentence analyzeMethod(JavaMethod method) {
        listener.enteringMethod(method);

        Sentence sentence = new Sentence();
        sentence.defineSource(methodSource(method.getName(), toString(method.getParameterTypes())));
        describe(sentence, method);

        fillWithPatterns(method, sentence);

        if (!sentence.isEmpty()) {
            fillWithParameters(method, sentence);
        }

        listener.exitingMethod(method);
        return sentence;
    }

    private void fillWithParameters(JavaMethod method, Sentence sentence) {
        List<DocletTag> paramDocs = method.getTagsByName("param");
        List<JavaParameter> parameters = method.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            JavaParameter param = parameters.get(i);
            String paramName = param.getName();
            DocletTag paramDoc = findParamDocByNameOrIndex(paramName, i, paramDocs);
            String doc = (paramDoc != null) ? extractDoc(paramName, paramDoc) : null;

            sentence.defineParameter(i, paramName, doc);
        }
    }

    private String extractDoc(String value, DocletTag paramDoc) {
        String cleaned = paramDoc.getValue().trim();
        if (cleaned.startsWith(value))
            return cleaned.substring(value.length()).trim();
        return cleaned;
    }

    /**
     * Attempt to retrieve the parameter (<code>@param</code> doclet parameter)
     * using the parameter name, otherwise fallback to the parameter index.
     */
    private DocletTag findParamDocByNameOrIndex(String name, int paramIndex, List<DocletTag> tags) {
        for (DocletTag tag : tags) {
            if (name.equals(tag.getParameters().get(0))) {
                return tag;
            }
        }
        if (paramIndex < tags.size()) {
            return tags.get(paramIndex);
        }
        return null;
    }

    private void fillWithPatterns(JavaMethod method, Sentence sentence) {
        List<JavaAnnotation> annotations = method.getAnnotations();
        for (JavaAnnotation annotation : annotations) {
            String keyword = extractStepKeywordIfRelevant(annotation);
            if (keyword != null) {
                String pattern = String.valueOf(annotation.getNamedParameter("value"));
                sentence.declarePattern(keyword, unescape(unquote(pattern)));
            }
        }
    }

    private String extractStepKeywordIfRelevant(JavaAnnotation annotation) {
        JavaClass annotationType = annotation.getType();
        String name = qualifiedName(annotationType);

        // Note: it seems that a step annotation is itself annotated
        // with '@cucumber.runtime.java.StepDefAnnotation'
        // it may be a safer way to determine if an annotation corresponds
        // to a keyword...

        Matcher matcher = STEP_KEYWORD_QUALIFIED_NAME.matcher(name);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        return null;
    }

    private static String qualifiedName(JavaClass klazz) {
        return klazz.getPackageName() + "." + klazz.getName();
    }

    private static String unescape(String value) {
        return value.replaceAll("\\\\(.)", "$1");
    }

    private static String unquote(String value) {
        int len = value.length();
        if (len >= 2 && value.charAt(0) == '"' && value.charAt(len - 1) == '"')
            return value.substring(1, len - 1);
        return value;
    }

    private static String toString(List<JavaType> parameterTypes) {
        StringBuilder b = new StringBuilder();
        for (JavaType type : parameterTypes) {
            if (b.length() > 0)
                b.append(",");
            b.append(type.getFullyQualifiedName());
        }
        return b.toString();
    }

    private static void addIfNotEmpty(SentenceGroup groupParent, SentenceGroup subGroup) {
        if (!subGroup.isEmpty())
            groupParent.declareGroup(subGroup);
    }

}
