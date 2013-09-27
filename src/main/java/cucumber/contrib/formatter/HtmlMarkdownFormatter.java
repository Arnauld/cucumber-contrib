package cucumber.contrib.formatter;

import gherkin.formatter.Formatter;
import gherkin.formatter.NiceAppendable;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.Background;
import gherkin.formatter.model.Examples;
import gherkin.formatter.model.Feature;
import gherkin.formatter.model.Match;
import gherkin.formatter.model.Result;
import gherkin.formatter.model.Scenario;
import gherkin.formatter.model.ScenarioOutline;
import gherkin.formatter.model.Step;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cucumber.runtime.CucumberException;
import cucumber.runtime.io.ClasspathResourceLoader;
import cucumber.runtime.io.Resource;

/**
 * Direct copy of {#link cucumber.runtime.formatter.HTMLFormatter} to customize it.
 */
public class HtmlMarkdownFormatter implements Formatter, Reporter {
    
    private static final String HTML_FILENAME = "index.html";
    private static final String ASSETS_ROOT = "spec/formatter/";
    
    @SuppressWarnings("serial")
    private static final Map<String, String> MIME_TYPES_EXTENSIONS = new HashMap<String, String>() {
        {
            put("image/bmp", "bmp");
            put("image/gif", "gif");
            put("image/jpeg", "jpg");
            put("image/png", "png");
            put("video/ogg", "ogg");
        }
    };

    private final File htmlReportDir;
    private NiceAppendable jsOut;

    private int embeddedIndex;
    private HtmlMarkdownReport report;

    public SpecFormatter(File htmlReportDir) {
        this.htmlReportDir = htmlReportDir;
        this.report = new HtmlMarkdownReport(jsOut());
        this.copyReportFiles();
    }

    /* (non-Javadoc)
     * @see gherkin.formatter.Formatter#uri(java.lang.String)
     */
    @Override
    public void uri(String uri) {
        report.uri(uri);
        System.out.println("SpecFormatter.uri(" + uri + ")");
    }

    /* (non-Javadoc)
     * @see gherkin.formatter.Formatter#feature(gherkin.formatter.model.Feature)
     */
    @Override
    public void feature(Feature feature) {
        System.out.println("SpecFormatter.feature(" + feature.toMap() + ")");
        report.feature(feature);
    }

    @Override
    public void background(Background background) {
        System.out.println("SpecFormatter.background(" + background.toMap() + ")");
        report.background(background);
    }

    @Override
    public void scenario(Scenario scenario) {
        System.out.println("SpecFormatter.scenario(" + scenario.toMap() + ")");
        report.scenario(scenario);
    }

    @Override
    public void scenarioOutline(ScenarioOutline scenarioOutline) {
        System.out.println("SpecFormatter.scenarioOutline(" + scenarioOutline.toMap() + ")");
        report.scenarioOutline(scenarioOutline);
    }

    @Override
    public void examples(Examples examples) {
        System.out.println("SpecFormatter.examples(" + examples.toMap() + ")");
        report.examples(examples);
    }

    @Override
    public void step(Step step) {
        System.out.println("SpecFormatter.step(" + step.toMap() + ")");
        report.step(step);
    }

    @Override
    public void eof() {
        System.out.println("SpecFormatter.eof()");
        report.eof();
    }

    @Override
    public void syntaxError(String state, String event, List<String> legalEvents, String uri, Integer line) {
        System.out.println("SpecFormatter.syntaxError(" + state + ", " + event + ", " + uri + ", " + line + ")");
    }

    @Override
    public void done() {
        System.out.println("SpecFormatter.done()");
        report.done();
    }

