package cucumber.contrib.grammar.step;

import com.google.common.collect.Lists;
import cucumber.contrib.grammar.GrammarConsolidation;

import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class Features {
    private final List<Feature> featureList = Lists.newArrayList();

    public Features() {
    }

    public void add(Feature feature) {
        featureList.add(feature);
    }

    public void traverse(FeatureVisitor visitor) {
        for(Feature feature : featureList) {
            feature.traverse(visitor);
        }
    }
}
