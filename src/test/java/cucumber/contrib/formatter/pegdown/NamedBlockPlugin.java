package cucumber.contrib.formatter.pegdown;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.support.StringBuilderVar;
import org.pegdown.Parser;
import org.pegdown.plugins.BlockPluginParser;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class NamedBlockPlugin extends Parser implements BlockPluginParser {

    public NamedBlockPlugin() {
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
                ((NamedBlockPluginNode)peek()).appendBody(text.getString()),
                BlockEndMarker()
        );
    }

    public Rule BlockBeginMarker() {
        StringBuilderVar text = new StringBuilderVar();
        return NodeSequence(
                "{%",
                OneOrMore(TestNot("%}"), BaseParser.ANY, text.append(matchedChar())),
                "%}",
                push(new NamedBlockPluginNode(text.getString())),
                Newline());
    }


    public Rule BlockEndMarker() {
        StringBuilderVar text = new StringBuilderVar();
        //((NamedBlockPluginNode)peek()).getHeaderTag(),
        return Sequence(
                        "{%",
                        OneOrMore(TestNot("%}"), BaseParser.ANY, text.append(matchedChar())),
                        "%}",
                        ((NamedBlockPluginNode)peek()).endTag(text.getString()),
                        Newline());
    }

}
