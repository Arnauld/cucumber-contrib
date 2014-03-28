package cucumber.contrib.formatter.pegdown;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.support.StringBuilderVar;
import org.pegdown.Parser;
import org.pegdown.plugins.BlockPluginParser;

/**
 *
 * <pre>
 *     {% blockname options... %}
 *       block content
 *     {% blockname %}
 * </pre>
 *
 *
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class NamedBlockCurlyBracePlugin extends Parser implements BlockPluginParser {

    public NamedBlockCurlyBracePlugin() {
        super(ALL, 1000l, DefaultParseRunnerProvider);
    }

    @Override
    public Rule[] blockPluginRules() {
        return new Rule[]{asciiFenceRule()};
    }

    public Rule asciiFenceRule() {
        StringBuilderVar text = new StringBuilderVar();
        return NodeSequence(
                BlockBeginMarker(),
                OneOrMore(TestNot(Newline(), BlockEndMarker()), BaseParser.ANY, text.append(matchedChar())),
                Newline(),
                ((NamedBlockNode) peek()).appendBody(text.getString()),
                BlockEndMarker()
        );
    }

    public Rule BlockBeginMarker() {
        StringBuilderVar text = new StringBuilderVar();
        return NodeSequence(
                Sp(),
                "{%",
                OneOrMore(TestNot("%}"), BaseParser.ANY, text.append(matchedChar())),
                Sp(),
                "%}",
                push(new NamedBlockNode(text.getString())),
                Newline());
    }


    public Rule BlockEndMarker() {
        StringBuilderVar text = new StringBuilderVar();
        //((NamedBlockNode)peek()).getHeaderTag(),
        return Sequence(
                Sp(),
                "{%",
                OneOrMore(TestNot("%}"), BaseParser.ANY, text.append(matchedChar())),
                "%}",
                Sp(),
                ((NamedBlockNode) peek()).endTag(text.getString()),
                Newline());
    }

}
