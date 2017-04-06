package main;

import java.util.List;

public class Main {

	public static void main(String[] args) {

		Text text = new Text();
		List<Document> documentsList = text.splitToParagraphes(text.readTextFile("corpus/train/12Esample01.txt"));
		for (int i = 0; i < documentsList.size(); i++) {
				List<Sentence> sentenceList = text.splitToSentences(documentsList.get(i).getOriginalDoc());
				for (int j = 0; j < sentenceList.size(); j++){
					sentenceList.get(j).setLengthByWords(text.findTextLengthByWord(sentenceList.get(j).getOriginalSentence()));
				}
			documentsList.get(i).setSentencesInDoc(sentenceList);

			// System.out.println(sentence);
		}
	}

}
