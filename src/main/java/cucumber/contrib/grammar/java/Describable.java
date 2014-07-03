package cucumber.contrib.grammar.java;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class Describable {
    private String comment;
    private Source source;

    public void describeWith(String comment) {
        this.comment = comment;
    }

    public void defineSource(Source source) {
        this.source = source;
    }

    public Source source() {
        return source;
    }

    public String comment() {
        return comment;
    }
}
