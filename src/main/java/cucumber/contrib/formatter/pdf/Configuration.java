package cucumber.contrib.formatter.pdf;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.CMYKColor;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEvent;
import com.itextpdf.tool.xml.Tag;
import com.itextpdf.tool.xml.css.CSS;
import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.html.HTML;
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
import java.util.*;
import java.util.List;

import static com.google.common.io.Resources.asByteSource;
import static com.google.common.io.Resources.getResource;

/**
 *
 */
public class Configuration {

    public static final Charset UTF8 = Charset.forName("UTF-8");
    //
    public static final String META_AUTHOR = "author";
    public static final String META_TITLE = "title";
    public static final String META_SUBJECT = "subject";
    public static final String META_KEYWORDS = "keywords";
    public static final String META_VERSION = "version";
    public static final String META_GENERATION_DATE_FORMAT = "generation-date-format";
    private static final String META_IMAGE_ROOT_PATH = "image-root-path";

    // TODO extract to 'TemplateEngine' thus one can plug an other template engine
    private MarkdownEmitter markdownEmitter;
    //
    private ContentUpdater firstPageContentProvider = new DefaultFirstPageContentProvider();

    // Footer
    private String firstPageFooterTemplateText;
    private String pageFooterTemplateText;
    private Phrase pageFooter;
    private Font pageFooterFont;

    // Header
    private String firstPageHeaderTemplateText;
    private String pageHeaderTemplateText;
    private Phrase pageHeader;
    private Font pageHeaderFont;

    //
    private Rectangle pageSize = PageSize.A4;
    private Margin documentMargin = new Margin(50);
    private Margin descriptionMargin = new Margin(20, 0, 25, 25);
    private Margin scenarioMargin = new Margin(0, 0, 20, 20);
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
    private String imageRootPath;
    private BaseColor primaryColor;
    private Font defaultFont;
    private Font mainTitleFont;
    private Font subTitleFont;
    private Font chapterTitleFont;
    private Font sectionTitleFont;
    private Font subSectionTitleFont;
    private BaseColor tableHeaderForeground;
    private BaseColor tableContentForeground;
    private Font tableHeaderFont;
    private Font tableContentFont;
    private Font tocEntryFont;
    private Font stepDataTableHeaderFont;
    private Font stepDataTableContentFont;
    private BaseColor stepDataTableHeaderBackground;
    private String defaultFontName;
    private Font versionTitleFont;
    private BaseColor tableHeaderBackground;
    //
    private String reportFilename = "report.pdf";
    //
    private boolean displayUri = true;
    private boolean displayTags = true;
    private boolean unbreakableScenario = false;

    //

    public Configuration() {
    }


    public String getReportFilename() {
        return reportFilename;
    }

