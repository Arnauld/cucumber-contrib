package cucumber.contrib.formatter.util;

import static java.util.regex.Pattern.compile;

import com.google.common.collect.Iterables;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class BricABrac {
    public static final String NL = "\n";
    private static final String COMMENT = "#";
    private static final Pattern COMMENT_PATTERN = compile("^\\s*" + COMMENT);
    private static final Pattern BLANK_PATTERN = compile("^[\\s\u00A0]*$");

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

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        }
        catch (IOException e) {
            // ignore
        }
    }

    public static String join(List<String> values, String separator) {
        StringBuilder b = new StringBuilder();
        for (String value : values) {
            if (value != null && !value.isEmpty()) {
                if (b.length() > 0) {
                    b.append(separator);
                }
                b.append(value);
            }
        }
        return b.toString();
    }

    public static boolean isBlank(String content) {
        return content == null || BLANK_PATTERN.matcher(content).matches();
    }

    public static boolean isNotBlank(String content) {
        return !isBlank(content);
    }


    public static String md5Hex(String content) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            byte[] digest = md5.digest(content.getBytes());
            return toHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

    public static boolean isOneOfIgnoringCase(String value, String ... alternatives) {
        for(String alt : alternatives) {
            if(alt.equalsIgnoreCase(value))
                return true;
        }
        return false;
    }
}
