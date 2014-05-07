package cucumber.contrib.formatter.renderer;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class ChartPieDescriptor extends ChartDescriptor {
    private Double radius;
    private Double innerRadius;
    private Double gap;
    private double[] values;

    @Override
    public ChartType getType() {
        return ChartType.Pie;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Double getRadius() {
        return radius;
    }

    public void setInnerRadius(double innerRadius) {
        this.innerRadius = innerRadius;
    }

    public Double getInnerRadius() {
        return innerRadius;
    }

    public void setGap(double gap) {
        this.gap = gap;
    }

    public Double getGap() {
        return gap;
    }

    public void setValues(double[] values) {
        this.values = values;
    }

    public double[] getValues() {
        return values;
    }
}
