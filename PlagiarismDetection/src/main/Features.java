package main;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import tagger.POS_tagger;

public class Features {

	private static final List<Character> punctuationList = Arrays.asList('!', ',', '.', '?', '-', ';');
	private static final Map<String, List<String>> posTags = new HashMap<>();

	static {
		posTags.put("VERB", Arrays.asList("VB", "VBD", "VBG", "VBN", "VBP", "VBZ"));
		posTags.put("NOUN", Arrays.asList("NN", "NNP", "NNPS", "NNS"));
		posTags.put("PRON", Arrays.asList("PRP", "PRP$"));
		posTags.put("ADJ", Arrays.asList("JJ", "JJR", "JJS"));
		posTags.put("ADV", Arrays.asList("RB", "RBR", "RBS"));
		posTags.put("ADP", Arrays.asList("IN", "TO"));
		posTags.put("CONJ", Arrays.asList("CC"));
		posTags.put("DET", Arrays.asList("DT", "PDT", "WDT"));
		posTags.put("NUM", Arrays.asList("CD"));
		posTags.put("PRT", Arrays.asList("RP"));
	}

	Text text = new Text();

	public Map<String, Integer> findWordFrequency(String wordArr[]) {

		Map<String, Integer> frequencyWord = new HashMap<>();

		int i = 0;
		while (i < wordArr.length) {
			if (frequencyWord.containsKey(wordArr[i])) {
				int n = frequencyWord.get(wordArr[i]);
				frequencyWord.put(wordArr[i], ++n);
			} else {
				frequencyWord.put(wordArr[i], 1);
			}
			i++;
		}
		return frequencyWord;
	}

