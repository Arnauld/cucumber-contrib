package cucumber.contrib.formatter.model;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public interface Statistics {
    void incFeature();

    void incStepOther();

    void incStepPending();

    void incStepSkipped();

    void incStepFailed();

    void incStepNoMatching();

    void incStepSucceeded();

    void incStepManual();

    void incScenarioOther();

    void incScenarioPending();

    void incScenarioFailed();

    void incScenarioManual();

    void incScenarioSucceeded();

    void incScenarioSkipped();

    int getNbScenarioSucceeded();

    int getNbScenarioSkipped();

    int getNbScenarioFailed();

    int getNbScenarioOther();

    int getNbScenarioPending();

    int getNbStepSucceeded();

    int getNbScenarioManual();

    int getNbStepManual();

    int getNbFeature();

    int getNbStepOther();

    int getNbStepPending();

    int getNbStepSkipped();

    int getNbStepFailed();

    int getNbStepNoMatching();

    int getNbScenarioExceptManual();

    int getNbScenario();

    int getNbStepsExceptManual();

    int getNbSteps();

    public interface Filter {
        boolean isManual(ScenarioWrapper scenarioWrapper);
    }
}
