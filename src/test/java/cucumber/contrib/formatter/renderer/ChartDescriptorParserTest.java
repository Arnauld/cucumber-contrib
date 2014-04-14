package cucumber.contrib.formatter.renderer;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class ChartDescriptorParserTest {


    @Test
    public void parse_data() {
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
        assertThat(descriptor.getSeries()).hasSize(1);
        assertThat(descriptor.getSeries().get(0)).isEqualTo(new double[]{1.0, 23.5, 10, 11, 9.4});
        assertThat(descriptor.isLegendVisible()).isTrue();

        assertThat(descriptor).isInstanceOf(ChartPieDescriptor.class);
        ChartPieDescriptor pieDescriptor = (ChartPieDescriptor)descriptor;

        assertThat(pieDescriptor.getRadius()).isEqualTo(0.9);
        assertThat(pieDescriptor.getInnerRadius()).isEqualTo(0.4);
        assertThat(pieDescriptor.getGap()).isEqualTo(0.2);
    }
}
