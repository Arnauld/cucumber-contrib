package cucumber.contrib.formatter;


import cucumber.contrib.formatter.util.BricABrac;
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

    @Test
    public void isBlank() {
        assertThat(BricABrac.isBlank(null)).isTrue();
        assertThat(BricABrac.isBlank("  ")).isTrue();
        assertThat(BricABrac.isBlank("\t")).isTrue();
        assertThat(BricABrac.isBlank("\n\t\r")).isTrue();
        assertThat(BricABrac.isBlank("\na\t\r")).isFalse();
    }

    @Test
    public void isBlank__nbsp() {
        assertThat(BricABrac.isBlank(" \u00A0")).isTrue();

    }
}
