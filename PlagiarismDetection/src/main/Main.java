package main;

import java.util.List;
import java.util.Map;

public class Main {

	public static void main(String[] args) {

		Text text = new Text();
		Features fet = new Features();
		List<Document> documentsList = text.splitToParagraphes(text.readTextFile("corpus/train/12Esample01.txt"));
		for (int i = 0; i < 1; i++) {
			List<Sentence> sentenceList = text.splitToSentences(documentsList.get(i).getOriginalDoc());
			for (int j = 0; j < sentenceList.size(); j++) {
				text = new Text();
				int sentLength = text.splitToWords(sentenceList.get(j).getOriginalSentence()).length;
				sentenceList.get(j).setLengthByWords(sentLength);
//				sentenceList.get(j).setNum_POS(fet.normalizePOS(text.findPOSFrequency(sentenceList.get(j).getOriginalSentence()), sentLength));
				Map<String, Integer> test ;
				
				test=text.findPOSFrequency(sentenceList.get(j).getOriginalSentence());
				sentenceList.get(j).setLengthByWords(sentLength);
			}
			documentsList.get(i).setSentencesInDoc(sentenceList);
		}
	}
}
