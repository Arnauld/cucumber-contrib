package cucumber.contrib.formatter.model;

import gherkin.formatter.model.*;

public abstract class ModelBasedReport {

    private String currentUri;
    private FeatureWrapper currentFeature;

    public ModelBasedReport() {
        startReport();
    }

    protected void startReport() {
    }

    protected void endReport() {
    }

    public void uri(String uri) {
        currentUri = uri;
    }

    public void feature(Feature feature) {
        flushCurrentFeature();
        this.currentFeature = new FeatureWrapper(currentUri, feature);
    }

    private void flushCurrentFeature() {
        if (currentFeature == null) {
            return;
        }
        emit(currentFeature);
        currentFeature = null;
    }

    protected abstract void emit(FeatureWrapper currentFeature);

    public void background(Background background) {
        this.currentFeature.background(background);
    }

    public void scenario(Scenario scenario) {
        currentFeature.scenario(scenario);
    }

    public void scenarioOutline(ScenarioOutline scenarioOutline) {
        throw new UnsupportedOperationException();
    }

    public void examples(Examples examples) {
        throw new UnsupportedOperationException();
    }

    public void step(Step step) {
        currentFeature.step(step);
    }

    public void match(Match match) {
        currentFeature.match(match);
    }

    public void result(Result result) {
        currentFeature.result(result);
    }

    public void done() {
        flushCurrentFeature();
        endReport();
    }
}
