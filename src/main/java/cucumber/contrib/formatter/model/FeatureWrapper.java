package cucumber.contrib.formatter.model;

import static cucumber.contrib.formatter.DescriptionExtractor.extractDescription;
import gherkin.formatter.model.Background;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Match;
import gherkin.formatter.model.Result;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeatureWrapper implements Wrapper {

    private Logger logger = LoggerFactory.getLogger(FeatureWrapper.class);

    private final Feature feature;
    private final String uri;
    private BackgroundWrapper background;
    private BackgroundWrapper featureBackground;
    private List<ScenarioWrapper> scenarios = new ArrayList<ScenarioWrapper>();
    private List<Embedding> pendingEmbeddings = new ArrayList<Embedding>();

    public FeatureWrapper(String uri, Feature feature) {
        this.uri = uri;
        this.feature = feature;
    }

    public String getUri() {
        return uri;
    }

    public void background(Background background) {
        this.background = new BackgroundWrapper(background);
        
        drainPendingEmbeddings();
    }

    public void result(Result result) {
        currentStepContainer().result(result);
    }

    public void match(Match match) {
        currentStepContainer().match(match);
    }

    public void embedding(String mimeType, byte[] bytes) {
        Embedding embedding = new Embedding(mimeType, bytes);
        if (isCurrentStepContainerDefined()
                && !currentStepContainer().isComplete())
            currentStepContainer().embedding(embedding);
        else
            pendingEmbeddings.add(embedding);
    }

    public void step(Step step) {
   		currentStepContainer().step(step);
    }

    public void scenario(Scenario scenario) {
        ScenarioWrapper wrapper = new ScenarioWrapper(scenario);
        if (this.background != null) {
            wrapper.setBackground(background);
            featureBackground = this.background;
            this.background = null;
        }
        this.scenarios.add(wrapper);
        drainPendingEmbeddings();
    }

    private void drainPendingEmbeddings() {
        for(Embedding embedding : pendingEmbeddings)
            currentStepContainer().embedding(embedding);
        pendingEmbeddings.clear();
    }

    private boolean isCurrentStepContainerDefined() {
        return background != null || !scenarios.isEmpty();
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
    
    public BackgroundWrapper getBackground() {
    	return featureBackground;
    }
}
