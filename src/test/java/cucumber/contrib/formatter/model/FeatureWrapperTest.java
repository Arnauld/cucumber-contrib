package cucumber.contrib.formatter.model;

import gherkin.formatter.model.Comment;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Scenario;
import org.junit.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.Assertions.assertThat;

public class FeatureWrapperTest {
    @Test
    public void with_Feature_comment_should_escape_double_sharp_lines() throws Exception {
        FeatureWrapper featureWrapper = new FeatureWrapper("", new Feature(null, null, null, null, null, null, null));
        List<Comment> comments = newArrayList(new Comment("## foo", 42));
        featureWrapper.scenario(new Scenario(comments, null, null, null, null, null, null));

        assertThat(featureWrapper.getDescription()).isEmpty();
    }
}
