package cucumber.contrib.formatter;

import java.util.Collection;
import java.util.regex.Pattern;

public class BricABrac {
    public static final String NL = "\n";

    public static boolean areEquals(String one, String two) {
        if (one == two)
            return true;
        if (one == null || two == null)
            return false;
        return one.equalsIgnoreCase(two);
    }

    private static Pattern COMMENT = Pattern.compile("^\\s*#");

    public static String discardCommentChar(String value) {
        return COMMENT.matcher(value).replaceAll("");
    }

    public static boolean isEmpty(String location) {
        return location == null || location.isEmpty();
    }

    public static boolean isEmpty(Collection<?> location) {
        return location == null || location.isEmpty();
    }

    public static CharSequence emptyIfNull(String text) {
        if (text == null)
            return "";
        return text;
    }

    public static boolean startsWithComment(String text) {
        return text.startsWith("#");
    }
}
