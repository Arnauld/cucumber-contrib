package cucumber.contrib.formatter.model;

import java.util.List;

import gherkin.formatter.model.Background;
import gherkin.formatter.model.Tag;

public class BackgroundWrapper extends StepContainer implements Wrapper {
	private final Background background;

    public BackgroundWrapper(Background background) {
    	this.background = background;
    }
    
    public String getName() {
        return background.getName();
    }

    public List<Tag> getTags() {
        //return background.getTags();
        return null;
    }

    public boolean hasTag(String searchedTag) {
        /*for(Tag tag : getTags()) {
            if(tag.getName().equalsIgnoreCase(searchedTag))
                return true;
        }*/
        return false;
    }
}