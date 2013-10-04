package cucumber.contrib.formatter.pdf;

import com.itextpdf.text.DocumentException;
import cucumber.contrib.formatter.model.FeatureWrapper;
import cucumber.contrib.formatter.model.ModelBasedReport;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 *
 */
public class PdfFormatter implements Formatter, Reporter {


    private final File reportDir;
    private ModelBasedReport report;
    private final PdfEmitter pdfEmitter;

    public PdfFormatter(File reportDir) throws FileNotFoundException, DocumentException {
        reportDir.mkdirs();
        this.reportDir = reportDir;
        this.pdfEmitter = new PdfEmitter();
        this.pdfEmitter.init(new File(reportDir, "report.pdf"));
        this.report = new ModelBasedReport() {
            @Override
            protected void emit(FeatureWrapper currentFeature) {
                pdfEmitter.emit(currentFeature);
            }
        };
    }

    @Override
    public void uri(String uri) {
        report.uri(uri);
    }

    @Override
    public void feature(Feature feature) {
        report.feature(feature);
    }

    @Override
    public void background(Background background) {
        report.background(background);
    }

    @Override
    public void scenario(Scenario scenario) {
        report.scenario(scenario);
    }

    @Override
    public void scenarioOutline(ScenarioOutline scenarioOutline) {
        report.scenarioOutline(scenarioOutline);
    }

    @Override
    public void examples(Examples examples) {
        report.examples(examples);
    }

    @Override
    public void step(Step step) {
        report.step(step);
    }

    @Override
    public void syntaxError(String s, String s2, List<String> strings, String s3, Integer integer) {
        //TODO
    }

    @Override
    public void eof() {
        System.out.println("PdfFormatter.eof");
    }

    @Override
    public void done() {
        System.out.println("PdfFormatter.done");
        report.done();
        pdfEmitter.done();
    }

    @Override
    public void close() {
        System.out.println("PdfFormatter.close");
    }

    @Override
    public void before(Match match, Result result) {
        //TODO
    }

    @Override
    public void after(Match match, Result result) {
        //TODO
    }

    @Override
    public void result(Result result) {
        report.result(result);
    }

    @Override
    public void match(Match match) {
        report.match(match);
    }

    @Override
    public void embedding(String s, byte[] bytes) {
        //TODO
    }

    @Override
    public void write(String s) {
        //TODO
    }
}
