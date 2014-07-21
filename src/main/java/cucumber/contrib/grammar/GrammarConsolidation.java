package cucumber.contrib.grammar;

import com.google.common.collect.FluentIterable;
import cucumber.contrib.grammar.java.Grammar;
import cucumber.contrib.grammar.java.MethodEntry;
import cucumber.contrib.grammar.java.UsedBy;
import cucumber.contrib.grammar.step.Feature;
import cucumber.contrib.grammar.step.FeatureVisitorAdapter;
import cucumber.contrib.grammar.step.Features;
import cucumber.contrib.grammar.step.Scenario;
import cucumber.contrib.grammar.step.ScenarioOutline;
import cucumber.contrib.grammar.step.Step;

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

    private static class Consolidator extends FeatureVisitorAdapter {
        private final Grammar grammar;
        //
        private String featureUri;
        private String scenarioName;
        private String scenarioOutlineName;

        public Consolidator(Grammar grammar) {
            this.grammar = grammar;
        }

        @Override
        public void enterFeature(Feature feature) {
            featureUri = feature.uri();
        }

        @Override
        public void enterScenario(Scenario scenario) {
            scenarioName = scenario.getVisualName();
        }

        @Override
        public void enterScenarioOutline(ScenarioOutline scenario) {
            scenarioOutlineName = scenario.getVisualName();
        }

        @Override
        public void visitStep(Step step) {
            String text = step.getText();
            FluentIterable<MethodEntry> methodEntries = grammar.matchingEntries(text);
            step.grammarMatchCount(methodEntries.size());

            UsedBy usedBy = new UsedBy(featureUri, scenarioOutlineName, scenarioName);
            for (MethodEntry methodEntry : methodEntries) {
                methodEntry.declareUsedBy(usedBy);
            }
        }
    }
}
