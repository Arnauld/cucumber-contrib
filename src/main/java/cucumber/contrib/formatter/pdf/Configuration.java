package cucumber.contrib.formatter.pdf;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
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

    //

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

    public Configuration withSubTitleFont(Font subTitleFont) {
        this.subTitleFont = subTitleFont;
        return this;
    }

    public Font subTitleFont() {
        if(subTitleFont == null)
            subTitleFont = FontFactory.getFont(defaultFontName(), 18, Font.ITALIC, getPrimaryColor());
        return subTitleFont;
    }

    //

    public Font versionTitleFont() {
        return FontFactory.getFont(defaultMonospaceFontname(), 14, Font.ITALIC, getPrimaryColor());
    }

    public Configuration withChapterTitleFont(Font chapterTitleFont) {
        this.chapterTitleFont = chapterTitleFont;
        return this;
    }

    public Font chapterTitleFont() {
        if(chapterTitleFont == null)
            chapterTitleFont = FontFactory.getFont(defaultFontName(), 16, Font.BOLD, getPrimaryColor());
        return chapterTitleFont;
    }

    public Configuration withSectionTitleFont(Font sectionTitleFont) {
        this.sectionTitleFont = sectionTitleFont;
        return this;
    }

    public Font sectionTitleFont() {
        if(sectionTitleFont == null)
            sectionTitleFont = FontFactory.getFont(defaultFontName(), 14, Font.BOLD, getPrimaryColor());
        return sectionTitleFont;
    }

    public Configuration withSubSectionTitleFont(Font subSectionTitleFont) {
        this.subSectionTitleFont = subSectionTitleFont;
        return this;
    }

    public Font subSectionTitleFont() {
        if(subSectionTitleFont == null)
            subSectionTitleFont = FontFactory.getFont(defaultFontName(), 12, Font.BOLD, getPrimaryColor());
        return subSectionTitleFont;
    }

    public Configuration withDefaultFont(Font defaultFont) {
        this.defaultFont = defaultFont;
        return this;
    }

    public Font defaultFont() {
        if(defaultFont == null)
            defaultFont = FontFactory.getFont(defaultFontName(), 12, Font.NORMAL, getDefaultColor());
        return defaultFont;
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

    public Configuration withStepDataTableHeaderFont(Font stepDataTableHeaderFont) {
        this.stepDataTableHeaderFont = stepDataTableHeaderFont;
        return this;
    }

    public Font stepDataTableHeaderFont() {
        if(stepDataTableHeaderFont == null)
            stepDataTableHeaderFont = FontFactory.getFont(defaultMonospaceFontname(), 8, Font.BOLD, BaseColor.WHITE);
        return stepDataTableHeaderFont;
    }

    public Configuration withStepDataTableContentFont(Font stepDataTableContentFont) {
        this.stepDataTableContentFont = stepDataTableContentFont;
        return this;
    }

    public Font stepDataTableContentFont() {
        if(stepDataTableContentFont == null)
            stepDataTableContentFont = stepDefaultFont();
        return stepDataTableContentFont;
    }

    public Configuration withStepDataTableHeaderBackground(BaseColor stepDataTableHeaderBackground) {
        this.stepDataTableHeaderBackground = stepDataTableHeaderBackground;
        return this;
    }

    public BaseColor stepDataTableHeaderBackground() {
        if(stepDataTableHeaderBackground == null)
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
        if(firstPageFooterTemplateText == null && !Strings.isNullOrEmpty(generationDateFormat))
            firstPageFooterTemplateText = getFormattedGenerationDate();

        String pageFooterTemplateText = this.pageFooterTemplateText;
        if(pageFooterTemplateText == null)
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
                getPrimaryColor());
    }

    private String getFormattedGenerationDate() {
        return new SimpleDateFormat(generationDateFormat).format(new Date());
    }

    public BaseColor getDefaultColor() {
        return BaseColor.BLACK;
    }

    public Configuration withPrimaryColor(BaseColor primaryColor) {
        this.primaryColor = primaryColor;
        return this;
    }

    public BaseColor getPrimaryColor() {
        if(primaryColor == null)
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
            if(element instanceof PdfPTable)
                extendTableToWidth((PdfPTable) element, document.right() - document.left());
            document.add(element);
        }
    }

    public static void extendTableToWidth(PdfPTable element, float width) throws DocumentException {
        element.setTotalWidth(width);

        float[] absoluteWidths = element.getAbsoluteWidths();
        if(element.getNumberOfColumns() < 10) {
            float sum = sum(absoluteWidths);
            float minWidth = sum / 10; // at least 10% of the table
            for(int i=0;i<absoluteWidths.length;i++) {
                absoluteWidths[i] = Math.max(minWidth, absoluteWidths[i]);
            }
        }

        int[] widths = new int[element.getNumberOfColumns()];
        for(int i=0; i<widths.length; i++)
            widths[i] = 1;
        element.setWidths(absoluteWidths);
    }

    private static float sum(float[] values) {
        float sum = 0;
        for(float v : values)
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
        if(title == null)
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
        if(pageFooterFont == null)
            pageFooterFont = FontFactory.getFont(defaultFontName(), 10, Font.ITALIC, getPrimaryColor());
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
        if(pageHeaderFont == null)
            pageHeaderFont = FontFactory.getFont(defaultFontName(), 10, Font.ITALIC, getPrimaryColor());
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
        if(tocEntryFont == null)
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
        Map<String,String> styles = new HashMap<String,String>();
        styles.put(CSS.Property.TEXT_ALIGN, "justify");
        return styles;
    }

    private Map<String, String> liCssStyles() {
        Map<String,String> styles = new HashMap<String,String>();
        styles.put(CSS.Property.COLOR, toRGBColor(getDefaultColor()));
        return styles;
    }

    protected Map<String, String> ulCssStyles() {
        Map<String,String> styles = new HashMap<String,String>();
        styles.put(CSS.Property.COLOR, toRGBColor(getPrimaryColor()));
        styles.put(CSS.Property.LIST_STYLE_TYPE, "square");
        styles.put(CSS.Property.LIST_STYLE, "1em");
        return styles;
    }

    protected Map<String, String> bodyCssStyles() {
        Map<String,String> styles = new HashMap<String,String>();
        fillCSSProperties(styles, defaultFont());
        return styles;
    }

    protected Map<String, String> h1CssStyles() {
        Map<String,String> styles = new HashMap<String,String>();
        fillCSSProperties(styles, chapterTitleFont());
        styles.put("page-break-before", "always");
        styles.put(CSS.Property.PADDING_BOTTOM, "2em");
        styles.put(CSS.Property.PADDING_TOP, "1em");
        return styles;
    }

    protected Map<String, String> h2CssStyles() {
        Map<String,String> styles = new HashMap<String,String>();
        fillCSSProperties(styles, sectionTitleFont());
        styles.put(CSS.Property.PADDING_BOTTOM, "2em");
        styles.put(CSS.Property.PADDING_TOP, "1em");
        return styles;
    }

    private void fillCSSProperties(Map<String, String> styles, Font font) {
        if(font.isBold())
            styles.put(CSS.Property.FONT_STYLE, CSS.Value.BOLD);
        styles.put(CSS.Property.FONT_SIZE, font.getSize() + "pt");
        styles.put(CSS.Property.COLOR, toRGBColor(font.getColor()));
    }

    public static String toRGBColor(BaseColor color) {
        return "rgb(" + color.getRed() + ", " + color.getGreen() + ", " + color.getBlue() + ")";
    }

}
