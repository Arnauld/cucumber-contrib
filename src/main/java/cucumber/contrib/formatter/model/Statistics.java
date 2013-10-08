package cucumber.contrib.formatter.model;

public class Statistics {
    private int nbScenarioSucceeded;
    private int nbScenarioSkipped;
    private int nbScenarioFailed;
    private int nbScenarioOther;
    private int nbScenarioPending;
    private int nbStepSucceeded;
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

    public void stepSucceeded() {
        nbStepSucceeded++;
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

    public void scenarioSucceeded() {
        nbScenarioSucceeded++;
    }

    public void scenarioSkipped() {
        nbScenarioSkipped++;
    }

}