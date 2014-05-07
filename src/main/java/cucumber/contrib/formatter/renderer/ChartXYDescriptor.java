package cucumber.contrib.formatter.renderer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class ChartXYDescriptor extends ChartDescriptor {
    private double[] xs;
    private List<double[]> yss = new ArrayList<double[]>();

    @Override
    public ChartType getType() {
        return ChartType.XY;
    }

    public double[] getXs() {
        return xs;
    }

    public void setXs(double... xs) {
        this.xs = xs;
    }

    public int getSeriesCount() {
        return yss.size();
    }

    public List<double[]> xyFlatten() {
        int nbCol = getSeriesCount() + 1;
        List<double[]> values = new ArrayList<double[]>(xs.length);
        for (int i = 0; i < xs.length; i++) {
            int index = 0;
            double[] row = new double[nbCol];

            row[index++] = xs[i];
            for (int j = 1; j < nbCol; j++) {
                row[index++] = yss.get(j - 1)[i];
            }
            values.add(row);
        }
        return values;
    }

    public void addYs(double[] ys) {
        yss.add(ys);
    }
}
