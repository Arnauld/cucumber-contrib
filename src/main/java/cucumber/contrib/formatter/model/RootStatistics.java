package cucumber.contrib.formatter.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gherkin.formatter.model.Tag;

import java.util.List;
import java.util.Map;

public class RootStatistics extends BasicStatistics {

    private Filter filter;
    private Map<String, Statistics> tagStatistics = Maps.newHashMap();


    public RootStatistics(Filter filter) {
        super();
        this.filter = filter;
    }

    public void consolidate(FeatureWrapper featureWrapper) {
        incFeature();
        for (ScenarioWrapper scenario : featureWrapper.getScenarios()) {
            Statistics statistics = composeThisWithTagStatistics(scenario);
            consolidate(statistics, filter, scenario);
        }
    }

    public Map<String, Statistics> getTagStatistics() {
        return tagStatistics;
    }

    private Statistics composeThisWithTagStatistics(ScenarioWrapper scenario) {
        List<Statistics> instances = Lists.newArrayList();
        for (Tag tag : scenario.getTags()) {
            instances.add(getStatisticsForTag(tag.getName()));
        }

        if (instances.isEmpty()) {
            return this;
        }

        instances.add(this);
        return CompositeStatistics.compose(instances);
    }

    private Statistics getStatisticsForTag(String name) {
        Statistics statistics = tagStatistics.get(name);
        if (statistics == null) {
            statistics = new BasicStatistics();
            tagStatistics.put(name, statistics);
        }
        return statistics;
    }

    private static void consolidate(Statistics statisticsToUse,
                                    Filter filter,
                                    ScenarioWrapper scenarioWrapper)
    {

        boolean isManual = filter != null && filter.isManual(scenarioWrapper);
        if (isManual) {
            statisticsToUse.incScenarioManual();
            for (StepWrapper step : scenarioWrapper.getSteps()) {
                // don't even care of step status
                statisticsToUse.incStepManual();
            }
            return;
        }

        for (StepWrapper step : scenarioWrapper.getSteps()) {
            consolidate(statisticsToUse, step);
        }

        updateScenarioOverallStatusStatistics(statisticsToUse, scenarioWrapper);
    }

    private static void updateScenarioOverallStatusStatistics(Statistics statisticsToUse,
                                                              ScenarioWrapper scenarioWrapper)
    {
        for (StepWrapper step : scenarioWrapper.getSteps()) {
            if (step.isFailure()) {
                statisticsToUse.incScenarioFailed();
                return;
            }
            else if (step.isSkipped()) {
                statisticsToUse.incScenarioSkipped();
                return;
            }
            else if (step.isPending()) {
                statisticsToUse.incScenarioPending();
                return;
            }
            else if (!step.isSuccess()) {
                statisticsToUse.incScenarioOther();
                return;
            }
        }
        statisticsToUse.incScenarioSucceeded();
    }

    private static void consolidate(Statistics statisticsToUse, StepWrapper stepWrapper) {
        if (!stepWrapper.isMatching()) {
            statisticsToUse.incStepNoMatching();
        }
        else if (stepWrapper.isFailure()) {
            statisticsToUse.incStepFailed();
        }
        else if (stepWrapper.isSkipped()) {
            statisticsToUse.incStepSkipped();
        }
        else if (stepWrapper.isPending()) {
            statisticsToUse.incStepPending();
        }
        else if (stepWrapper.isSuccess()) {
            statisticsToUse.incStepSucceeded();
        }
        else {
            statisticsToUse.incStepOther();
        }
    }
}