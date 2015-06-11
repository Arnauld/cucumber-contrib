package cucumber.contrib.formatter.model;

import static cucumber.contrib.formatter.DescriptionExtractor.extractDescription;
import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.ScenarioOutline;
import gherkin.formatter.model.Tag;

import java.util.List;

public class ScenarioWrapper extends StepContainer implements Wrapper, HasComments {
    private Scenario scenario;
    private ScenarioOutline scenarioOutline;
    private BackgroundWrapper background;

    public ScenarioWrapper(Scenario scenario) {
        this.scenario = scenario;
    }
    
    public ScenarioWrapper(ScenarioOutline scenarioOutline) {
        this.scenarioOutline = scenarioOutline;
    }

    public void setBackground(BackgroundWrapper background) {
        this.background = background;
    }

    private String scenarioNameToAnchor(String name) {
        return "scenario-" + name.toLowerCase().replace(' ', '_');
    }

    public List<Comment> getComments() {
    	if (scenario != null) {
    		return scenario.getComments();
    	} else {
    		return scenarioOutline.getComments();
    	}
    }

    public String getName() {
    	if (scenario != null) {
    		return scenario.getName();
    	} else {
    		return scenarioOutline.getName();
    	}
    }

    public List<Tag> getTags() {
    	if (scenario != null) {
    		return scenario.getTags();
    	} else {
    		return scenarioOutline.getTags();
    	}
    }

    public String getDescription() {
    	if (scenario != null) {
    		return  extractDescription(scenario.getDescription(), steps);
    	} else {
    		return  extractDescription(scenarioOutline.getDescription(), steps);
    	}
    }

    public boolean hasTag(String searchedTag) {
        for(Tag tag : getTags()) {
            if(tag.getName().equalsIgnoreCase(searchedTag))
                return true;
        }
        return false;
    }
}