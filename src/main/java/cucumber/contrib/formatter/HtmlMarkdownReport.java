package cucumber.contrib.formatter;

import gherkin.formatter.Argument;
import gherkin.formatter.NiceAppendable;
import gherkin.formatter.model.*;
import org.pegdown.PegDownProcessor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static cucumber.contrib.formatter.BricABrac.NL;

public class HtmlMarkdownReport {

    private final NiceAppendable appendable;
    private String currentUri;
    private FeatureWrapper currentFeature;
    private PegDownProcessor markdown;
    private int indent = 0;
    private boolean formatScenarioComment = true;
    private Statistics statistics;
    private boolean useChartJS = false;

    public HtmlMarkdownReport(NiceAppendable out) {
        this.appendable = out;
        this.markdown = new PegDownProcessor();
        this.statistics = new Statistics();
        startReport();
    }

    private void startReport() {
        out("<!DOCTYPE html>");
        out("<html lang=\"en\">");
        begin("<head>");
        out("<meta charset=\"utf-8\">");
        out("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out("<title>Cucumber Features</title>");
        out("<link href='bootstrap/css/bootstrap.css' rel='stylesheet'>");
        out("<link href='style.css' rel='stylesheet'>");
        out("<script src='jquery-1.8.2.min.js'></script>");
        if (useChartJS) {
            out("<script src='chart.min.js'></script>");
        } else {
            out("<script src='canvasjs.min.js'></script>");
        }
        out("<script src='formatter.js'></script>");
        end("</head>");
        begin("<body>");
        begin("<div class='container cucumber-report'>");
        out("<div class='header'></div>");
        if (useChartJS) {
            out("<canvas id='chart' width='400' height='400'></canvas>");
        } else {
            begin("<div class='row'>");
            out("<div class='span6' id='chartScenario' style='height: 300px'></div>");
            out("<div class='span6' id='chartStep' style='height: 300px'></div>");
            end("</div>");
        }
    }

    private void endReport() {
        end("</div>");
        writeStatistics();
        end("</body>");
        end("</html>");
    }

    private void writeStatistics() {
        begin("<script>");
        if (useChartJS) {
            begin("var data=[");
            out("{ value: " + statistics.nbScenarioFailed + ", color: '#F7464A' },");
            out("{ value: " + statistics.nbScenarioOther + ", color: '#E0E4CC' },");
            out("{ value: " + statistics.nbScenarioPending + ", color: '#F38630' },");
            out("{ value: " + statistics.nbScenarioSkipped + ", color: '#F38630' },");
            out("{ value: " + statistics.nbScenarioSuccessed + ", color: '#69D2E7' }");
            end("];");
            out("var options={};");
            out("var ctx = $('#chart').get(0).getContext('2d');");
            out("new Chart(ctx).Pie(data, options);");
        }
        else {
            begin("var chartScenario = new CanvasJS.Chart('chartScenario',");
            begin("{");
            out("title: {text:'Scenario Summary'},");
            out("legend: {verticalAlign: 'center', horizontalAlign: 'left', fontSize: 20, fontFamily: 'Helvetica'  },");
            out("theme: 'theme2',");
            begin("data: [");
            begin("{");
            out("type:'pie', ");
            out("indexLabelFontFamily: 'Garamond', ");
            out("indexLabelFontSize: 20, ");
            out("startAngle:-20, ");
            out("showInLegend: false, ");
            out("toolTipContent:'Scenario {legendText}: {y}',");
            begin("dataPoints: [");
            out("{ y: " + statistics.nbScenarioFailed + ", legendText: 'Failure', label: 'Failure: ' + " + statistics.nbScenarioFailed + "},");
            out("{ y: " + statistics.nbScenarioOther + ", legendText: 'Other', label: 'Other: ' + " + statistics.nbScenarioOther + "},");
            out("{ y: " + statistics.nbScenarioPending + ", legendText: 'Pending', label: 'Pending: ' + " + statistics.nbScenarioPending + "},");
            out("{ y: " + statistics.nbScenarioSkipped + ", legendText: 'Skipped', label: 'Skipped: ' + " + statistics.nbScenarioSkipped + "},");
            out("{ y: " + statistics.nbScenarioSuccessed + ", legendText: 'Success', label: 'Success: ' + " + statistics.nbScenarioSuccessed + "}");
            end("]");
            end("}");
            end("]");
            end("});");
            out("chartScenario.render();");

            begin("var chartSteps = new CanvasJS.Chart('chartStep',");
            begin("{");
            out("title: {text:'Steps Summary'},");
            out("legend: {verticalAlign: 'center', horizontalAlign: 'left', fontSize: 20, fontFamily: 'Helvetica'  },");
            out("theme: 'theme1',");
            begin("data: [");
            begin("{");
            out("type:'pie', ");
            out("indexLabelFontFamily: 'Garamond', ");
            out("indexLabelFontSize: 20, ");
            out("startAngle:-20, ");
            out("showInLegend: false, ");
            out("toolTipContent:'Step {legendText}: {y}',");
            begin("dataPoints: [");
            out("{ y: " + statistics.nbStepFailed + ", legendText: 'Failure', label: 'Failure: ' + " + statistics.nbStepFailed + " },");
            out("{ y: " + statistics.nbStepOther + ", legendText: 'Other', label: 'Other: ' + " + statistics.nbStepOther + " },");
            out("{ y: " + statistics.nbStepPending + ", legendText: 'Pending', label: 'Pending: ' + " + statistics.nbStepPending + " },");
            out("{ y: " + statistics.nbStepSkipped + ", legendText: 'Skipped', label: 'Skipped: ' + " + statistics.nbStepSkipped + " },");
            out("{ y: " + statistics.nbStepSuccessed + ", legendText: 'Success', label: 'Success: ' + " + statistics.nbStepSuccessed + " },");
            out("{ y: " + statistics.nbStepNoMatching + ", legendText: 'No match', label: 'No match: ' + " + statistics.nbStepNoMatching + " }");
            end("]");
            end("}");
            end("]");
            end("});");
            out("chartSteps.render();");
        }
        end("</script>");
    }

    private void out(String line) {
        indent().append(line).append(NL);
    }

    private void begin(String line) {
        out(line);
        indent++;
    }

    private void end(String line) {
        indent--;
        out(line);
    }

    private NiceAppendable indent() {
        for (int i = 0; i < indent; i++) {
            appendable.append("  ");
        }
        return appendable;
    }

    public void uri(String uri) {
        currentUri = uri;
    }

    public void feature(Feature feature) {
        flushCurrentFeature();
        this.currentFeature = new FeatureWrapper(currentUri, feature);
    }

    private void flushCurrentFeature() {
        if (currentFeature == null) {
            return;
        }
        currentFeature.consolidate(statistics);
        currentFeature.emit(this);
        currentFeature = null;
    }

    public void background(Background background) {
        this.currentFeature.background(background);
    }

    public void scenario(Scenario scenario) {
        currentFeature.scenario(scenario);
    }

    public void scenarioOutline(ScenarioOutline scenarioOutline) {
        throw new UnsupportedOperationException();
    }

    public void examples(Examples examples) {
        throw new UnsupportedOperationException();
    }

    public void step(Step step) {
        currentFeature.step(step);
    }

    public void match(Match match) {
        currentFeature.match(match);
    }

    public void result(Result result) {
        currentFeature.result(result);
    }

    public void done() {
        flushCurrentFeature();
        endReport();
    }

    public void eof() {
    }

    public interface Wrapper {
        void consolidate(Statistics statistics);

        void emit(HtmlMarkdownReport out);
    }

    public class FeatureWrapper implements Wrapper {

        private final Feature feature;
        private final String uri;
        private BackgroundWrapper background;
        private List<ScenarioWrapper> scenarios = new ArrayList<ScenarioWrapper>();

        public FeatureWrapper(String uri, Feature feature) {
            this.uri = uri;
            this.feature = feature;
        }

        public void background(Background background) {
            this.background = new BackgroundWrapper(background);
        }

        public void result(Result result) {
            currentStepContainer().result(result);
        }

        public void match(Match match) {
            currentStepContainer().match(match);
        }

        public void step(Step step) {
            currentStepContainer().step(step);
        }

        public void scenario(Scenario scenario) {
            ScenarioWrapper wrapper = new ScenarioWrapper(scenario);
            if (this.background != null) {
                wrapper.setBackground(background);
                this.background = null;
            }
            this.scenarios.add(wrapper);
        }

        private StepContainer currentStepContainer() {
            return (background != null) ? background : currentScenario();

        }

        private ScenarioWrapper currentScenario() {
            return this.scenarios.get(this.scenarios.size() - 1);
        }

        private String featureNameToAnchor(String name) {
            return "feature-" + name.toLowerCase().replace(' ', '_');
        }

        @Override
        public void emit(HtmlMarkdownReport r) {
            r.begin("<section class='feature'>");
            String featureName = feature.getName();
            r.out("<h1 id='" + featureNameToAnchor(featureName) + "'>" + featureName + "</h1>");
            r.out("<p class='uri'>" + uri + "</p>");
            r.begin("<p class='description'>");
            r.out(formatHtml(feature.getDescription()));
            r.end("</p>");
            r.begin("<div class='scenario-list'>");
            for (ScenarioWrapper scenario : scenarios) {
                scenario.emit(r);
            }
            r.end("</div>");
            r.end("</section>");
        }

        @Override
        public void consolidate(Statistics statistics) {
            statistics.feature();
            for (ScenarioWrapper scenario : scenarios) {
                scenario.consolidate(statistics);
            }
        }
    }

    protected class StepContainer {
        protected List<StepWrapper> steps = new ArrayList<StepWrapper>();
        private int stepCursor = -1; // used by match & result

        public void match(Match match) {
            matchStep().match(match);
        }

        public void result(Result result) {
            resultStep().result(result);
        }

        private StepWrapper matchStep() {
            return this.steps.get(++stepCursor); // match is triggered before
                                                 // result
        }

        private StepWrapper resultStep() {
            return this.steps.get(stepCursor);
        }

        public void step(Step step) {
            steps.add(new StepWrapper(step));
        }
    }

    public class BackgroundWrapper extends StepContainer implements Wrapper {

        public BackgroundWrapper(Background background) {
        }

        @Override
        public void emit(HtmlMarkdownReport out) {
        }

        @Override
        public void consolidate(Statistics statistics) {
        }
    }

    public class ScenarioWrapper extends StepContainer implements Wrapper {
        private final Scenario scenario;
        private BackgroundWrapper background;

        public ScenarioWrapper(Scenario scenario) {
            this.scenario = scenario;
        }

        public void setBackground(BackgroundWrapper background) {
            this.background = background;
        }

        private String scenarioNameToAnchor(String name) {
            return "scenario-" + name.toLowerCase().replace(' ', '_');
        }

        @Override
        public void consolidate(Statistics statistics) {
            for (StepWrapper step : steps) {
                step.consolidate(statistics);
            }

            for (StepWrapper step : steps) {
                if (step.isFailure()) {
                    statistics.scenarioFailed();
                    return;
                } else if (step.isSkipped()) {
                    statistics.scenarioSkipped();
                    return;
                } else if (step.isPending()) {
                    statistics.scenarioPending();
                    return;
                } else if (!step.isSuccess()) {
                    statistics.scenarioOther();
                    return;
                }
            }
            statistics.scenarioSuccessed();
        }

        @Override
        public void emit(HtmlMarkdownReport r) {
            r.begin("<section class='scenario'>");

            String extra = "";
            if (!scenario.getKeyword().equalsIgnoreCase("Scenario")) {
                extra = " <small>(" + scenario.getKeyword() + ")</small>";
            }

            r.out("<h2 id='" + scenarioNameToAnchor(scenario.getName()) + "'>" + scenario.getName() + extra + "</h2>");
            r.begin("<div class='tags'>");
            for (Tag tag : scenario.getTags()) {
                r.out("<span class='tag'>" + tag.getName() + "&nbsp;</span>");
            }
            r.end("</div>");
            r.begin("<p class='description'>");
            r.out(formatHtml(scenario.getDescription()));
            r.end("</p>");
            if (formatScenarioComment && !steps.isEmpty()) {
                List<Comment> comments = steps.get(0).step.getComments();
                StringBuilder commentsInlined = new StringBuilder();
                for (Comment comment : comments) {
                    commentsInlined.append(discardCommentChar(comment.getValue())).append(NL);
                }
                r.begin("<p class='comments'>");
                String formatted = formatHtml(commentsInlined.toString());
                r.out(formatted);
                r.end("</p>");
            }

            r.begin("<ol class='step-list'>");
            for (StepWrapper step : steps)
                step.emit(r);
            r.end("</ol>");
            r.end("</section>");
        }
    }

    public class StepWrapper implements Wrapper {
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

        @Override
        public void emit(HtmlMarkdownReport r) {
            String cssStyles = "step";
            if (!isMatching())
                cssStyles = " step-unmatch";
            r.begin("<li class='" + cssStyles + "'>");
            r.out("<span class='step-status " + result.getStatus() + "'></span>");
            r.out("<span class='step-keyword'>" + step.getKeyword() + "</span>&nbsp;");
            r.out("<span class='step-text'>" + step.getName() + "</span>");

            if (!isEmpty(step.getOutlineArgs())) {
                StringBuilder builder = new StringBuilder();
                for (Argument arg : step.getOutlineArgs()) {
                    if (builder.length() > 0) {
                        builder.append(", ");
                    }
                    builder.append(arg.getVal());
                }
                r.out("<span class='outline-arguments'>(" + builder + ")</span>");
            }

            if (!isEmpty(step.getRows())) {
                boolean firstRow = true;
                r.begin("<div class='parameters'>");
                r.begin("<table class='data-table'>");
                for (Row row : step.getRows()) {
                    String cellPrefix = "<td>";
                    String cellSuffix = "</td>";
                    if (firstRow) {
                        r.begin("<thead>");
                        cellPrefix = "<th>";
                        cellSuffix = "</th>";
                    }

                    r.begin("<tr>");
                    for (String cell : row.getCells()) {
                        r.out(cellPrefix + cell + cellSuffix);
                    }

                    r.end("</tr>");

                    if (firstRow) {
                        r.end("</thead>");
                        r.begin("<tbody>");
                        firstRow = false;
                    }
                }
                r.end("</tbody>");
                r.end("</table>");
                r.end("</div>");
            }

            r.end("</li>");
        }
    }

    public String formatHtml(String text) {
        if (isEmpty(text)) {
            return "";
        }
        return markdown.markdownToHtml(text);
    }

    public static boolean areEquals(String one, String two) {
        if (one == two)
            return true;
        if (one == null || two == null)
            return false;
        return one.equalsIgnoreCase(two);
    }

    private static Pattern COMMENT = Pattern.compile("^\\s*#*");

    public static String discardCommentChar(String value) {
        return COMMENT.matcher(value).replaceAll("");
    }

    public static boolean isEmpty(String location) {
        return location == null || location.isEmpty();
    }

    public static boolean isEmpty(Collection<?> location) {
        return location == null || location.isEmpty();
    }

    public static CharSequence emptyIfNull(String text) {
        if (text == null)
            return "";
        return text;
    }

    public static class Statistics {
        private int nbScenarioSuccessed;
        private int nbScenarioSkipped;
        private int nbScenarioFailed;
        private int nbScenarioOther;
        private int nbScenarioPending;
        private int nbStepSuccessed;
        private int nbFeature;
        private int nbStepOther;
        private int nbStepPending;
        private int nbStepSkipped;
        private int nbStepFailed;
        private int nbStepNoMatching;

        public void feature() {
            nbFeature++;
        }

        public void stepOther() {
            nbStepOther++;
        }

        public void stepPending() {
            nbStepPending++;
        }

        public void stepSkipped() {
            nbStepSkipped++;
        }

        public void stepFailed() {
            nbStepFailed++;
        }

        public void stepNoMatching() {
            nbStepNoMatching++;
        }

        public void stepSuccessed() {
            nbStepSuccessed++;
        }

        public void scenarioOther() {
            nbScenarioOther++;
        }

        public void scenarioPending() {
            nbScenarioPending++;
        }

        public void scenarioFailed() {
            nbScenarioFailed++;
        }

        public void scenarioSuccessed() {
            nbScenarioSuccessed++;
        }

        public void scenarioSkipped() {
            nbScenarioSkipped++;
        }

    }

}
