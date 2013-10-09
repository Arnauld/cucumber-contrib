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

    public int getNbScenarioSucceeded() {
        return nbScenarioSucceeded;
    }

    public int getNbScenarioSkipped() {
        return nbScenarioSkipped;
    }

    public int getNbScenarioFailed() {
        return nbScenarioFailed;
    }

    public int getNbScenarioOther() {
        return nbScenarioOther;
    }

    public int getNbScenarioPending() {
        return nbScenarioPending;
    }

    public int getNbStepSucceeded() {
        return nbStepSucceeded;
    }

    public int getNbFeature() {
        return nbFeature;
    }

    public int getNbStepOther() {
        return nbStepOther;
    }

    public int getNbStepPending() {
        return nbStepPending;
    }

    public int getNbStepSkipped() {
        return nbStepSkipped;
    }

    public int getNbStepFailed() {
        return nbStepFailed;
    }

    public int getNbStepNoMatching() {
        return nbStepNoMatching;
    }

    public int getNbScenario() {
        return getNbScenarioFailed() +
                getNbScenarioOther() +
                getNbScenarioPending() +
                getNbScenarioSkipped() +
                getNbScenarioSucceeded();
    }

    public int getNbSteps() {
        return getNbStepFailed() +
                getNbStepOther() +
                getNbStepNoMatching() +
                getNbStepPending() +
                getNbStepSkipped() +
                getNbStepSucceeded();
    }
}