package cucumber.contrib.formatter.model;

import gherkin.formatter.model.Background;

public class BackgroundWrapper extends StepContainer implements Wrapper {
	private final Background background;

    public BackgroundWrapper(Background background) {
    	this.background = background;
    }
    
    public String getName() {
        return background.getName();
    }
}