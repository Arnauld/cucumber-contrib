package cucumber.contrib.formatter.renderer;

import de.erichseifert.gral.util.Insets2D;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public abstract class ChartDescriptor {
    private List<double[]> series = new ArrayList<double[]>();
    private boolean legendVisible;
    private String title;
    private Insets2D.Double insets;
    private double width = 400;
    private double height = 300;

    public abstract ChartType getType();

    public List<double[]> getSeries() {
        return series;
    }

    public void addSerie(double... values) {
        this.series.add(values);
    }

    public void setLegendVisible(boolean visible) {
        this.legendVisible = visible;
    }

    public boolean isLegendVisible() {
        return legendVisible;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Insets2D.Double getInsets() {
//        new Insets2D.Double(20.0, 40.0, 40.0, 40.0)
        return insets;
    }

    public void setInsets(Insets2D.Double insets) {
        this.insets = insets;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
