package main;

import java.util.List;

public class Main {

	public static void main(String[] args) {

		Text text = new Text();
		Features feat = new Features();
		List<Document> documentsList = text.splitToParagraphes(text.readTextFile("corpus/train/12Esample01.txt"));
		for (int i = 0; i < 1; i++) {
			List<Sentence> sentenceList = text.splitToSentences(documentsList.get(i).getOriginalDoc());

			for (int j = 0; j < sentenceList.size(); j++) {
				text = new Text();
				sentenceList.get(j).setLengthByWords(text.splitToWords(sentenceList.get(j).getOriginalSentence()).length);
				sentenceList.get(j).setNum_POS(feat.findPOSFrequency(sentenceList.get(j)));
				Sentence sentObj = feat.findPunctuationFrequency(sentenceList.get(j));
				sentenceList.get(j).setNum_punctuation(sentObj.getNum_punctuation());
				sentenceList.get(j).setLengthByChar(sentObj.getLengthByChar());
				
			}
			documentsList.get(i).setSentencesInDoc(sentenceList);
		}
	}
}
