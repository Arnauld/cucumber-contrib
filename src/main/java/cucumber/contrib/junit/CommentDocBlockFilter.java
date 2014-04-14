package cucumber.contrib.junit;

import cucumber.contrib.util.Filter;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class CommentDocBlockFilter implements Filter<InputStream> {

    private static final String NL = System.getProperty("line.separator");
    private static final String COMMENT = "#";

    private String charsetName = "UTF8";

    public CommentDocBlockFilter() {
    }

    public CommentDocBlockFilter useCharsetName(String charsetName) {
        this.charsetName = charsetName;
        return this;
    }

    @Override
    public InputStream filter(InputStream inputStream) {
        try {
            String raw = IOUtils.toString(inputStream, charsetName);

            StringBuilder filtered = new StringBuilder();
            LineProcessor strategy = new DoubleDashStrategy(filtered);
            String[] split = raw.split("[\r\n]", -1);
            for (int i = 0, n = split.length; i < n; i++) {
                String line = split[i];
                strategy = strategy.process(line, i == (n - 1));
            }

            return new ByteArrayInputStream(filtered.toString().getBytes(charsetName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public interface LineProcessor {
        LineProcessor process(String line, boolean isLast);
    }

    public class DoubleDashStrategy implements LineProcessor {
        private final StringBuilder filtered;
        private boolean inComment = false;

        protected DoubleDashStrategy(StringBuilder filtered) {
            this.filtered = filtered;
        }

        @Override
        public LineProcessor process(String line, boolean isLast) {
            String trimmed = line.trim();

            if (trimmed.equals("--")) {
                inComment = !inComment;
                filtered.append(COMMENT);
            }
            else if(inComment) {
                filtered.append(COMMENT).append(line);
            }
            else {
                filtered.append(line);
            }

            if(!isLast) {
                filtered.append(NL);
            }
            return this;
        }
    }
}
