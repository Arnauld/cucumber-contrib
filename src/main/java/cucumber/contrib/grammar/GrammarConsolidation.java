package cucumber.contrib.grammar;

import cucumber.contrib.grammar.java.Grammar;
import cucumber.contrib.grammar.step.FeatureVisitorAdapter;
import cucumber.contrib.grammar.step.Features;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class GrammarConsolidation {
    private final Grammar grammar;
    private final Features features;

    public GrammarConsolidation(Grammar grammar, Features features) {
        this.grammar = grammar;
        this.features = features;
    }

    public void consolidate() {
        features.traverse(new Consolidator(grammar));
    }

    private class Consolidator extends FeatureVisitorAdapter {
        private final Grammar grammar;

        public Consolidator(Grammar grammar) {
            this.grammar = grammar;
        }
    }
}
