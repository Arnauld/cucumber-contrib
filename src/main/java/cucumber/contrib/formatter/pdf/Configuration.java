package cucumber.contrib.formatter.pdf;

import static com.google.common.io.Resources.asByteSource;
import static com.google.common.io.Resources.getResource;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfPageEvent;
import com.itextpdf.text.pdf.draw.LineSeparator;
import cucumber.contrib.formatter.BricABrac;
import cucumber.contrib.formatter.FormatterException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 *
 */
public class Configuration {

    public static final String META_AUTHOR = "author";
    public static final String META_TITLE = "title";
    public static final String META_SUBJECT = "subject";
    public static final String META_KEYWORDS = "keywords";
    public static final String META_VERSION = "version";
    public static final String META_GENERATION_DATE_FORMAT = "generation-date-format";
    public static final Charset UTF8 = Charset.forName("UTF-8");

    // TODO extract to 'TemplateEngine' thus one can plug an other template engine
    private MarkdownEmitter markdownEmitter;
    //
    private String title;
    private String subject;
    private String version;
    private java.util.List<String> authors = Lists.newArrayList();
    //
    private String generationDateFormat = "'Generated' yyyy/MM/dd HH:mm:ssZ";
    private int chapterCount = 0;
    private String preambule;
    private String keywords;


    private MarkdownEmitter getMarkdownEmitter() {
        if (markdownEmitter == null) {
            markdownEmitter = new MarkdownEmitter(this);
        }
        return markdownEmitter;
    }

    public Document createDocument() {
        return new Document(PageSize.A4, 50, 50, 50, 50);
    }

    public Rectangle getDocumentArtBox() {
        return new Rectangle(50, 50, 545, 792);
    }

