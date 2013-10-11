package cucumber.contrib.formatter.model;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class CompositeStatistics {
    public static Statistics compose(final Statistics... instances) {
        return compose(Arrays.asList(instances));
    }

    public static Statistics compose(final Iterable<Statistics> instances) {
        return (Statistics)Proxy.newProxyInstance(CompositeStatistics.class.getClassLoader(), new Class[] {Statistics.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                for(Statistics instance : instances)
                    method.invoke(instance, objects);

                // TODO check for return type and throw exception if it is not a void function
                return null;
            }
        });
    }
}
