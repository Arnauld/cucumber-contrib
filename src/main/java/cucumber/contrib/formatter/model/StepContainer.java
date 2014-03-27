package cucumber.contrib.formatter.model;

import gherkin.formatter.model.Match;
import gherkin.formatter.model.Result;
import gherkin.formatter.model.Step;

import java.util.ArrayList;
import java.util.List;

public class StepContainer {
    protected List<StepWrapper> steps = new ArrayList<StepWrapper>();
    private int stepCursor = -1; // used by match & result
    private List<Embedding> embeddings = new ArrayList<Embedding>();

    public void match(Match match) {
        matchStep().match(match);
    }

    public void result(Result result) {
        resultStep().result(result);
    }

    private StepWrapper matchStep() {
        return this.steps.get(++stepCursor); // match is triggered before result
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

    public void embedding(Embedding embedding) {
        StepWrapper step = currentStep();
        if(step != null) {
            step.embedding(embedding);
        }
        else {
            embeddings.add(embedding);
        }
    }

    private StepWrapper currentStep() {
        if (stepCursor > -1)
            return steps.get(stepCursor);
        else
            return null;
    }

    public List<Embedding> getEmbeddings() {
        return embeddings;
    }
}