    private Font footerFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC, getPrimaryColor());
    }

    public Font mainTitleFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 32, Font.ITALIC, getPrimaryColor());
    }

    public Font subTitleFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 18, Font.ITALIC, getPrimaryColor());
    }

    public Font versionTitleFont() {
        return FontFactory.getFont(FontFactory.COURIER, 14, Font.ITALIC, getPrimaryColor());
    }

    //

    public Font chapterTitleFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD, getPrimaryColor());
    }

    public Font sectionTitleFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD, getPrimaryColor());
    }

    public Font defaultFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, getMainColor());
    }

    public Font defaultStrongFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD, getMainColor());
    }

    protected Font defaultMetaFont() {
        return FontFactory.getFont(FontFactory.HELVETICA, 8, Font.NORMAL, getPrimaryColor());
    }


    public Font featureTitleFont() {
        return chapterTitleFont();
    }

    public Font scenarioTitleFont() {
        return sectionTitleFont();
    }

    public Font stepKeywordFont() {
        return FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD, new CMYKColor(255, 255, 0, 17));
    }

    public Font stepDefaultFont() {
        return FontFactory.getFont(FontFactory.COURIER, 8, Font.NORMAL, new CMYKColor(255, 255, 0, 17));
    }

    public Font tagsFont() {
        return FontFactory.getFont(FontFactory.COURIER, 8, Font.ITALIC, new CMYKColor(25, 255, 255, 17));
    }

    public Font stepDataTableHeaderFont() {
        return FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD, BaseColor.WHITE);
    }

    public Font stepDataTableContentFont() {
        return stepDefaultFont();
    }

    public BaseColor stepDataTableHeaderBackground() {
        return Colors.CYAN;
    }

    public BaseColor stepDataTableRowAlternateBackground() {
        return Colors.VERY_LIGHT_GRAY;
    }

    public String getPreambule() {
        return preambule;
    }

    public void writeMetaInformations(Document document) {
        if (!authors.isEmpty()) {
            document.addAuthor(BricABrac.join(authors, ", "));
        }
        if (!Strings.isNullOrEmpty(keywords)) {
            document.addKeywords(keywords);
        }
        if (!Strings.isNullOrEmpty(title)) {
            document.addTitle(title);
        }
        if (!Strings.isNullOrEmpty(subject)) {
            document.addSubject(subject);
        }
    }


    public void writeFirstPages(Document document) throws DocumentException {
        int data = 0;
        Paragraph preface = new Paragraph();
        Paragraph lastLine = addEmptyLines(preface, 1);
        lastLine.setSpacingAfter(200.0f);

        if (!Strings.isNullOrEmpty(title)) {
            Font font = mainTitleFont();
            Paragraph paragraph = new Paragraph(title, font);
            paragraph.setAlignment(Element.ALIGN_RIGHT);
            paragraph.setSpacingAfter(15.0f);
            LineSeparator line = new LineSeparator(1, 45, font.getColor(), Element.ALIGN_RIGHT, -10);
            paragraph.add(line);
            preface.add(paragraph);
            data++;
        }

        if (!Strings.isNullOrEmpty(subject)) {
            Paragraph paragraph = new Paragraph(subject, subTitleFont());
            paragraph.setAlignment(Element.ALIGN_RIGHT);
            paragraph.setSpacingAfter(10.0f);
            //
            preface.add(paragraph);
            data++;
        }

        if (!Strings.isNullOrEmpty(version)) {
            Paragraph paragraph = new Paragraph(version, versionTitleFont());
            paragraph.setAlignment(Element.ALIGN_RIGHT);
            paragraph.setSpacingAfter(10.0f);
            preface.add(paragraph);
            data++;
        }

        document.add(preface);
        if (data > 0) {
            document.newPage();
        }
    }

    public PdfPageEvent createHeaderFooter() {

        String pageFooter = "";
        String firstPageFooter = "";

        if (!Strings.isNullOrEmpty(title)) {
            pageFooter = title;
        }

        if (!Strings.isNullOrEmpty(generationDateFormat)) {
            firstPageFooter = getFormattedGenerationDate();
        }

        return new HeaderFooter(firstPageFooter, pageFooter, getPrimaryColor(), footerFont());
    }


    private String getFormattedGenerationDate() {
        return new SimpleDateFormat(generationDateFormat).format(new Date());
    }

    private BaseColor getMainColor() {
        return BaseColor.BLACK;
    }

    private BaseColor getPrimaryColor() {
        return Colors.DARK_RED;
    }

    private Paragraph addEmptyLines(Paragraph owner, int nb) {
        Paragraph lastLine = null;
        for (int i = 0; i < nb; i++) {
            lastLine = new Paragraph(" ");
            owner.add(lastLine);
        }
        return lastLine;
    }

    public void writePreambule(Document document) throws DocumentException {
        String preambule = getPreambule();
        if (preambule == null) {
            return;
        }

        Paragraph paragraph = new Paragraph();
        paragraph.addAll(getMarkdownEmitter().markdownToElements(preambule));
        document.add(paragraph);
    }

    private String loadResource(URL resource, Charset charset) {
        try {
            ByteSource source = asByteSource(resource);
            CharSource charSource = source.asCharSource(charset);
            return charSource.read();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void appendMarkdownContent(final java.util.List<Element> elements, String markdownText) {
        elements.addAll(getMarkdownEmitter().markdownToElements(markdownText));
    }

    public Chapter createTitledChapter(String title) {
        Paragraph titleParagraph = new Paragraph(title, featureTitleFont());
        titleParagraph.setSpacingBefore(10f);
        titleParagraph.setSpacingAfter(10f);
        Chapter chapter = new Chapter(titleParagraph, ++chapterCount);
        chapter.setNumberDepth(2);
        return chapter;
    }

    public boolean shouldEmitSummary() {
        return true;
    }

    public Configuration withPreambule(String preambule) {
        this.preambule = preambule;
        return this;
    }


    public Configuration withPreambuleResource(Class<?> contextClass, String resourcePath) {
        URL resource = getResource(contextClass, resourcePath);
        return withPreambule(loadResource(resource, UTF8));
    }

    public Configuration withMetaInformationsResources(Class<?> contextClass, String resourcePath) {
        URL resource = getResource(contextClass, resourcePath);
        InputStream inputStream = null;
        try {
            Properties properties = new Properties();
            inputStream = resource.openStream();
            properties.load(new InputStreamReader(inputStream, UTF8));
            return withMetaInformations(properties);
        }
        catch (IOException ioe) {
            throw new FormatterException(
                    "Failed to load meta informations from properties (resource: " + resource + ")", ioe);
        }
        finally {
            BricABrac.closeQuietly(inputStream);
        }
    }

    public Configuration withMetaInformations(Properties properties) {
        return withAuthor(properties.getProperty(META_AUTHOR))
                .withTitle(properties.getProperty(META_TITLE, title))
                .withKeywords(properties.getProperty(META_KEYWORDS, keywords))
                .withSubject(properties.getProperty(META_SUBJECT, subject))
                .withVersion(properties.getProperty(META_VERSION, version))
                .withGenerationDateFormat(properties.getProperty(META_GENERATION_DATE_FORMAT, generationDateFormat));
    }

    private Configuration withGenerationDateFormat(String generationDateFormat) {
        this.generationDateFormat = generationDateFormat;
        return this;
    }

    public Configuration withAuthor(String author) {
        if (author != null) {
            this.authors.add(author);
        }
        return this;
    }

    public Configuration withTitle(String title) {
        this.title = title;
        return this;
    }

    public Configuration withKeywords(String keywords) {
        this.keywords = keywords;
        return this;
    }

    public Configuration withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public Configuration withVersion(String version) {
        this.version = version;
        return this;
    }

}
