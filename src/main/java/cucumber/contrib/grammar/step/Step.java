package cucumber.contrib.grammar.step;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class Step {
    private final String keyword;
    private final String name;

    public Step(String keyword, String name) {
        this.keyword = keyword;
        this.name = name;
    }

    public void traverse(FeatureVisitor visitor) {
        visitor.visitStep(this);
    }
}