    @Override
    public void close() {
        System.out.println("SpecFormatter.close()");
        jsOut().close();
    }

//    private void writeToJsReport(String functionName, String arg) {
//        String stringArg = gson.toJson(arg);
//        jsOut().append(JS_FORMATTER_VAR).append(".").append(functionName).append("(").append(stringArg).append(");").println();
//    }
//
//    private void writeToJsReport(String functionName, Mappable arg) {
//        String stringArg = gson.toJson(arg.toMap());
//        jsOut().append(JS_FORMATTER_VAR).append(".").append(functionName).append("(").append(stringArg).append(");").println();
//    }


    @Override
    public void match(Match match) {
        System.out.println("SpecFormatter.match(" + match.toMap() + ")");
        report.match(match);
    }
    
    @Override
    public void result(Result result) {
        System.out.println("SpecFormatter.result(" + result.toMap() + ")");
        report.result(result);
    }

    @Override
    public void before(Match match, Result result) {
        System.out.println("SpecFormatter.before(" + match.toMap() + ", " + result + ")");
        throw new UnsupportedOperationException();
    }

    @Override
    public void after(Match match, Result result) {
        System.out.println("SpecFormatter.after(" + match.toMap() + ", " + result + ")");
        throw new UnsupportedOperationException();
    }


    @Override
    public void embedding(String mimeType, byte[] data) {
        System.out.println("SpecFormatter.embedding(" + mimeType + ")");
        // Creating a file instead of using data urls to not clutter the js file
        String extension = MIME_TYPES_EXTENSIONS.get(mimeType);
        if (extension != null) {
            StringBuilder fileName = new StringBuilder("embedded").append(embeddedIndex++).append(".").append(extension);
            writeBytesAndClose(data, reportFileOutputStream(fileName.toString()));
            //writeToJsReport("embedding", new StringBuilder("'").append(mimeType).append("','").append(fileName).append("'").toString());
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void write(String text) {
        System.out.println("SpecFormatter.write(" + text + ")");
        //writeToJsReport("write", text);
        throw new UnsupportedOperationException();
    }

    private void copyReportFiles() {
        Iterable<Resource> resources = 
                new ClasspathResourceLoader(getClass().getClassLoader()).resources(ASSETS_ROOT, null);
        for(Resource resource : resources) {
            String textAsset = resource.getPath().substring(ASSETS_ROOT.length());
            try {
                InputStream textAssetStream = resource.getInputStream();
                
                if(textAssetStream == null) {
                    throw new CucumberException("Couldn't find " + textAsset + ". Is cucumber-html on your classpath? Make sure you have the right version.");
                }
                //String baseName = new File(textAsset).getName();
                writeStreamAndClose(textAssetStream, reportFileOutputStream(textAsset));
                
            } catch (IOException e) {
                throw new CucumberException("Couldn't find " + textAsset + ". Is cucumber-spec on your classpath? Make sure you have the right version.", e);
            }
        }
    }

    private void writeStreamAndClose(InputStream in, OutputStream out) {
        byte[] buffer = new byte[16 * 1024];
        try {
            int len = in.read(buffer);
            while (len != -1) {
                out.write(buffer, 0, len);
                len = in.read(buffer);
            }
            out.close();
        } catch (IOException e) {
            throw new CucumberException("Unable to write to report file item: ", e);
        }
    }

    private void writeBytesAndClose(byte[] buf, OutputStream out) {
        try {
            out.write(buf);
        } catch (IOException e) {
            throw new CucumberException("Unable to write to report file item: ", e);
        }
    }

    private NiceAppendable jsOut() {
        if (jsOut == null) {
            try {
                jsOut = new NiceAppendable(new OutputStreamWriter(reportFileOutputStream(HTML_FILENAME), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new CucumberException(e);
            }
        }
        return jsOut;
    }

    private OutputStream reportFileOutputStream(String fileName) {
        htmlReportDir.mkdirs();
        File file = new File(htmlReportDir, fileName);
        file.getParentFile().mkdirs();
        try {
            return new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new CucumberException("Error creating file: " + file.getAbsolutePath(), e);
        }
    }

}
