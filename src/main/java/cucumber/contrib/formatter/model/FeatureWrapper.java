package cucumber.contrib.formatter.model;

import gherkin.formatter.model.*;

import java.util.ArrayList;
import java.util.List;

import static cucumber.contrib.formatter.DescriptionExtractor.extractDescription;

public class FeatureWrapper implements Wrapper {

    private final Feature feature;
    private final String uri;
    private BackgroundWrapper background;
    private List<ScenarioWrapper> scenarios = new ArrayList<ScenarioWrapper>();

    public FeatureWrapper(String uri, Feature feature) {
        this.uri = uri;
        this.feature = feature;
    }

    public String getUri() {
        return uri;
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

    public void embedding(String mimeType, byte[] bytes) {
        Embedding embedding = new Embedding(mimeType, bytes);
        currentStepContainer().embedding(embedding);
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

    public String getName() {
        return feature.getName();
    }

    public String getDescription() {
        return extractDescription(feature.getDescription(), scenarios);
    }

    public List<ScenarioWrapper> getScenarios() {
        return scenarios;
    }
}
