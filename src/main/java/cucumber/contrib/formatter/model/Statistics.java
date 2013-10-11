package cucumber.contrib.formatter.model;

public class Statistics {

    public interface Filter {
        boolean isManual(ScenarioWrapper scenarioWrapper);
    }

    private int nbFeature;
    //
    private int nbScenarioSucceeded;
    private int nbScenarioSkipped;
    private int nbScenarioFailed;
    private int nbScenarioOther;
    private int nbScenarioPending;
    private int nbScenarioManual;
    //
    private int nbStepSucceeded;
    private int nbStepOther;
    private int nbStepPending;
    private int nbStepSkipped;
    private int nbStepFailed;
    private int nbStepNoMatching;
    private int nbStepManual;

    private Filter filter;

    public Statistics(Filter filter) {
        this.filter = filter;
    }

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

    public void stepManual() {
        nbStepManual++;
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

    public void scenarioManual() {
        nbScenarioManual++;
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

    public int getNbScenarioManual() {
        return nbScenarioManual;
    }

    public int getNbStepManual() {
        return nbStepManual;
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

    public int getNbScenarioExceptManual() {
        return getNbScenarioFailed() +
                getNbScenarioOther() +
                getNbScenarioPending() +
                getNbScenarioSkipped() +
                getNbScenarioSucceeded();
    }

    public int getNbScenario() {
        return getNbScenarioExceptManual() +
                getNbScenarioManual();
    }

    public int getNbStepsExceptManual() {
        return getNbStepFailed() +
                getNbStepOther() +
                getNbStepNoMatching() +
                getNbStepPending() +
                getNbStepSkipped() +
                getNbStepSucceeded();
    }

    public int getNbSteps() {
        return  getNbStepsExceptManual() +
                getNbStepManual();
    }



    public void consolidate(ScenarioWrapper scenarioWrapper) {
        boolean isManual = filter == null ? false : filter.isManual(scenarioWrapper);

        if(isManual) {
            scenarioManual();
            for (StepWrapper step : scenarioWrapper.getSteps()) {
                stepManual();
            }
            return;
        }

        for (StepWrapper step : scenarioWrapper.getSteps()) {
            step.consolidate(this);
        }

        for (StepWrapper step : scenarioWrapper.getSteps()) {
            if (step.isFailure()) {
                scenarioFailed();
                return;
            } else if (step.isSkipped()) {
                scenarioSkipped();
                return;
            } else if (step.isPending()) {
                scenarioPending();
                return;
            } else if (!step.isSuccess()) {
                scenarioOther();
                return;
            }
        }
        scenarioSucceeded();
    }

    public void consolidate(StepWrapper stepWrapper) {
        if (!stepWrapper.isMatching()) {
            stepNoMatching();
        } else if (stepWrapper.isFailure()) {
            stepFailed();
        } else if (stepWrapper.isSkipped()) {
            stepSkipped();
        } else if (stepWrapper.isPending()) {
            stepPending();
        } else if (stepWrapper.isSuccess()) {
            stepSucceeded();
        } else {
            stepOther();
        }
    }
}