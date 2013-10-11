package cucumber.contrib.formatter.model;

import gherkin.formatter.model.*;

import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static cucumber.contrib.formatter.BricABrac.areEqualsIgnoringCase;
import static cucumber.contrib.formatter.BricABrac.isEmpty;

public class StepWrapper implements Wrapper, HasComments {
    private final Step step;
    private Result result;
    private Match match;

    public StepWrapper(Step step) {
        this.step = step;
    }

    public boolean isOutlined() {
        return !isEmpty(step.getOutlineArgs());
    }

    public void result(Result result) {
        this.result = result;
    }

    public void match(Match match) {
        this.match = match;
    }

    public boolean isMatching() {
        return !isNullOrEmpty(match.getLocation());
    }

    public boolean isSuccess() {
        return areEqualsIgnoringCase("passed", result.getStatus());
    }

    public boolean isSkipped() {
        return areEqualsIgnoringCase("skipped", result.getStatus());
    }

    public boolean isPending() {
        return areEqualsIgnoringCase("pending", result.getStatus());
    }

    public boolean isFailure() {
        return areEqualsIgnoringCase("failed", result.getStatus());
    }

    public List<Comment> getComments() {
        return step.getComments();
    }

    public String getKeyword() {
        return step.getKeyword();
    }

    public String getName() {
        return step.getName();
    }

    public boolean hasTable() {
        List<DataTableRow> tableRows = getTableRows();
        return tableRows != null && !tableRows.isEmpty();
    }

    public List<DataTableRow> getTableRows() {
        return step.getRows();
    }
}
