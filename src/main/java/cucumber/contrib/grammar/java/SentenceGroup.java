package cucumber.contrib.grammar.java;

import com.google.common.collect.Lists;

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

    public List<Sentence> matchingSentences(String text) {
        List<Sentence> matches = Lists.newArrayList();
        for (SentenceGroup subGroup : subGroups) {
            matches.addAll(subGroup.matchingSentences(text));
        }
        for (Sentence sentence : sentences) {
            if (sentence.matches(text))
                matches.add(sentence);
        }
        return matches;
    }
}
