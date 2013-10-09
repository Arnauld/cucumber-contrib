package cucumber.contrib.formatter.pdf;

import com.itextpdf.text.BaseColor;

import java.util.TreeSet;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class ColorThresholdSelector {
    private final TreeSet<ColorThreshold> thresholds = new TreeSet<ColorThreshold>();

    public ColorThresholdSelector() {
    }

    public ColorThresholdSelector withThreshold(float threshold, BaseColor color) {
        thresholds.add(new ColorThreshold(threshold, color));
        return this;
    }

    public BaseColor colorFor(float value) {
        ColorThreshold lastMatch = null;
        for(ColorThreshold threshold : thresholds) {
            if(threshold.getThreshold() < value) {
                lastMatch = threshold;
                continue;
            }
            break;
        }
        if(lastMatch != null)
            return lastMatch.getColor();
        else
            return null;
    }

    private static class ColorThreshold implements Comparable<ColorThreshold> {
        private final Float threshold;
        private final BaseColor color;

        private ColorThreshold(float threshold, BaseColor color) {
            this.threshold = threshold;
            this.color = color;
        }

        private Float getThreshold() {
            return threshold;
        }

        private BaseColor getColor() {
            return color;
        }

        @Override
        public int compareTo(ColorThreshold other) {
            return threshold.compareTo(other.threshold);
        }
    }
}
