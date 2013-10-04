package cucumber.contrib.formatter.model;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.Step;
import org.junit.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.Assertions.assertThat;

public class ScenarioWrapperTest {

    @Test
    public void with_Scenario_comment_should_escape_double_sharp_lines() throws Exception {
        ScenarioWrapper scenarioWrapper = new ScenarioWrapper(new Scenario(null, null, null, null, null, null, null));
        List<Comment> comments = newArrayList(new Comment("## foo", 42));
        scenarioWrapper.step(new Step(comments, null, null, null, null, null));

        assertThat(scenarioWrapper.getDescription()).isEmpty();
    }
}
