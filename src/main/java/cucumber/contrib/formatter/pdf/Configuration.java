package cucumber.contrib.formatter.pdf;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEvent;
import com.itextpdf.tool.xml.pipeline.html.AbstractImageProvider;
import com.itextpdf.tool.xml.pipeline.html.ImageProvider;
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
import java.util.List;
import java.util.Properties;

import static com.google.common.io.Resources.asByteSource;
import static com.google.common.io.Resources.getResource;

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
    private static final String META_IMAGE_ROOT_PATH = "image-root-path";
    // TODO extract to 'TemplateEngine' thus one can plug an other template engine
    private MarkdownEmitter markdownEmitter;
    //
    private ContentUpdater firstPageContentProvider = new DefaultFirstPageContentProvider();
    private String title;
    private String subject;
    private String version;
    private java.util.List<String> authors = Lists.newArrayList();
    //
    private String generationDateFormat = "'Generated' yyyy/MM/dd HH:mm:ssZ";
    private int chapterCount = 0;
    private String preambule;
    private String keywords;
    private String imageRootPath;
    private Font mainTitleFont;
    private BaseColor tableHeaderForeground;
    private BaseColor tableContentForeground;
    private Font tableHeaderFont;
    private Font tableContentFont;

    public Configuration() {
    }

    public static Paragraph addEmptyLines(Paragraph owner, int nb) {
        Paragraph lastLine = null;
        for (int i = 0; i < nb; i++) {
            lastLine = new Paragraph(" ");
            owner.add(lastLine);
        }
        return lastLine;
    }

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
        return FontFactory.getFont(defaultFontName(), 10, Font.ITALIC, getPrimaryColor());
    }

    private String defaultFontName() {
        return FontFactory.HELVETICA;
    }

    public Configuration withMainTitleFont(Font mainTitleFont) {
        this.mainTitleFont = mainTitleFont;
        return this;
    }

    public Font mainTitleFont() {
        if (mainTitleFont == null)
            mainTitleFont = FontFactory.getFont(defaultFontName(), 32, Font.ITALIC, getPrimaryColor());
        return mainTitleFont;
    }

    public Font subTitleFont() {
        return FontFactory.getFont(defaultFontName(), 18, Font.ITALIC, getPrimaryColor());
    }

    //

    public Font versionTitleFont() {
        return FontFactory.getFont(defaultMonospaceFontname(), 14, Font.ITALIC, getPrimaryColor());
    }

    public Font chapterTitleFont() {
        return FontFactory.getFont(defaultFontName(), 16, Font.BOLD, getPrimaryColor());
    }

    public Font sectionTitleFont() {
        return FontFactory.getFont(defaultFontName(), 14, Font.BOLD, getPrimaryColor());
    }

    public Font defaultFont() {
        return FontFactory.getFont(defaultFontName(), 12, Font.NORMAL, getDefaultColor());
    }

    public Font defaultStrongFont() {
        return FontFactory.getFont(defaultFontName(), 12, Font.BOLD, getDefaultColor());
    }

    protected Font defaultMetaFont() {
        return FontFactory.getFont(defaultFontName(), 8, Font.NORMAL, getPrimaryColor());
    }

    public Configuration withTableHeaderForeground(BaseColor tableHeaderForeground) {
        this.tableHeaderForeground = tableHeaderForeground;
        return this;
    }

    public BaseColor tableHeaderForeground() {
        if (tableHeaderForeground == null)
            tableHeaderForeground = BaseColor.BLACK;
        return tableHeaderForeground;
    }

    public Configuration withTableContentForeground(BaseColor tableContentForeground) {
        this.tableContentForeground = tableContentForeground;
        return this;
    }

    public BaseColor tableContentForeground() {
        if (tableContentForeground == null)
            tableContentForeground = BaseColor.BLACK;
        return tableContentForeground;
    }

    public BaseColor tableHeaderBackground() {
        return Colors.CYAN;
    }

    public BaseColor tableAlternateBackground() {
        return Colors.VERY_LIGHT_GRAY;
    }

    public Configuration withTableContentFont(Font tableContentFont) {
        this.tableContentFont = tableContentFont;
        return this;
    }

    public Font tableContentFont() {
        if (tableContentFont == null)
            tableContentFont = FontFactory.getFont(defaultFontName(), 10, Font.NORMAL, tableContentForeground());
        return tableContentFont;
    }

    public Configuration withTableHeaderFont(Font tableHeaderFont) {
        this.tableHeaderFont = tableHeaderFont;
        return this;
    }

    public Font tableHeaderFont() {
        if (tableHeaderFont == null)
            tableHeaderFont = FontFactory.getFont(defaultFontName(), 10, Font.BOLD, tableHeaderForeground());
        return tableHeaderFont;
    }

    public Font featureTitleFont() {
        return chapterTitleFont();
    }

    public Font scenarioTitleFont() {
        return sectionTitleFont();
    }

    public Font stepKeywordFont() {
        return FontFactory.getFont(defaultMonospaceFontname(), 8, Font.BOLD, new CMYKColor(255, 255, 0, 17));
    }

    private String defaultMonospaceFontname() {
        return FontFactory.COURIER;
    }

    public Font stepDefaultFont() {
        return FontFactory.getFont(defaultMonospaceFontname(), 8, Font.NORMAL, new CMYKColor(255, 255, 0, 17));
    }

    public Font tagsFont() {
        return FontFactory.getFont(defaultMonospaceFontname(), 8, Font.ITALIC, new CMYKColor(25, 255, 255, 17));
    }

    public Font stepDataTableHeaderFont() {
        return FontFactory.getFont(defaultMonospaceFontname(), 8, Font.BOLD, BaseColor.WHITE);
    }

    public Font stepDataTableContentFont() {
        return stepDefaultFont();
    }

    public BaseColor stepDataTableHeaderBackground() {
        return tableHeaderBackground();
    }

    public BaseColor stepDataTableRowAlternateBackground() {
        return tableAlternateBackground();
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
        if (firstPageContentProvider != null) {
            firstPageContentProvider.update(this, document);
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

    public BaseColor getDefaultColor() {
        return BaseColor.BLACK;
    }

    public BaseColor getPrimaryColor() {
        return Colors.DARK_RED;
    }

    public void writePreambule(Document document) throws DocumentException {
        String preambule = getPreambule();
        if (preambule == null) {
            return;
        }

        List<Element> elements = getMarkdownEmitter().markdownToElements(preambule);
        for (Element element : elements) {
            if(element instanceof PdfPTable)
                extendTableToPage((PdfPTable)element, document);
            document.add(element);
        }
    }

    private void extendTableToPage(PdfPTable element, Document document) throws DocumentException {
        float width = document.right() - document.left();
        element.setTotalWidth(width);
        element.setWidths(element.getAbsoluteWidths());
    }

    private String loadResource(URL resource, Charset charset) {
        try {
            ByteSource source = asByteSource(resource);
            CharSource charSource = source.asCharSource(charset);
            return charSource.read();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
        } catch (IOException ioe) {
            throw new FormatterException(
                    "Failed to load meta informations from properties (resource: " + resource + ")", ioe);
        } finally {
            BricABrac.closeQuietly(inputStream);
        }
    }

    public Configuration withMetaInformations(Properties properties) {
        return withAuthor(properties.getProperty(META_AUTHOR))
                .withTitle(properties.getProperty(META_TITLE, title))
                .withKeywords(properties.getProperty(META_KEYWORDS, keywords))
                .withSubject(properties.getProperty(META_SUBJECT, subject))
                .withVersion(properties.getProperty(META_VERSION, version))
                .withImageRootPath(properties.getProperty(META_IMAGE_ROOT_PATH, imageRootPath))
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

    public Configuration withFirstPageContentProvider(ContentUpdater firstPageContentProvider) {
        this.firstPageContentProvider = firstPageContentProvider;
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

    public String manualTag() {
        return "@manual";
    }

    public ImageProvider getImageProvider() {
        return new AbstractImageProvider() {

            @Override
            public Image retrieve(String src) {
                return super.retrieve(src);
            }

            @Override
            public String getImageRootPath() {
                throw new FormatterException("ImageRootPath");
            }
        };
    }

    public String getImageRootPath() {
        return imageRootPath;
    }

    public Configuration withImageRootPath(String imageRootPath) {
        this.imageRootPath = imageRootPath;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public String getSubject() {
        return subject;
    }

    public String getVersion() {
        return version;
    }
}
