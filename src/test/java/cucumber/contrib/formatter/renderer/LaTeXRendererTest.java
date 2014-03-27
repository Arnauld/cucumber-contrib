package cucumber.contrib.formatter.renderer;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;

import java.awt.*;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class LaTeXRendererTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();


    @Test
    public void usecase() {
        String math = "\\frac {V_m} {K_M+S}";

        TeXFormula formula = new TeXFormula(math);
        formula.createPNG(TeXConstants.STYLE_DISPLAY, 40, "/Users/arnauld/Projects/cucumber-jvm-series/cucumber-contrib/tmp/latex.png", Color.WHITE, Color.BLUE);
    }
}
