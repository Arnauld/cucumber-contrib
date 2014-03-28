package cucumber.contrib.formatter.pegdown;

import org.parboiled.common.ImmutableList;
import org.parboiled.common.StringUtils;
import org.pegdown.ast.AbstractNode;
import org.pegdown.ast.Node;
import org.pegdown.ast.Visitor;

import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class NamedBlockNode extends AbstractNode {

    private final String header;
    private final StringBuilder body;


    public NamedBlockNode(String text) {
        this.header = text;
        this.body = new StringBuilder();
    }

    public String getHeader() {
        return header;
    }

    public String beginTag() {
        String trimmed = getHeader().trim();
        int spaceIdx = trimmed.indexOf(" ");
        if (spaceIdx > 0)
            return trimmed.substring(0, spaceIdx);
        else
            return trimmed;
    }

    /**
     * Indicates if the end tag provided matches the begin tag.
     *
     * @see #beginTag()
     */
    public boolean endTag(String endTag) {
        return endTag.trim().equals(beginTag());
    }


    public String getBody() {
        return body.toString();
    }

    public boolean appendBody(String text) {
        body.append(text);
        return true;
    }

    @Override
    public List<Node> getChildren() {
        return ImmutableList.of();
    }

    @Override
    public String toString() {
        return super.toString() + " '" + StringUtils.escape(getHeader()) + '\'';
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
