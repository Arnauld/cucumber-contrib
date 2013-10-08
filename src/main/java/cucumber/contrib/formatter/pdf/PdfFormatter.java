package cucumber.contrib.formatter.pdf;

import com.itextpdf.text.DocumentException;
import cucumber.contrib.formatter.FormatterException;
import cucumber.contrib.formatter.model.FeatureWrapper;
import cucumber.contrib.formatter.model.ModelBasedReport;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class PdfFormatter implements Formatter, Reporter {

    private final File reportDir;
    private ModelBasedReport report;
    private PdfEmitter pdfEmitter;
    private Configuration configuration;

    public PdfFormatter(File reportDir) throws FileNotFoundException {
        this.reportDir = reportDir;
        this.reportDir.mkdirs();
    }

    private Configuration getConfiguration() {
        if(configuration == null) {
            configuration = createConfiguration();
        }
        return configuration;
    }

    protected Configuration createConfiguration() {
        return new Configuration();
    }

    private ModelBasedReport getReport() {
        if(report == null) {
            report = new ModelBasedReport() {
                @Override
                protected void emit(FeatureWrapper currentFeature) {
                    getPdfEmitter().emit(currentFeature);
                }
            };
        }
        return report;
    }

    private PdfEmitter getPdfEmitter() {
        if (pdfEmitter == null) {
            pdfEmitter = new PdfEmitter(getConfiguration());
            try {
                pdfEmitter.init(new File(reportDir, "report.pdf"));
            } catch (FileNotFoundException e) {
                throw new FormatterException("Failed to create report file", e);
            } catch (DocumentException e) {
                throw new FormatterException("Failed to create report file", e);
            }
        }
        return pdfEmitter;
    }

    @Override
    public void uri(String uri) {
        getReport().uri(uri);
    }

    @Override
    public void feature(Feature feature) {
        getReport().feature(feature);
    }

    @Override
    public void background(Background background) {
        getReport().background(background);
    }

    @Override
    public void scenario(Scenario scenario) {
        getReport().scenario(scenario);
    }

    @Override
    public void scenarioOutline(ScenarioOutline scenarioOutline) {
        getReport().scenarioOutline(scenarioOutline);
    }

    @Override
    public void examples(Examples examples) {
        getReport().examples(examples);
    }

    @Override
    public void step(Step step) {
        getReport().step(step);
    }

    @Override
    public void syntaxError(String s, String s2, List<String> strings, String s3, Integer integer) {
        // TODO
    }

    @Override
    public void eof() {
        System.out.println("PdfFormatter.eof");
    }

    @Override
    public void done() {
        System.out.println("PdfFormatter.done");
        getReport().done();
        getPdfEmitter().done();
    }

    @Override
    public void close() {
        System.out.println("PdfFormatter.close");
    }

    @Override
    public void before(Match match, Result result) {
        // TODO
    }

    @Override
    public void after(Match match, Result result) {
        // TODO
    }

    @Override
    public void result(Result result) {
        getReport().result(result);
    }

    @Override
    public void match(Match match) {
        getReport().match(match);
    }

    @Override
    public void embedding(String s, byte[] bytes) {
        // TODO
    }

    @Override
    public void write(String s) {
        // TODO
    }

}
