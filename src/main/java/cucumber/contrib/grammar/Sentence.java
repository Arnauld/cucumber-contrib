package cucumber.contrib.grammar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class Sentence extends Describable {
    private List<String> patterns = new ArrayList<String>();
    private List<Parameter> parameters = new ArrayList<Parameter>();

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
}
