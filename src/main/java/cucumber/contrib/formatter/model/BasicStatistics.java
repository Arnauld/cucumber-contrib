package cucumber.contrib.formatter.model;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class BasicStatistics implements Statistics {

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


    @Override
    public void incFeature() {
        nbFeature++;
    }

    @Override
    public void incStepOther() {
        nbStepOther++;
    }

    @Override
    public void incStepPending() {
        nbStepPending++;
    }

    @Override
    public void incStepSkipped() {
        nbStepSkipped++;
    }

    @Override
    public void incStepFailed() {
        nbStepFailed++;
    }

    @Override
    public void incStepNoMatching() {
        nbStepNoMatching++;
    }

    @Override
    public void incStepSucceeded() {
        nbStepSucceeded++;
    }

    @Override
    public void incStepManual() {
        nbStepManual++;
    }

    @Override
    public void incScenarioOther() {
        nbScenarioOther++;
    }

    @Override
    public void incScenarioPending() {
        nbScenarioPending++;
    }

    @Override
    public void incScenarioFailed() {
        nbScenarioFailed++;
    }

    @Override
    public void incScenarioManual() {
        nbScenarioManual++;
    }

    @Override
    public void incScenarioSucceeded() {
        nbScenarioSucceeded++;
    }

    @Override
    public void incScenarioSkipped() {
        nbScenarioSkipped++;
    }

    @Override
    public int getNbScenarioSucceeded() {
        return nbScenarioSucceeded;
    }

    @Override
    public int getNbScenarioSkipped() {
        return nbScenarioSkipped;
    }

    @Override
    public int getNbScenarioFailed() {
        return nbScenarioFailed;
    }

    @Override
    public int getNbScenarioOther() {
        return nbScenarioOther;
    }

    @Override
    public int getNbScenarioPending() {
        return nbScenarioPending;
    }

    @Override
    public int getNbStepSucceeded() {
        return nbStepSucceeded;
    }

    @Override
    public int getNbScenarioManual() {
        return nbScenarioManual;
    }

    @Override
    public int getNbStepManual() {
        return nbStepManual;
    }

    @Override
    public int getNbFeature() {
        return nbFeature;
    }

    @Override
    public int getNbStepOther() {
        return nbStepOther;
    }

    @Override
    public int getNbStepPending() {
        return nbStepPending;
    }

    @Override
    public int getNbStepSkipped() {
        return nbStepSkipped;
    }

    @Override
    public int getNbStepFailed() {
        return nbStepFailed;
    }

    @Override
    public int getNbStepNoMatching() {
        return nbStepNoMatching;
    }

    @Override
    public int getNbScenarioExceptManual() {
        return getNbScenarioFailed() +
                getNbScenarioOther() +
                getNbScenarioPending() +
                getNbScenarioSkipped() +
                getNbScenarioSucceeded();
    }

    @Override
    public int getNbScenario() {
        return getNbScenarioExceptManual() +
                getNbScenarioManual();
    }

    @Override
    public int getNbStepsExceptManual() {
        return getNbStepFailed() +
                getNbStepOther() +
                getNbStepNoMatching() +
                getNbStepPending() +
                getNbStepSkipped() +
                getNbStepSucceeded();
    }

    @Override
    public int getNbSteps() {
        return  getNbStepsExceptManual() +
                getNbStepManual();
    }
}
