package cucumber.contrib.grammar.step;

import cucumber.runtime.model.CucumberExamples;
import cucumber.runtime.model.CucumberFeature;
import cucumber.runtime.model.CucumberScenario;
import cucumber.runtime.model.CucumberScenarioOutline;
import cucumber.runtime.model.CucumberTagStatement;

import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class CucumberConverter {

    public Feature convert(CucumberFeature cucumberFeature) {
        Feature feature = new Feature(cucumberFeature.getUri());
        for (CucumberTagStatement featureElement : cucumberFeature.getFeatureElements()) {
            if (featureElement instanceof CucumberScenario) {
                feature.add(convert((CucumberScenario) featureElement));
            } else if (featureElement instanceof CucumberScenarioOutline) {
                feature.add(convert((CucumberScenarioOutline) featureElement));
            }
        }
        return feature;
    }

    private ScenarioOutline convert(CucumberScenarioOutline cucumberScenarioOutline) {
        ScenarioOutline outline = new ScenarioOutline();
        outline.setVisualName(cucumberScenarioOutline.getVisualName());

        for (CucumberExamples cucumberExamples : cucumberScenarioOutline.getCucumberExamplesList()) {
            List<CucumberScenario> exampleScenarios = cucumberExamples.createExampleScenarios();
            for (CucumberScenario exampleScenario : exampleScenarios) {
                outline.add(convert(exampleScenario));
            }
        }
        return outline;
    }

    private Scenario convert(CucumberScenario cucumberScenario) {
        Scenario scenario = new Scenario();
        scenario.setVisualName(cucumberScenario.getVisualName());
        for (gherkin.formatter.model.Step step : cucumberScenario.getSteps())
            scenario.add(convert(step));
        return scenario;
    }

    private Step convert(gherkin.formatter.model.Step cucumberStep) {
        return new Step(cucumberStep.getKeyword(), cucumberStep.getName());
    }
}
