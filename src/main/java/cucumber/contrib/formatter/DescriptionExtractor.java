package cucumber.contrib.formatter;

import cucumber.contrib.formatter.model.HasComments;
import cucumber.contrib.formatter.model.StepWrapper;
import gherkin.formatter.model.Comment;

import java.util.List;

public class DescriptionExtractor {

    public static String extractDescription(String description, List<? extends HasComments> hasComments) {
        StringBuilder builder = new StringBuilder();
        if (!BricABrac.isEmpty(description)) {
            builder.append(description).append(BricABrac.NL);
        }
        if (!hasComments.isEmpty()) {
            HasComments step = hasComments.get(0);
            for (Comment comment : step.getComments()) {
                String uncommented = BricABrac.discardCommentChar(comment.getValue());
                if (!BricABrac.startsWithComment(uncommented)) { // double # case
                    builder.append(uncommented).append(BricABrac.NL);
                }
            }
        }
        return builder.toString();
    }
}