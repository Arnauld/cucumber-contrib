package cucumber.contrib.formatter.model;

import gherkin.formatter.Argument;
import gherkin.formatter.model.*;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;
import static cucumber.contrib.formatter.util.BricABrac.areEqualsIgnoringCase;
import static cucumber.contrib.formatter.util.BricABrac.isEmpty;

public class StepWrapper implements Wrapper, HasComments {

    private final Step step;

    private List<Embedding> embeddings = new ArrayList<Embedding>();
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

    public boolean isComplete() {
        return match!=null && result!=null;
    }

    public static class Tok {
        public final String value;
        public final boolean param;

        public Tok(String value, boolean param) {
            this.value = value;
            this.param = param;
        }
    }

    public List<Tok> tokenizeBody() {
        String full = getName();
        int lastIndex = 0;
        List<Tok> toks = new ArrayList<Tok>();
        for(Argument arg : match.getArguments()) {
            if(arg.getOffset() > lastIndex) {
                toks.add(new Tok(full.substring(lastIndex, arg.getOffset()), false));
            }
            toks.add(new Tok(arg.getVal(), true));
            lastIndex = arg.getOffset() + arg.getVal().length();
        }
        if(lastIndex < full.length())
            toks.add(new Tok(full.substring(lastIndex), false));
        return toks;
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

    public boolean hasDocString() {
        return step.getDocString() != null;
    }

    public DocString getDocString() {
        return step.getDocString();
    }

    public List<DataTableRow> getTableRows() {
        return step.getRows();
    }

    public void embedding(Embedding embedding) {
        embeddings.add(embedding);
    }

    public List<Embedding> getEmbeddings() {
        return embeddings;
    }

    @Override
    public String toString() {
        return "StepWrapper{" +
                "step=" + step +
                '}';
    }

}
