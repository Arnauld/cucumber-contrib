package cucumber.contrib.grammar.step;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="http://twitter.com/aloyer">@aloyer</a>
 */
public class SentenceGroup extends Describable {

    private List<SentenceGroup> subGroups = new ArrayList<SentenceGroup>();
    private List<Sentence> sentences = new ArrayList<Sentence>();

    public void declareGroup(SentenceGroup sentenceGroup) {
        subGroups.add(sentenceGroup);
    }

    public void declareSentence(Sentence sentence) {
        sentences.add(sentence);
    }

    public boolean isEmpty() {
        return subGroups.isEmpty() && sentences.isEmpty();
    }

    public List<SentenceGroup> subGroups() {
        return subGroups;
    }

    public List<Sentence> sentences() {
        return sentences;
    }

    public SentenceGroup subGroup(int index) {
        return subGroups.get(index);
    }
}
