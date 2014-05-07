package cucumber.contrib.formatter.renderer;

import org.junit.Test;

import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class ChartDescriptorParserTest {


    @Test
    public void parse_pie_chart() {
        String input = "" +
                "type: pie\n" +
                "data: [1.0, 23.5, 10, 11, 9.4]\n" +
                "radius: 0.9\n" +
                "inner-radius: 0.4\n" +
                "gap: 0.2\n" +
                "legend: true\n" +
                "";
        ChartDescriptorParser parser = new ChartDescriptorParser();
        ChartDescriptor descriptor = parser.parse(input);
        assertThat(descriptor).isNotNull();
        assertThat(descriptor.getType()).isEqualTo(ChartType.Pie);
        assertThat(descriptor.isLegendVisible()).isTrue();

        assertThat(descriptor).isInstanceOf(ChartPieDescriptor.class);
        ChartPieDescriptor pieDescriptor = (ChartPieDescriptor) descriptor;

        assertThat(pieDescriptor.getValues()).isEqualTo(new double[]{1.0, 23.5, 10, 11, 9.4});
        assertThat(pieDescriptor.getRadius()).isEqualTo(0.9);
        assertThat(pieDescriptor.getInnerRadius()).isEqualTo(0.4);
        assertThat(pieDescriptor.getGap()).isEqualTo(0.2);
    }

    @Test
    public void parse_xy_chart() {
        String input = "" +
                "type: xy\n" +
                "x: [1.0, 2.0, 3.0, 6.4, 8]\n" +
                "y: [1.0, 23.5, 10, 11, 9.4]\n" +
                " - color: rgb(55, 170, 200)\n" +
                " - point: circle, radius: 3.0\n" +
                " - line: line, width: 2.0\n" +
                " - area: fill\n" +
                "y: [3.0, 5.0, 7.0, 9.0, 11.0]\n" +
                " - color: rgb(200, 80, 75)\n" +
                " - point: square\n" +
                " - line: none\n" +
                " - area: line, gap: 0.3\n" +
                "legend: true\n" +
                "";
        ChartDescriptorParser parser = new ChartDescriptorParser();
        ChartDescriptor descriptor = parser.parse(input);
        assertThat(descriptor).isNotNull();
        assertThat(descriptor.getType()).isEqualTo(ChartType.XY);

        assertThat(descriptor).isInstanceOf(ChartXYDescriptor.class);
        ChartXYDescriptor xyDescriptor = (ChartXYDescriptor) descriptor;

        assertThat(xyDescriptor.getXs()).isEqualTo(new double[]{1.0, 2.0, 3.0, 6.4, 8});
        List<double[]> xyFlatten = xyDescriptor.xyFlatten();
        assertThat(xyFlatten).hasSize(5);
        assertThat(xyFlatten.get(0)).isEqualTo(d(1.0, 1.0, 3.0));
        assertThat(xyFlatten.get(1)).isEqualTo(d(2.0, 23.5, 5.0));
        assertThat(xyFlatten.get(2)).isEqualTo(d(3.0, 10.0, 7.0));
        assertThat(xyFlatten.get(3)).isEqualTo(d(6.4, 11.0, 9.0));
        assertThat(xyFlatten.get(4)).isEqualTo(d(8.0, 9.4, 11.0));

    }

    private static double[] d(double... values) {
        return values;
    }
}
