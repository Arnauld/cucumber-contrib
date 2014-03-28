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
        String math = "" +
                "    \\Re{z} =\\frac{ n\\pi \\dfrac{\\theta +\\psi}{2}}{\n" +
                "                   \\left( \\dfrac{\\theta +\\psi}{2}\\right)^2 + " +
                "                   \\left( \\dfrac{1}{2} \\log \\left \\vert \\dfrac{B}{A} \\right \\vert \\right)^2}";

        TeXFormula formula = new TeXFormula(math);
        formula.createPNG(TeXConstants.STYLE_DISPLAY, 40, "/Users/arnauld/Projects/cucumber-jvm-series/cucumber-contrib/tmp/latex.png", Color.WHITE, Color.BLUE);
    }
}
