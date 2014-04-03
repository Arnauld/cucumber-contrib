package cucumber.contrib.util;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class Filters {
    public static <T> Filter<T> identity() {
        return new Filter<T>() {
            @Override
            public T filter(T value) {
                return value;
            }
        };
    }
}
