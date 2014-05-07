package cucumber.contrib.formatter.renderer;

/**
* @author <a href="http://twitter.com/aloyer">@aloyer</a>
*/
public enum ChartType {
    Pie,
    XY,
    Histogram;

    public static ChartType fromString(String value) {
        for (ChartType type : values())
            if (type.name().equalsIgnoreCase(value))
                return type;
        throw new IllegalArgumentException("Unsupported type '" + value + "'");
    }
}
