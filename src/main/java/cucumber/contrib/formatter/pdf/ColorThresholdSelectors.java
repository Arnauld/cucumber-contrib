package cucumber.contrib.formatter.pdf;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class ColorThresholdSelectors {
    public static ColorThresholdSelector redOrangeGreenPercent() {
        return new ColorThresholdSelector()
                .withThreshold(0.0f, Colors.DARK_RED)
                .withThreshold(0.5f, Colors.ORANGE)
                .withThreshold(0.9f, Colors.GREEN_FLASHY);
    }

    public static ColorThresholdSelector yellowOrangeRedPercent() {
        return new ColorThresholdSelector()
                .withThreshold(0.0f, Colors.YELLOW)
                .withThreshold(0.5f, Colors.ORANGE)
                .withThreshold(0.9f, Colors.DARK_RED);
    }

}
