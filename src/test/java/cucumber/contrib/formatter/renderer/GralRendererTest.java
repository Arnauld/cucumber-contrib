package cucumber.contrib.formatter.renderer;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.Drawable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.io.plots.DrawableWriter;
import de.erichseifert.gral.io.plots.DrawableWriterFactory;
import de.erichseifert.gral.plots.PiePlot;
import de.erichseifert.gral.plots.colors.LinearGradient;
import de.erichseifert.gral.util.Insets2D;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class GralRendererTest {

    /**
     * First corporate color used for normal coloring.
     */
    protected static final Color COLOR1 = new Color(55, 170, 200);
    /**
     * Second corporate color used as signal color
     */
    protected static final Color COLOR2 = new Color(200, 80, 75);

    private static final int SAMPLE_COUNT = 10;

    private Random random;

    @Before
    public void setUp() {
        random = new Random();
    }

    @Test
    public void usecase() throws IOException {
        //  Create data
        DataTable data = new DataTable(Integer.class);
        for (int i = 0; i < SAMPLE_COUNT; i++) {
            int val = random.nextInt(8) + 2;
            int sign = (random.nextDouble() <= 0.15) ? -1 : 1;
            System.out.println("SimplePiePlot.SimplePiePlot(" + val + ", " + sign + ")");
            data.add(val);
        }

        // Create new pie plot
        PiePlot plot = new PiePlot(data);

        // Format plot
        plot.getTitle().setText("Dooonut!");
        // Change relative size of pie
        plot.setRadius(0.9);
        // Display a legend
        plot.setLegendVisible(true);
        // Add some margin to the plot area
        plot.setInsets(new Insets2D.Double(20.0, 40.0, 40.0, 40.0));

        PiePlot.PieSliceRenderer pointRenderer =
                (PiePlot.PieSliceRenderer) plot.getPointRenderer(data);
        // Change relative size of inner region
        pointRenderer.setInnerRadius(0.4);
        // Change the width of gaps between segments
        pointRenderer.setGap(0.2);
        // Change the colors
        LinearGradient colors = new LinearGradient(COLOR1, COLOR2);
        pointRenderer.setColor(colors);
        // Show labels
        pointRenderer.setValueVisible(true);
        pointRenderer.setValueColor(Color.WHITE);
        pointRenderer.setValueFont(Font.decode(null).deriveFont(Font.BOLD, 24));

        File output = new File("/Users/arnauld/Projects/cucumber-jvm-series/cucumber-contrib/tmp/");
        writePNG_byHand(plot, output, "gral-pie.png");
    }

    private void writePNG_byHand(Drawable data, File outputDir, String file) throws IOException {
        int factor = 2;
        int width = factor * 800;
        int height = factor * 600;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setPaintMode();

        DrawingContext ctx = new DrawingContext(graphics);
        data.setBounds(0, 0, width, height);
        data.draw(ctx);

        ImageIO.write(image, "png", new File(outputDir, file));
    }

    private void writePNG(Drawable data, File outputDir, String file) throws IOException {
        String mimeType = "image/png";
        if (file.endsWith(".svg"))
            mimeType = "image/svg+xml";

        DrawableWriter writer = DrawableWriterFactory.getInstance().get(mimeType);
        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream(new File(outputDir, file));
            data.setBounds(10, 10, 790, 590);
            writer.write(data, outStream, 800, 600);
        } finally {
            IOUtils.closeQuietly(outStream);
        }
    }
}
