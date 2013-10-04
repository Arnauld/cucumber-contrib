package cucumber.contrib.formatter.model;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Match;
import gherkin.formatter.model.Result;
import gherkin.formatter.model.Step;

import java.util.ArrayList;
import java.util.List;

public class StepContainer {
    protected List<StepWrapper> steps = new ArrayList<StepWrapper>();
    private int stepCursor = -1; // used by match & result

    public void match(Match match) {
        matchStep().match(match);
    }

    public void result(Result result) {
        resultStep().result(result);
    }

    private StepWrapper matchStep() {
        return this.steps.get(++stepCursor); // match is triggered before
        // result
    }

    private StepWrapper resultStep() {
        return this.steps.get(stepCursor);
    }

    public void step(Step step) {
        steps.add(new StepWrapper(step));
    }

    public List<StepWrapper> getSteps() {
        return steps;
    }
}
