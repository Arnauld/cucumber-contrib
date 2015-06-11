package cucumber.contrib.formatter.model;

import gherkin.formatter.model.*;
import net.sourceforge.plantuml.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static cucumber.contrib.formatter.DescriptionExtractor.extractDescription;

public class FeatureWrapper implements Wrapper {

    private Logger logger = LoggerFactory.getLogger(FeatureWrapper.class);

    private final Feature feature;
    private final String uri;
    private BackgroundWrapper background;
    private List<ScenarioWrapper> scenarios = new ArrayList<ScenarioWrapper>();
    private List<Embedding> pendingEmbeddings = new ArrayList<Embedding>();
    private int lastStepLine = 0;


    public FeatureWrapper(String uri, Feature feature) {
        this.uri = uri;
        this.feature = feature;
    }

    public String getUri() {
        return uri;
    }

    public void background(Background background) {
    	logger.info("featurewrapper.background");
        
    	//if (this.background == null) {
    		this.background = new BackgroundWrapper(background);
    	//}
        
        drainPendingEmbeddings();
    }

    public void result(Result result) {
        //currentStepContainer().result(result);
        if (scenarios.size() > 0) {
    		currentScenario().result(result);
    	} else if (this.background != null) {
    		background.result(result);
    	}
    }

    public void match(Match match) {
        //currentStepContainer().match(match);
        if (scenarios.size() > 0) {
    		currentScenario().match(match);
    	} else if (this.background != null) {
    		background.match(match);
    	}
    }

    public void embedding(String mimeType, byte[] bytes) {
        Embedding embedding = new Embedding(mimeType, bytes);
        if (isCurrentStepContainerDefined()
                && !currentStepContainer().isComplete())
            //currentStepContainer().embedding(embedding);
	        if (scenarios.size() > 0) {
	    		currentScenario().embedding(embedding);
	    	} else if (this.background != null) {
	    		background.embedding(embedding);
	    	}
        else
            pendingEmbeddings.add(embedding);
    }

    public void step(Step step) {
    	if ((scenarios.size() > 0) && (step.getLine() > lastStepLine)) {
    		currentScenario().step(step);
    	} else if ((this.background != null)  && (step.getLine() > lastStepLine)) {
    		background.step(step);
    	}
    	
    	lastStepLine = step.getLine();
    }

    public void scenario(Scenario scenario) {
        ScenarioWrapper wrapper = new ScenarioWrapper(scenario);
        if (this.background != null) {
            wrapper.setBackground(background);
            //this.background = null;
        }
        this.scenarios.add(wrapper);
        drainPendingEmbeddings();
    }
    
    public void scenarioOutline(ScenarioOutline scenarioOutline) {
    	ScenarioWrapper wrapper = new ScenarioWrapper(scenarioOutline);
        if (this.background != null) {
            wrapper.setBackground(background);
            //this.background = null;
        }
        this.scenarios.add(wrapper);
        drainPendingEmbeddings();
    }

    private void drainPendingEmbeddings() {
        for(Embedding embedding : pendingEmbeddings) {
        	//currentStepContainer().embedding(embedding);
        	if (scenarios.size() > 0) {
        		currentScenario().embedding(embedding);
        	} else if (this.background != null) {
        		background.embedding(embedding);
        	}
        }
            
        pendingEmbeddings.clear();
    }

    private boolean isCurrentStepContainerDefined() {
        return background != null || !scenarios.isEmpty();
    }

    private StepContainer currentStepContainer() {
        //return (background != null) ? background : currentScenario();
        
        if (scenarios.size() > 0) {
    		return currentScenario();
    	} else {
    		return background;
    	}
        
    	//return (currentScenario() != null) ? currentScenario() : background;
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
    	return background;
    }
}
