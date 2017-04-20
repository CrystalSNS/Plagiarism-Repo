package main;

import java.io.IOException;
import java.util.List;

public class Main {

	public static void main(String[] args) throws IOException {

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
				sentObj = feat.findRelationalFrequency(documentsList.get(i).getOriginalDoc(), sentenceList.get(j).getOriginalSentence());
				sentenceList.get(j).setWord_5(sentObj.getWord_5());
				sentenceList.get(j).setWord_95(sentObj.getWord_95());
				sentenceList.get(j).setWord_Mean(sentObj.getWord_Mean());
				sentenceList.get(j).setChar1_5(sentObj.getChar1_5());
				sentenceList.get(j).setChar1_95(sentObj.char1_95);
				sentenceList.get(j).setChar1_Mean(sentObj.char1_Mean);
				sentenceList.get(j).setChar3_5(sentObj.char3_5);
				sentenceList.get(j).setChar3_95(sentObj.getChar3_95());
				sentenceList.get(j).setChar3_Mean(sentObj.getChar3_Mean());
				sentenceList.get(j).setChar4_5(sentObj.getChar4_5());
				sentenceList.get(j).setChar4_95(sentObj.getChar4_95());
				sentenceList.get(j).setChar4_Mean(sentObj.getChar4_Mean());
				
				text.splitToChar(documentsList.get(i).getOriginalDoc());

			}
			documentsList.get(i).setSentencesInDoc(sentenceList);
		}

	}
}
