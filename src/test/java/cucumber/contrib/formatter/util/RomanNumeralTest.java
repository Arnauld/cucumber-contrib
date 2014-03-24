package cucumber.contrib.formatter.util;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class RomanNumeralTest {

    private RomanNumeral romanNumeral = new RomanNumeral();

    @Test(expected = IllegalArgumentException.class)
    public void format_should_not_support_0() {
        romanNumeral.format(0);
    }

    @Test
    public void format_should_render_1_as_i() {
        assertThat(romanNumeral.format(1)).isEqualTo("i");
    }

    @Test
    public void format_should_render_2_as_ii() {
        assertThat(romanNumeral.format(2)).isEqualTo("ii");
    }

    @Test
    public void format_should_render_3_as_iii() {
        assertThat(romanNumeral.format(3)).isEqualTo("iii");
    }

    @Test
    public void format_should_render_4_as_iv() {
        assertThat(romanNumeral.format(4)).isEqualTo("iv");
    }

    @Test
    public void format_should_render_5_as_v() {
        assertThat(romanNumeral.format(5)).isEqualTo("v");
    }

    @Test
    public void format_should_render_6_as_vi() {
        assertThat(romanNumeral.format(6)).isEqualTo("vi");
    }

    @Test
    public void format_should_render_7_as_vii() {
        assertThat(romanNumeral.format(7)).isEqualTo("vii");
    }

    @Test
    public void format_should_render_8_as_viii() {
        assertThat(romanNumeral.format(8)).isEqualTo("viii");
    }

    @Test
    public void format_should_render_9_as_ix() {
        assertThat(romanNumeral.format(9)).isEqualTo("ix");
    }

    @Test
    public void format_should_render_10_as_x() {
        assertThat(romanNumeral.format(10)).isEqualTo("x");
    }

    @Test
    public void format_should_render_11_as_xi() {
        assertThat(romanNumeral.format(11)).isEqualTo("xi");
    }

    @Test
    public void format_should_render_12_as_xii() {
        assertThat(romanNumeral.format(12)).isEqualTo("xii");
    }

    @Test
    public void format_should_render_13_as_xiii() {
        assertThat(romanNumeral.format(13)).isEqualTo("xiii");
    }

    @Test
    public void format_should_render_14_as_xiv() {
        assertThat(romanNumeral.format(14)).isEqualTo("xiv");
    }

    @Test
    public void format_should_render_15_as_xv() {
        assertThat(romanNumeral.format(15)).isEqualTo("xv");
    }

    @Test
    public void format_should_render_19_as_xix() {
        assertThat(romanNumeral.format(19)).isEqualTo("xix");
    }

    @Test
    public void format_should_render_25_as_xix() {
        assertThat(romanNumeral.format(25)).isEqualTo("xxv");
    }
}
