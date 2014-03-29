package cucumber.contrib.formatter.pegdown;

import cucumber.contrib.formatter.util.BricABrac;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class LaTeXEquationToHtmlPlugin extends ImageBasedToHtmlPlugin {

    private Logger log = LoggerFactory.getLogger(LaTeXEquationToHtmlPlugin.class);
    private final Color foregroundColor;
    private final Color backgroundColor;
    private final float textFontPointSize;

    public LaTeXEquationToHtmlPlugin(File generationDirectory,
                                     Color foregroundColor,
                                     Color backgroundColor,
                                     float textFontPointSize) {
        super(generationDirectory);
        this.foregroundColor = foregroundColor;
        this.backgroundColor = backgroundColor;
        this.textFontPointSize = textFontPointSize;
    }

    public LaTeXEquationToHtmlPlugin(File generationDirectory) {
        this(generationDirectory, Color.BLACK, Color.WHITE, 20.0f);
    }

    @Override
    protected boolean acceptsNode(NamedBlockNode named) {
        return BricABrac.isOneOfIgnoringCase(named.beginTag(), "latex", "equation", "formula");
    }

    @Override
    protected void generateImage(File pngFile, NamedBlockNode named) throws Exception {
        log.debug("About to generated mathematical formula (LaTeX) as PNG files {}", pngFile.getAbsolutePath());


        TeXFormula formula = createTeXFormula(named.getBody());
        formula.createPNG(TeXConstants.STYLE_DISPLAY, textFontPointSize,
                pngFile.getAbsolutePath(), backgroundColor, foregroundColor);

        log.info("Mathematical formula generated as PNG files {}", pngFile.getAbsolutePath());
    }

    private TeXFormula createTeXFormula(String equation) throws Exception {
        try {
            return new TeXFormula(equation);
        } catch (Exception e) {
            log.error("Invalid Mathematical expression {}", equation, e);
            throw e;
        }
    }

}
