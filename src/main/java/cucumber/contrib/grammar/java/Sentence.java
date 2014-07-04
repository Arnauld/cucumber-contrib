package cucumber.contrib.grammar.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class Sentence extends Describable {
    private List<String> patterns = new ArrayList<String>();
    private List<Parameter> parameters = new ArrayList<Parameter>();
    private Set<UsedBy> usedBySet = new HashSet<UsedBy>();

    public List<String> patterns() {
        return patterns;
    }

    public boolean isEmpty() {
        return patterns.isEmpty();
    }

    public void declarePattern(String keyword, String pattern) {
        if (pattern != null)
            patterns.add(pattern);
    }

    public List<Parameter> parameters() {
        return parameters;
    }

    public void defineParameter(int index,
                                String name,
                                String parameterDoc) {
        parameters.add(new Parameter(index, name, parameterDoc));
    }

    public Parameter parameter(int index) {
        return parameters.get(index);
    }

    public void declareUsedBy(UsedBy usedBy) {
        usedBySet.add(usedBy);
    }

    public boolean matches(String text) {
        for (String regex : patterns) {
            Pattern pattern = Pattern.compile(regex);
            if (pattern.matcher(text).matches())
                return true;
        }
        return false;
    }
}
