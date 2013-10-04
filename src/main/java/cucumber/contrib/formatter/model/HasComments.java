package cucumber.contrib.formatter.model;

import gherkin.formatter.model.Comment;

import java.util.List;

public interface HasComments {
    List<Comment> getComments();
}
