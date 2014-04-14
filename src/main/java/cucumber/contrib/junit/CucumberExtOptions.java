package cucumber.contrib.junit;

import cucumber.contrib.util.Filter;

import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface CucumberExtOptions {
    Class<? extends Filter<InputStream>>[] filters() default {};
}
