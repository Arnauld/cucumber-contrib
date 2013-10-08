package cucumber.contrib.formatter;


import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 *
 */
public class BricABracTest {

    @Test
    public void areEquals() {
        assertThat(BricABrac.areEqualsIgnoringCase(null, null)).isTrue();
        assertThat(BricABrac.areEqualsIgnoringCase(null, "")).isFalse();
        assertThat(BricABrac.areEqualsIgnoringCase("", null)).isFalse();
        assertThat(BricABrac.areEqualsIgnoringCase("bob", "bob")).isTrue();
        assertThat(BricABrac.areEqualsIgnoringCase("Bob", "bOb")).isTrue();
    }

    @Test
    public void discardCommentChar() {
        assertThat(BricABrac.discardCommentChar("# bob")).isEqualTo(" bob");
        assertThat(BricABrac.discardCommentChar("  #bob")).isEqualTo("bob");
    }

    @Test
    public void discardCommentChar_double_comment_is_kept() {
        assertThat(BricABrac.discardCommentChar("  ##  bob")).isEqualTo("#  bob");
    }
}
