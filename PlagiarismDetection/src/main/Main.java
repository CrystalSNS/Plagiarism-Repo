package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) throws IOException {

		Text text = new Text();
		Features feat = new Features();
		List<Document> documentsList = text.splitToParagraphes(text.readTextFile("corpus/train/12Esample01.txt"));

		for (int i = 0; i < documentsList.size(); i++) {

			List<Sentence> sentenceList = text.splitToSentences(documentsList.get(i).getOriginalDoc());
			List<Sentence> sentenceListNew = new ArrayList<>();

			for (int j = 0; j < sentenceList.size(); j++) {

				text = new Text();
				Sentence sentObj = new Sentence();
				sentObj.setOriginalSentence(sentenceList.get(j).getOriginalSentence());
				sentObj = feat.findPOSFrequency(sentObj);
				sentObj = feat.findPunctuationFrequency(sentObj);
				sentObj = feat.findRelationalFrequency(documentsList.get(i).getOriginalDoc(), sentObj);

				sentenceListNew.add(sentObj);
				
				text.writFeatureToFile(sentObj, (j == sentenceList.size()-1), "/Users/noch/Desktop/test/test.csv");
			}
			documentsList.get(i).setSentencesInDoc(sentenceListNew);
		}
	}
}