    public Configuration withReportFilename(String reportFilename) {
        this.reportFilename = reportFilename;
        return this;
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

    public Configuration withDocumentMargin(float margin) {
        return withDocumentMargin(new Margin(margin));
    }

    public Configuration withDocumentMargin(float marginLeftRight, float marginTopBottom) {
        return withDocumentMargin(new Margin(marginLeftRight, marginTopBottom));
    }

    public Configuration withDocumentMargin(Margin documentMargin) {
        this.documentMargin = documentMargin;
        return this;
    }

    public Margin getDocumentMargin() {
        return documentMargin;
    }

    public Configuration withDescriptionMargin(float margin) {
        return withDescriptionMargin(new Margin(margin));
    }

    public Configuration withDescriptionMargin(float marginLeft, float marginRight, float marginTop, float marginBottom) {
        return withDescriptionMargin(new Margin(marginLeft, marginRight, marginTop, marginBottom));
    }

    public Configuration withDescriptionMargin(Margin descriptionMargin) {
        this.descriptionMargin = descriptionMargin;
        return this;
    }

    public Margin getDescriptionMargin() {
        return descriptionMargin;
    }

    public Configuration withScenarioMargin(float marginLeft, float marginRight, float marginTop, float marginBottom) {
        return withScenarioMargin(new Margin(marginLeft, marginRight, marginTop, marginBottom));
    }

    public Configuration withScenarioMargin(Margin scenarioMargin) {
        this.scenarioMargin = scenarioMargin;
        return this;
    }

    public Margin getScenarioMargin() {
        return scenarioMargin;
    }

    public Document createDocument() {
        return new Document(pageSize,
                documentMargin.marginLeft,
                documentMargin.marginRight,
                documentMargin.marginTop,
                documentMargin.marginBottom);
    }

    public Rectangle getDocumentArtBox() {
        return new Rectangle(
                documentMargin.marginLeft,
                documentMargin.marginBottom,
                pageSize.getWidth() - documentMargin.marginRight,
                pageSize.getHeight() - documentMargin.marginTop);
    }

    public Configuration withDefaultFontName(String fontName) {
        this.defaultFontName = fontName;
        return this;
    }

    public String defaultFontName() {
        if (defaultFontName == null)
            defaultFontName = FontFactory.HELVETICA;
        return defaultFontName;
    }

    public Configuration withMainTitleFont(Font mainTitleFont) {
        this.mainTitleFont = mainTitleFont;
        return this;
    }

    /**
     * @see #primaryColor()
     * @see #defaultFontName()
     */
    public Font mainTitleFont() {
        if (mainTitleFont == null)
            mainTitleFont = FontFactory.getFont(defaultFontName(), 32, Font.ITALIC, primaryColor());
        return mainTitleFont;
    }

    public Configuration withSubTitleFont(Font subTitleFont) {
        this.subTitleFont = subTitleFont;
        return this;
    }

    /**
     * @see #primaryColor()
     * @see #defaultFontName()
     */
    public Font subTitleFont() {
        if (subTitleFont == null)
            subTitleFont = FontFactory.getFont(defaultFontName(), 18, Font.ITALIC, primaryColor());
        return subTitleFont;
    }

    //

    public Configuration withVersionTitleFont(Font versionTitleFont) {
        this.versionTitleFont = versionTitleFont;
        return this;
    }

    /**
     * @see #primaryColor()
     * @see #defaultMonospaceFontname()
     */
    public Font versionTitleFont() {
        if (versionTitleFont == null)
            versionTitleFont = FontFactory.getFont(defaultMonospaceFontname(), 14, Font.ITALIC, primaryColor());
        return versionTitleFont;
    }

    public Configuration withChapterTitleFont(Font chapterTitleFont) {
        this.chapterTitleFont = chapterTitleFont;
        return this;
    }

    /**
     * @see #primaryColor()
     * @see #defaultFontName()
     */
    public Font chapterTitleFont() {
        if (chapterTitleFont == null)
            chapterTitleFont = FontFactory.getFont(defaultFontName(), 16, Font.BOLD, primaryColor());
        return chapterTitleFont;
    }

    public Configuration withSectionTitleFont(Font sectionTitleFont) {
        this.sectionTitleFont = sectionTitleFont;
        return this;
    }

    /**
     * @see #primaryColor()
     * @see #defaultFontName()
     */
    public Font sectionTitleFont() {
        if (sectionTitleFont == null)
            sectionTitleFont = FontFactory.getFont(defaultFontName(), 14, Font.BOLD, primaryColor());
        return sectionTitleFont;
    }

    public Configuration withSubSectionTitleFont(Font subSectionTitleFont) {
        this.subSectionTitleFont = subSectionTitleFont;
        return this;
    }

    /**
     * @see #primaryColor()
     * @see #defaultFontName()
     */
    public Font subSectionTitleFont() {
        if (subSectionTitleFont == null)
            subSectionTitleFont = FontFactory.getFont(defaultFontName(), 12, Font.BOLD, primaryColor());
        return subSectionTitleFont;
    }

    public Configuration withDefaultFont(Font defaultFont) {
        this.defaultFont = defaultFont;
        return this;
    }

    /**
     * @see #defaultColor()
     * @see #defaultFontName()
     */
    public Font defaultFont() {
        if (defaultFont == null)
            defaultFont = FontFactory.getFont(defaultFontName(), 12, Font.NORMAL, defaultColor());
        return defaultFont;
    }

    /**
     * @see #defaultColor()
     * @see #defaultFontName()
     */
    public Font defaultStrongFont() {
        return FontFactory.getFont(defaultFontName(), 12, Font.BOLD, defaultColor());
    }

    /**
     * @see #primaryColor()
     * @see #defaultFontName()
     */
    public Font defaultMetaFont() {
        return FontFactory.getFont(defaultFontName(), 8, Font.NORMAL, primaryColor());
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

    public Configuration withTableHeaderBackground(BaseColor tableHeaderBackground) {
        this.tableHeaderBackground = tableHeaderBackground;
        return this;
    }

    public BaseColor tableHeaderBackground() {
        if (tableHeaderBackground == null)
            tableHeaderBackground = Colors.CYAN;
        return tableHeaderBackground;
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

    public Configuration withStepDataTableHeaderFont(Font stepDataTableHeaderFont) {
        this.stepDataTableHeaderFont = stepDataTableHeaderFont;
        return this;
    }

    public Font stepDataTableHeaderFont() {
        if (stepDataTableHeaderFont == null)
            stepDataTableHeaderFont = FontFactory.getFont(defaultMonospaceFontname(), 8, Font.BOLD, BaseColor.WHITE);
        return stepDataTableHeaderFont;
    }

    public Configuration withStepDataTableContentFont(Font stepDataTableContentFont) {
        this.stepDataTableContentFont = stepDataTableContentFont;
        return this;
    }

    public Font stepDataTableContentFont() {
        if (stepDataTableContentFont == null)
            stepDataTableContentFont = stepDefaultFont();
        return stepDataTableContentFont;
    }

    public Configuration withStepDataTableHeaderBackground(BaseColor stepDataTableHeaderBackground) {
        this.stepDataTableHeaderBackground = stepDataTableHeaderBackground;
        return this;
    }

    public BaseColor stepDataTableHeaderBackground() {
        if (stepDataTableHeaderBackground == null)
            stepDataTableHeaderBackground = tableHeaderBackground();
        return stepDataTableHeaderBackground;
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

        String firstPageHeaderTemplateText = this.firstPageHeaderTemplateText;
        String pageHeaderTemplateText = this.pageHeaderTemplateText;

        String firstPageFooterTemplateText = this.firstPageFooterTemplateText;
        if (firstPageFooterTemplateText == null && !Strings.isNullOrEmpty(generationDateFormat))
            firstPageFooterTemplateText = getFormattedGenerationDate();

        String pageFooterTemplateText = this.pageFooterTemplateText;
        if (pageFooterTemplateText == null)
            pageFooterTemplateText = title;

        return new HeaderFooter(
                firstPageHeaderTemplateText,
                pageHeaderTemplateText,
                pageHeaderFont(),
                pageHeader,
                firstPageFooterTemplateText,
                pageFooterTemplateText,
                pageFooterFont(),
                pageFooter,
                primaryColor());
    }

    private String getFormattedGenerationDate() {
        return new SimpleDateFormat(generationDateFormat).format(new Date());
    }

    public BaseColor defaultColor() {
        return BaseColor.BLACK;
    }

    public Configuration withPrimaryColor(BaseColor primaryColor) {
        this.primaryColor = primaryColor;
        return this;
    }

    public BaseColor primaryColor() {
        if (primaryColor == null)
            primaryColor = Colors.DARK_RED;
        return primaryColor;
    }

    public void writePreambule(Document document) throws DocumentException {
        String preambule = getPreambule();
        if (preambule == null) {
            return;
        }

        List<Element> elements = markdownContent(preambule);
        for (Element element : elements) {
            if (element instanceof PdfPTable)
                extendTableToWidth((PdfPTable) element, document.right() - document.left());
            document.add(element);
        }
    }

    public static void extendTableToWidth(PdfPTable element, float width) throws DocumentException {
        element.setTotalWidth(width);

        float[] absoluteWidths = element.getAbsoluteWidths();
        if (element.getNumberOfColumns() < 10) {
            float sum = sum(absoluteWidths);
            float minWidth = sum / 10; // at least 10% of the table
            for (int i = 0; i < absoluteWidths.length; i++) {
                absoluteWidths[i] = Math.max(minWidth, absoluteWidths[i]);
            }
        }

        int[] widths = new int[element.getNumberOfColumns()];
        for (int i = 0; i < widths.length; i++)
            widths[i] = 1;
        element.setWidths(absoluteWidths);
    }

    private static float sum(float[] values) {
        float sum = 0;
        for (float v : values)
            sum += v;
        return sum;
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
        elements.addAll(markdownContent(markdownText));
    }

    public List<Element> markdownContent(String markdownText) {
        return getMarkdownEmitter().markdownToElements(markdownText);
    }

    public Chapter createTitledChapter(String title) {
        if (title == null)
            throw new IllegalArgumentException();

        Paragraph titleParagraph = new Paragraph(title, chapterTitleFont());
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

    public Configuration withFirstPageFooterTemplateText(String firstPageFooterTemplateText) {
        this.firstPageFooterTemplateText = firstPageFooterTemplateText;
        return this;
    }

    public Configuration withPageFooterTemplateText(String pageFooterTemplateText) {
        this.pageFooterTemplateText = pageFooterTemplateText;
        return this;
    }

    public Configuration withPageFooterFont(Font pageFooterFont) {
        this.pageFooterFont = pageFooterFont;
        return this;
    }

    public Font pageFooterFont() {
        if (pageFooterFont == null)
            pageFooterFont = FontFactory.getFont(defaultFontName(), 10, Font.ITALIC, primaryColor());
        return pageFooterFont;
    }

    public Configuration withPageFooter(Phrase pageFooter) {
        this.pageFooter = pageFooter;
        return this;
    }

    public Configuration withFirstPageHeaderTemplateText(String firstPageHeaderTemplateText) {
        this.firstPageHeaderTemplateText = firstPageHeaderTemplateText;
        return this;
    }

    public Configuration withPageHeaderTemplateText(String pageHeaderTemplateText) {
        this.pageHeaderTemplateText = pageHeaderTemplateText;
        return this;
    }

    private Font pageHeaderFont() {
        if (pageHeaderFont == null)
            pageHeaderFont = FontFactory.getFont(defaultFontName(), 10, Font.ITALIC, primaryColor());
        return pageHeaderFont;
    }

    public Configuration withPageHeaderFont(Font pageHeaderFont) {
        this.pageHeaderFont = pageHeaderFont;
        return this;
    }

    public Configuration withPageHeader(Phrase pageHeader) {
        this.pageHeader = pageHeader;
        return this;
    }


    public Font tocEntryFont() {
        if (tocEntryFont == null)
            tocEntryFont = defaultFont();
        return tocEntryFont;
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

    public void resolveDefaultStyles(Tag t) {
        t.setCSS(bodyCssStyles());
    }

    public void fillDefaultHtmlCSS(CssFile cssFile) {
        cssFile.add(HTML.Tag.BASE, bodyCssStyles());
        cssFile.add(HTML.Tag.BODY, bodyCssStyles());
        cssFile.add(HTML.Tag.H1, h1CssStyles());
        cssFile.add(HTML.Tag.H2, h2CssStyles());
        cssFile.add(HTML.Tag.UL, ulCssStyles());
        cssFile.add(HTML.Tag.LI, liCssStyles());
        cssFile.add(HTML.Tag.P, pCssStyles());
    }

    private Map<String, String> pCssStyles() {
        Map<String, String> styles = new HashMap<String, String>();
        styles.put(CSS.Property.TEXT_ALIGN, "justify");
        return styles;
    }

    private Map<String, String> liCssStyles() {
        Map<String, String> styles = new HashMap<String, String>();
        styles.put(CSS.Property.COLOR, toRGBColor(defaultColor()));
        return styles;
    }

    protected Map<String, String> ulCssStyles() {
        Map<String, String> styles = new HashMap<String, String>();
        styles.put(CSS.Property.COLOR, toRGBColor(primaryColor()));
        styles.put(CSS.Property.LIST_STYLE_TYPE, "square");
        styles.put(CSS.Property.LIST_STYLE, "1em");
        return styles;
    }

    protected Map<String, String> bodyCssStyles() {
        Map<String, String> styles = new HashMap<String, String>();
        fillCSSProperties(styles, defaultFont());
        return styles;
    }

    protected Map<String, String> h1CssStyles() {
        Map<String, String> styles = new HashMap<String, String>();
        fillCSSProperties(styles, chapterTitleFont());
        styles.put("page-break-before", "always");
        styles.put(CSS.Property.PADDING_BOTTOM, "2em");
        styles.put(CSS.Property.PADDING_TOP, "1em");
        return styles;
    }

    protected Map<String, String> h2CssStyles() {
        Map<String, String> styles = new HashMap<String, String>();
        fillCSSProperties(styles, sectionTitleFont());
        styles.put(CSS.Property.PADDING_BOTTOM, "2em");
        styles.put(CSS.Property.PADDING_TOP, "1em");
        return styles;
    }

    private void fillCSSProperties(Map<String, String> styles, Font font) {
        if (font.isBold())
            styles.put(CSS.Property.FONT_STYLE, CSS.Value.BOLD);
        styles.put(CSS.Property.FONT_SIZE, font.getSize() + "pt");
        styles.put(CSS.Property.COLOR, toRGBColor(font.getColor()));
    }

    public static String toRGBColor(BaseColor color) {
        return "rgb(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ")";
    }

    public boolean shouldDisplayUri() {
        return displayUri;
    }

    public Configuration displayUri(boolean displayUri) {
        this.displayUri = displayUri;
        return this;
    }

    public boolean shouldDisplayTags() {
        return displayTags;
    }


    public Configuration displayTags(boolean displayTags) {
        this.displayTags = displayTags;
        return this;
    }

    public Configuration unbreakableScenario(boolean unbreakableScenario) {
        this.unbreakableScenario = unbreakableScenario;
        return this;
    }

    public boolean shouldKeepScenarioUnbreakable() {
        return unbreakableScenario;
    }
}
