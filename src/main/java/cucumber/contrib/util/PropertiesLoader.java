package cucumber.contrib.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class PropertiesLoader {
    public Properties load(URL resource) throws IOException {

        InputStream inStream = null;
        try {
            Properties properties = new Properties();
            inStream = resource.openStream();
            properties.load(inStream);
            return properties;
        } finally {
            IOUtils.closeQuietly(inStream);
        }
    }
}
