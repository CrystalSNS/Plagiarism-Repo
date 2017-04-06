package tagger;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class POS_tagger {

	public String builtPOS(String orignalText) throws Exception {
		MaxentTagger tagger = new MaxentTagger("modelPOS/english-left3words-distsim.tagger");
		String textWithPOS = tagger.tagString(orignalText);
		return (textWithPOS);
	}

}
