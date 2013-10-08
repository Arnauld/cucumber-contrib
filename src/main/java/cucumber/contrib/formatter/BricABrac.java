package cucumber.contrib.formatter;

import com.google.common.collect.Iterables;

import java.util.Collection;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class BricABrac {
    public static final String NL = "\n";
    private static final String COMMENT = "#";
    private static final Pattern COMMENT_PATTERN = compile("^\\s*" + COMMENT);

    public static boolean areEqualsIgnoringCase(String one, String two) {
        if (one == two) {
            return true;
        }
        if (one == null || two == null) {
            return false;
        }
        return one.equalsIgnoreCase(two);
    }


    public static String discardCommentChar(String value) {
        return COMMENT_PATTERN.matcher(value).replaceAll("");
    }

    public static boolean isEmpty(Collection<?> location) {
        return location != null && Iterables.isEmpty(location);
    }

    public static boolean startsWithComment(String text) {
        return text.startsWith(COMMENT);
    }
}
