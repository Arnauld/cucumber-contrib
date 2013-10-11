package cucumber.contrib.formatter;

/**
 */
public class FormatterException extends RuntimeException {
    public FormatterException(String message) {
        super(message);
    }
    public FormatterException(String message, Throwable cause) {
        super(message, cause);
    }
}
