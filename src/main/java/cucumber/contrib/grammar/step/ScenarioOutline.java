package cucumber.contrib.grammar.step;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class ScenarioOutline {

    private String visualName;
    private List<Scenario> exampleScenarios = Lists.newArrayList();

    public void setVisualName(String visualName) {
        this.visualName = visualName;
    }

    public String getVisualName() {
        return visualName;
    }

    public void add(Scenario exampleScenario) {
        exampleScenarios.add(exampleScenario);
    }

    public void traverse(FeatureVisitor visitor) {
        visitor.enterScenarioOutline(this);
        for (Scenario scenario : exampleScenarios)
            scenario.traverse(visitor);
        visitor.exitScenarioOutline(this);
    }
}
