package cucumber.contrib.formatter.model;

import gherkin.formatter.model.*;

import java.util.List;

import static cucumber.contrib.formatter.BricABrac.areEquals;
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
        return !isEmpty(match.getLocation());
    }

    public boolean isSuccess() {
        return areEquals("passed", result.getStatus());
    }

    public boolean isSkipped() {
        return areEquals("skipped", result.getStatus());
    }

    public boolean isPending() {
        return areEquals("pending", result.getStatus());
    }

    public boolean isFailure() {
        return areEquals("failed", result.getStatus());
    }

    @Override
    public void consolidate(Statistics statistics) {
        if (!isMatching()) {
            statistics.stepNoMatching();
        } else if (isFailure()) {
            statistics.stepFailed();
        } else if (isSkipped()) {
            statistics.stepSkipped();
        } else if (isPending()) {
            statistics.stepPending();
        } else if (isSuccess()) {
            statistics.stepSuccessed();
        } else {
            statistics.stepOther();
        }
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
        return tableRows!=null && !tableRows.isEmpty();
    }

    public List<DataTableRow> getTableRows() {
        return step.getRows();
    }
}
