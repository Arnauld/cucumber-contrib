package cucumber.contrib.formatter.renderer;

import de.erichseifert.gral.data.DataSource;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.graphics.DrawingContext;
import de.erichseifert.gral.plots.PiePlot;
import de.erichseifert.gral.plots.Plot;
import de.erichseifert.gral.plots.colors.LinearGradient;
import de.erichseifert.gral.util.Insets2D;

import java.awt.*;
import java.io.IOException;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class GralRenderer {

    /**
     * First corporate color used for normal coloring.
     */
    protected static final Color COLOR1 = new Color(55, 170, 200);
    /**
     * Second corporate color used as signal color
     */
    protected static final Color COLOR2 = new Color(200, 80, 75);

    public void render(Graphics2D out,
                       ChartDescriptor descriptor) throws IOException {

        DataSource data = createDataSource(descriptor);
        Plot plot = createPlot(descriptor, data);

        DrawingContext ctx = new DrawingContext(out);
        plot.setBounds(0, 0, descriptor.getWidth(), descriptor.getHeight());
        plot.draw(ctx);
    }

    private void applyDefaultDescriptor(ChartDescriptor descriptor, Plot plot) {
        String title = descriptor.getTitle();
        if (title != null)
            plot.getTitle().setText(title);

        if (descriptor.isLegendVisible())
            plot.setLegendVisible(true);

        // Add some margin to the plot area
        Insets2D.Double insets = descriptor.getInsets();
        if (insets != null)
            plot.setInsets(insets);
    }

    private Plot createPlot(ChartDescriptor descriptor, DataSource data) {
        switch (descriptor.getType()) {
            case Pie:
                PiePlot piePlot = new PiePlot(data);
                apply((ChartPieDescriptor) descriptor, piePlot, data);
                return piePlot;
        }
        throw new UnsupportedOperationException("Chart type not supported '" + descriptor.getType() + "'");
    }

    private void apply(ChartPieDescriptor descriptor, PiePlot plot, DataSource data) {
        applyDefaultDescriptor(descriptor, plot);

        // Change relative size of pie
        if (descriptor.getRadius() != null)
            plot.setRadius(descriptor.getRadius());

        PiePlot.PieSliceRenderer pointRenderer =
                (PiePlot.PieSliceRenderer) plot.getPointRenderer(data);

        // Change relative size of inner region
        if (descriptor.getInnerRadius() != null)
            pointRenderer.setInnerRadius(descriptor.getInnerRadius());

        // Change the width of gaps between segments
        if (descriptor.getGap() != null)
            pointRenderer.setGap(descriptor.getGap());

        // Change the colors
        LinearGradient colors = new LinearGradient(COLOR1, COLOR2);
        pointRenderer.setColor(colors);

        // Show labels
        pointRenderer.setValueVisible(true);
        pointRenderer.setValueColor(Color.WHITE);
        pointRenderer.setValueFont(Font.decode(null).deriveFont(Font.BOLD));
    }

    @SuppressWarnings("unchecked")
    private DataSource createDataSource(ChartDescriptor descriptor) {
        switch (descriptor.getType()) {
            case Pie: {
                ChartPieDescriptor pie = (ChartPieDescriptor) descriptor;
                DataTable data = new DataTable(Double.class);
                for (double d : pie.getValues()) {
                    data.add(d);
                }
                return data;
            }
            case XY: {
                ChartXYDescriptor xy = (ChartXYDescriptor) descriptor;
                DataTable data = new DataTable(xy.getSeriesCount() + 1, Double.class);
            }

        }
        throw new UnsupportedOperationException("Chart type not supported '" + descriptor.getType() + "'");
    }
}
