package cucumber.contrib.junit;

import cucumber.contrib.formatter.util.BricABrac;
import cucumber.contrib.util.Filter;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class DocumentThroughStoreFilter implements Filter<InputStream> {

    private static final String NL = System.getProperty("line.separator");

    private final DocumentStore documentStore;
    private String charsetName = "UTF8";
    private boolean maintainLineCount = true;

    public DocumentThroughStoreFilter(DocumentStore documentStore) {
        this.documentStore = documentStore;
    }

    public DocumentThroughStoreFilter useCharsetName(String charsetName) {
        this.charsetName = charsetName;
        return this;
    }

    public DocumentThroughStoreFilter maintainLineCount(boolean maintainLineCount) {
        this.maintainLineCount = maintainLineCount;
        return this;
    }

    @Override
    public InputStream filter(InputStream inputStream) {
        try {
            String raw = IOUtils.toString(inputStream, charsetName);

            StringBuilder filtered = new StringBuilder();
            LineProcessor strategy = new DoubleDashStrategy(filtered); //new RawStrategy(filtered);
            for (String line : raw.split("[\r\n]")) {
                strategy = strategy.process(line);
            }

            return new ByteArrayInputStream(filtered.toString().getBytes(charsetName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public interface LineProcessor {
        LineProcessor process(String line);
    }

    public class DoubleDashStrategy implements LineProcessor {
        private final StringBuilder filtered;
        private final StringBuilder comment;
        private String commentRef = null;
        private boolean inComment = false;

        protected DoubleDashStrategy(StringBuilder filtered) {
            this.filtered = filtered;
            this.comment = new StringBuilder();
        }

        @Override
        public LineProcessor process(String line) {
            String trimmed = line.trim();
            if (trimmed.equals("--")) {
                if (inComment) {
                    flushComment();
                    inComment = false;
                } else {
                    inComment = true;
                }
                filtered.append(line).append(NL);
            }
            else if(inComment) {
                if(commentRef==null) {
                    commentRef = documentStore.generateRefId();
                    filtered.append(commentRef).append(NL);
                }
                else if(maintainLineCount) {
                    filtered.append(NL);
                }
                comment.append(line);
            }
            else {
                filtered.append(line).append(NL);
            }
            return this;
        }

        private void flushComment() {
            if(commentRef != null) {
                documentStore.put(commentRef, comment.toString());
            }
            commentRef = null;
            comment.setLength(0);
        }
    }

    public class RawStrategy implements LineProcessor {
        private final StringBuilder filtered;
        private final StringBuilder comment;
        private String commentRef = null;
        private boolean inComment = false;

        protected RawStrategy(StringBuilder filtered) {
            this.filtered = filtered;
            this.comment = new StringBuilder();
        }

        @Override
        public LineProcessor process(String line) {
            String trimmed = line.trim();
            if (trimmed.startsWith("@")) {
                filtered.append(line).append(NL);
                return this;
            }

            if (inComment) {
                if (isCommentStop(trimmed)) {
                    if (commentRef != null) {
                        documentStore.put(commentRef, comment.toString());
                        commentRef = null;
                        comment.setLength(0);
                    }
                    inComment = false;
                    filtered.append(line).append(NL);
                } else {
                    if (commentRef == null) {
                        commentRef = documentStore.generateRefId();
                        filtered.append(commentRef).append(NL);
                    } else if (maintainLineCount) {
                        filtered.append(NL);
                    }
                    comment.append(line).append(NL);
                }
            } else {
                filtered.append(line).append(NL);
                if (isCommentTrigger(trimmed)) {
                    inComment = true;
                }
            }
            return this;
        }

        private boolean isCommentTrigger(String trimmed) {
            return BricABrac.startsWithOneOf(trimmed,
                    "Scenario: ", "Scenario Outline: ", //
                    "Feature: ");
        }

        private boolean isCommentStop(String trimmed) {
            return BricABrac.startsWithOneOf(trimmed,
                    "Given ", "And ", "But ", "When ", "Then ", //
                    "Scenario: ", "Scenario Outline: ", //
                    "Feature: ");
        }
    }


}
