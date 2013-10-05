package cucumber.contrib.formatter.model;

public class Statistics {
    private int nbScenarioSuccessed;
    private int nbScenarioSkipped;
    private int nbScenarioFailed;
    private int nbScenarioOther;
    private int nbScenarioPending;
    private int nbStepSuccessed;
    private int nbFeature;
    private int nbStepOther;
    private int nbStepPending;
    private int nbStepSkipped;
    private int nbStepFailed;
    private int nbStepNoMatching;

    public void feature() {
        nbFeature++;
    }

    public void stepOther() {
        nbStepOther++;
    }

    public void stepPending() {
        nbStepPending++;
    }

    public void stepSkipped() {
        nbStepSkipped++;
    }

    public void stepFailed() {
        nbStepFailed++;
    }

    public void stepNoMatching() {
        nbStepNoMatching++;
    }

    public void stepSuccessed() {
        nbStepSuccessed++;
    }

    public void scenarioOther() {
        nbScenarioOther++;
    }

    public void scenarioPending() {
        nbScenarioPending++;
    }

    public void scenarioFailed() {
        nbScenarioFailed++;
    }

    public void scenarioSuccessed() {
        nbScenarioSuccessed++;
    }

    public void scenarioSkipped() {
        nbScenarioSkipped++;
    }

}