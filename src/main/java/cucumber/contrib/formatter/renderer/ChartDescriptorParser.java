package cucumber.contrib.formatter.renderer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class ChartDescriptorParser {

    private static final Pattern TYPE = singleLine("type", "([a-zA-Z]+)");
    private static final Pattern LEGEND = singleLine("legend", "(true|false)");

    public ChartDescriptor parse(String input) {
        ChartDescriptor descriptor = handleType(input);
        handleLegend(input, descriptor);
        return descriptor;
    }

    private void handleLegend(String input, ChartDescriptor descriptor) {
        Boolean bool = bool(input, LEGEND);
        if (bool != null)
            descriptor.setLegendVisible(bool);
    }

    private ChartDescriptor handleType(String input) {
        String value = first(input, TYPE);
        if (value == null) {
            throw new IllegalArgumentException("No type defined");
        }

        ChartType chartType = ChartType.fromString(value);
        switch (chartType) {
            case Pie: {
                ChartPieDescriptor descriptor = new ChartPieDescriptor();
                handlePieSpecifics(input, descriptor);
                return descriptor;
            }
            case XY: {
                ChartXYDescriptor descriptor = new ChartXYDescriptor();
                handleXYSpecifics(input, descriptor);
                return descriptor;
            }
            default:
                throw new UnsupportedOperationException("Chart type " + chartType + " not yet supported");
        }
    }


    private void handlePieSpecifics(String input, ChartPieDescriptor descriptor) {
        double[] values = commaSeparatedDoubles(input, singleLine("data", "\\[(.*)\\]"));
        if (values != null)
            descriptor.setValues(values);

        String radius = first(input, singleLine("radius", "(.+)"));
        if (radius != null)
            descriptor.setRadius(Double.parseDouble(radius));

        String innerRadius = first(input, singleLine("inner-radius", "(.+)"));
        if (innerRadius != null)
            descriptor.setInnerRadius(Double.parseDouble(innerRadius));

        String gap = first(input, singleLine("gap", "(.+)"));
        if (gap != null)
            descriptor.setGap(Double.parseDouble(gap));
    }

    private void handleXYSpecifics(String input, ChartXYDescriptor descriptor) {
        double[] xs = commaSeparatedDoubles(input, singleLine("x", "\\[(.*)\\]"));
        if (xs != null)
            descriptor.setXs(xs);

        List<String> blocks = allMultiline(input, "(y:[^\n]*(?:\n\\s*\\-[^\n]*)*)");
        for(String block : blocks) {
            double[] ys = commaSeparatedDoubles(block, singleLine("y", "\\[(.*)\\]"));

            descriptor.addYs(ys);
        }

    }

    private static double[] commaSeparatedDoubles(String input, Pattern pattern) {
        String raw = first(input, pattern);
        if (raw == null)
            return null;

        String[] valuesAsString = raw.split(",");
        double[] values = new double[valuesAsString.length];
        int pos = 0;
        for (String valueAsString : valuesAsString) {
            values[pos++] = Double.parseDouble(valueAsString);
        }
        return trimIfRequired(values, pos);
    }

    private static double[] trimIfRequired(double[] values, int length) {
        if (values.length > length) {
            double[] n = new double[length];
            System.arraycopy(values, 0, n, 0, length);
            return n;
        }
        return values;
    }

    private static Boolean bool(String input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return Boolean.parseBoolean(matcher.group(1));
        }
        return null;
    }

    private static String first(String input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private List<String> allMultiline(String input, String pattern) {
        Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE|Pattern.MULTILINE).matcher(input);
        List<String> found = new ArrayList<String>();
        while(matcher.find()) {
            found.add(matcher.group(1));
        }
        return found;
    }


    private static Pattern singleLine(String key, String valuePattern) {
        return Pattern.compile("^\\s*\\Q" + key + "\\E\\s*:\\s*" + valuePattern + "\\s*$",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    }

}
