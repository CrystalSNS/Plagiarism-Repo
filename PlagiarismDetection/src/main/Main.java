package main;

import java.util.List;

public class Main {

	public static void main(String[] args) {

		Text text = new Text();
		List<Document> documentsList = text.splitToParagraphes(text.readTextFile("corpus/train/12Esample01.txt"));
		for (int i = 0; i < 1; i++) {
			List<Sentence> sentenceList = text.splitToSentences(documentsList.get(i).getOriginalDoc());

			for (int j = 0; j < sentenceList.size(); j++) {
				text = new Text();
				sentenceList.get(j).setLengthByWords(text.splitToWords(sentenceList.get(j).getOriginalSentence()).length);
				sentenceList.get(j).setNum_POS(text.findPOSFrequency(sentenceList.get(j)));
				
				System.out.println("test");
			}
			documentsList.get(i).setSentencesInDoc(sentenceList);
		}
	}
}
