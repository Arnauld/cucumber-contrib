package cucumber.contrib.formatter.model;

import cucumber.contrib.formatter.BricABrac;
import gherkin.formatter.model.*;

import java.util.ArrayList;
import java.util.List;

import static cucumber.contrib.formatter.BricABrac.NL;
import static cucumber.contrib.formatter.BricABrac.discardCommentChar;

/**
 *
 */
public class FeatureWrapper implements Wrapper {

    private final Feature feature;
    private final String uri;
    private BackgroundWrapper background;
    private List<ScenarioWrapper> scenarios = new ArrayList<ScenarioWrapper>();

    public FeatureWrapper(String uri, Feature feature) {
        this.uri = uri;
        this.feature = feature;
    }

    public void background(Background background) {
        this.background = new BackgroundWrapper(background);
    }

    public void result(Result result) {
        currentStepContainer().result(result);
    }

    public void match(Match match) {
        currentStepContainer().match(match);
    }

    public void step(Step step) {
        currentStepContainer().step(step);
    }

    public void scenario(Scenario scenario) {
        ScenarioWrapper wrapper = new ScenarioWrapper(scenario);
        if (this.background != null) {
            wrapper.setBackground(background);
            this.background = null;
        }
        this.scenarios.add(wrapper);
    }

    private StepContainer currentStepContainer() {
        return (background != null) ? background : currentScenario();

    }

    private ScenarioWrapper currentScenario() {
        return this.scenarios.get(this.scenarios.size() - 1);
    }

    private String featureNameToAnchor(String name) {
        return "feature-" + name.toLowerCase().replace(' ', '_');
    }


    @Override
    public void consolidate(Statistics statistics) {
        statistics.feature();
        for (ScenarioWrapper scenario : scenarios)
            scenario.consolidate(statistics);
    }

    public String getName() {
        return feature.getName();
    }

    public String getDescription() {
        String description = feature.getDescription();
        StringBuilder builder = new StringBuilder();
        if(!BricABrac.isEmpty(description)) {
            builder.append(description).append(NL);
        }
        if(!scenarios.isEmpty()) {
            ScenarioWrapper scenario = scenarios.get(0);
            for(Comment comment : scenario.getComments()) {
                builder.append(discardCommentChar(comment.getValue())).append(NL);
            }
        }
        return builder.toString();
    }

    public List<ScenarioWrapper> getScenarios() {
        return scenarios;
    }
}