	public Sentence findRelationalFrequency(String doc, Sentence sentenceObj) {

		String noStopWordDoc = null;
		String wordArr[] = null;
		List<String[]> allCharGramListsDoc = null;
		List<String[]> allCharGramListsSent = null;

		try {
			noStopWordDoc = text.removeStopWords(doc);
			wordArr = text.splitToWords(noStopWordDoc);
			allCharGramListsDoc = text.splitToChar(noStopWordDoc);
			allCharGramListsSent = text.splitToChar(sentenceObj.noStopWordSentence);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Map<String, Integer> wordFreInDoc = findWordFrequency(wordArr);

		wordArr = text.splitToWords(sentenceObj.noStopWordSentence);
		Map<String, Integer> wordFreInSent = findWordFrequency(wordArr);

		float vds = calculateRelationalFrequency(wordFreInDoc, wordFreInSent);

		sentenceObj.setWord_5(round((float) (vds * 0.05)));
		sentenceObj.setWord_95(round((float) (vds * 0.95)));
		sentenceObj.setWord_Mean(round(vds / wordFreInDoc.size()));

		// Char 1 gram
		wordFreInDoc = findWordFrequency(allCharGramListsDoc.get(0));
		wordFreInSent = findWordFrequency(allCharGramListsSent.get(0));

		vds = calculateRelationalFrequency(wordFreInDoc, wordFreInSent);
		sentenceObj.setChar1_5(round((float) (vds * 0.05)));
		sentenceObj.setChar1_95(round((float) (vds * 0.95)));
		sentenceObj.setChar1_Mean(round(vds / wordFreInDoc.size()));

		// Char 3 gram
		wordFreInDoc = findWordFrequency(allCharGramListsDoc.get(1));
		wordFreInSent = findWordFrequency(allCharGramListsSent.get(1));

		vds = calculateRelationalFrequency(wordFreInDoc, wordFreInSent);
		sentenceObj.setChar3_5(round((float) (vds * 0.05)));
		sentenceObj.setChar3_95(round((float) (vds * 0.95)));
		sentenceObj.setChar3_Mean(round((vds / wordFreInDoc.size())));

		// Char 4 gram
		wordFreInDoc = findWordFrequency(allCharGramListsDoc.get(2));
		wordFreInSent = findWordFrequency(allCharGramListsSent.get(2));

		vds = calculateRelationalFrequency(wordFreInDoc, wordFreInSent);
		sentenceObj.setChar4_5(round((float) (vds * 0.05)));
		sentenceObj.setChar4_95(round((float) (vds * 0.95)));
		sentenceObj.setChar4_Mean(round(vds / wordFreInDoc.size()));

		sentenceObj.setLengthByChar(allCharGramListsSent.get(0).length);

		return sentenceObj;
	}

	public float calculateRelationalFrequency(Map<String, Integer> wordFreInDoc, Map<String, Integer> wordFreInSent) {

		int ndw = 0;
		float vdw, vds = 0;
		// most frequency word
		int maxNdw = (Collections.max(wordFreInDoc.values()));
		for (Entry<String, Integer> word : wordFreInSent.entrySet()) {
			if (wordFreInDoc.get(word.getKey()) != null) {
				ndw = wordFreInDoc.get(word.getKey());
			}
			// problem some trigram-chars doesn't exist in document
			int n = ndw - word.getValue() + 1;
			if (n <= 0)
				n = 1;
			vdw = (float) Math.log(maxNdw / n);
			// problem a tri-gram char=1 in doc, =2 in sentence => /0
			vds = vds + vdw;
		}

		return vds;
	}

	public Sentence findPOSFrequency(Sentence sentence) {

		POS_tagger posTaggerObj = new POS_tagger();
		String sen, noStopWordSent = null;
		String wordArr[] = null;
		try {
			noStopWordSent = text.removeStopWords(sentence.getOriginalSentence());
			sen = posTaggerObj.builtPOS(noStopWordSent);
			wordArr = text.splitToWords(sen);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, Float> frequencyPos = new HashMap<>();
		int i = 0;
		for (Map.Entry<String, List<String>> posTag : posTags.entrySet()) {
			frequencyPos.put(posTag.getKey(), (float) 0);
		}

		while (i < wordArr.length) {
			for (Map.Entry<String, List<String>> posTag : posTags.entrySet()) {
				int j = 0;
				while (j < posTag.getValue().size()) {
					if (wordArr[i].substring(wordArr[i].lastIndexOf("_") + 1).equals(posTag.getValue().get(j))) {
						if (frequencyPos.get(posTag.getKey()) != 0) {
							float n = frequencyPos.get(posTag.getKey());
							frequencyPos.put(posTag.getKey(), round(n + (float) 1 / wordArr.length));
							break;
						} else {
							frequencyPos.put(posTag.getKey(), round((float) 1 / wordArr.length));
							break;
						}
					}
					j++;
				}
			}
			i++;
		}
		sentence.setNum_POS(frequencyPos);
		sentence.setNoStopWordSentence(noStopWordSent);
		sentence.setLengthByWords(wordArr.length);

		return sentence;
	}

	public Sentence findPunctuationFrequency(Sentence sentenceObj) {

		Map<Character, Float> frequencyPun = new HashMap<>();
		for (Character pu : punctuationList) {
			frequencyPun.put(pu, (float) 0);
		}
		for (Character ch : sentenceObj.getOriginalSentence().toCharArray()) {

			if (!(ch == ' ')) {
				if (!(ch >= 'a' && ch <= 'z')) {
					for (Character pu : punctuationList) {
						if (ch.equals(pu)) {
							if (frequencyPun.get(pu) != null) {
								float n = frequencyPun.get(pu);
								frequencyPun.put(pu, round(n + (float) 1 / sentenceObj.getLengthByWords()));
								break;
							} else {
								frequencyPun.put(pu, round((float) 1 / sentenceObj.getLengthByWords()));
								break;
							}
						}
					}
				}
			}
		}

		sentenceObj.setNum_punctuation(frequencyPun);

		return sentenceObj;

	}

	public static float round(float value) {
		int places = 2;
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.floatValue();
	}

	public DocumentCl setFeatureToSentence(DocumentCl document, Boolean isPlagi) {
		Text text = new Text();
		List<Sentence> sentenceListNew = new ArrayList<>();
		Features feat = new Features();
		if (isPlagi) {
			for (Entry<Integer, String> passage : document.getPassageLable().entrySet()) {
				List<Sentence> sentenceList = text.splitToSentences(passage.getValue());

				for (int j = 0; j < sentenceList.size(); j++) {

					Sentence sentObj = new Sentence();
					sentObj.setOriginalSentence(sentenceList.get(j).getOriginalSentence());
					sentObj = feat.findPOSFrequency(sentObj);
					sentObj = feat.findPunctuationFrequency(sentObj);
					sentObj = feat.findRelationalFrequency(document.getOriginalDoc(), sentObj);

					if (passage.getKey() % 2 != 0) {
						sentObj.setY(0);
					} else
						sentObj.setY(1);
					sentenceListNew.add(sentObj);
				}
			}
		} else {

			List<Sentence> sentenceList = text.splitToSentences(document.originalDoc);

			for (int j = 0; j < sentenceList.size(); j++) {

				Sentence sentObj = new Sentence();
				sentObj.setOriginalSentence(sentenceList.get(j).getOriginalSentence());
				sentObj = feat.findPOSFrequency(sentObj);
				sentObj = feat.findPunctuationFrequency(sentObj);
				sentObj = feat.findRelationalFrequency(document.getOriginalDoc(), sentObj);
				sentObj.setY(0);

				sentenceListNew.add(sentObj);
			}
		}

		document.setSentencesInDoc(sentenceListNew);

		return document;
	}

}
