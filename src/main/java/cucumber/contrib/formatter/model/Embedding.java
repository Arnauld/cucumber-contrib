package cucumber.contrib.formatter.model;

import java.nio.charset.Charset;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class Embedding {
    private static final Charset UTF8 = Charset.forName("utf8");
    private final String mimeType;
    private final byte[] data;

    public Embedding(String mimeType, byte[] data) {
        this.mimeType = mimeType;
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getDataAsUTF8() {
        return new String(data, UTF8);
    }
}