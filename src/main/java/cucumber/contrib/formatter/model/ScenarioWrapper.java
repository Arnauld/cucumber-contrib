package cucumber.contrib.formatter.model;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Tag;

import java.util.List;

import static cucumber.contrib.formatter.DescriptionExtractor.extractDescription;

public class ScenarioWrapper extends StepContainer implements Wrapper, HasComments {
    private final Scenario scenario;
    private BackgroundWrapper background;

    public ScenarioWrapper(Scenario scenario) {
        this.scenario = scenario;
    }

    public void setBackground(BackgroundWrapper background) {
        this.background = background;
    }

    private String scenarioNameToAnchor(String name) {
        return "scenario-" + name.toLowerCase().replace(' ', '_');
    }

    public List<Comment> getComments() {
        return scenario.getComments();
    }

    @Override
    public void consolidate(Statistics statistics) {
        statistics.consolidate(this);
    }

    public String getName() {
        return scenario.getName();
    }

    public List<Tag> getTags() {
        return scenario.getTags();
    }

    public String getDescription() {
        return extractDescription(scenario.getDescription(), steps);
    }

    public boolean hasTag(String searchedTag) {
        for(Tag tag : getTags()) {
            if(tag.getName().equalsIgnoreCase(searchedTag))
                return true;
        }
        return false;
    }
}