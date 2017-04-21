package main;

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

		sentenceObj.setWord_5(vds * 5 / 100);
		sentenceObj.setWord_95(vds * 95 / 100);
		sentenceObj.setWord_Mean(vds / wordFreInDoc.size());

		// Char 1 gram
		wordFreInDoc = findWordFrequency(allCharGramListsDoc.get(0));
		wordFreInSent = findWordFrequency(allCharGramListsSent.get(0));

		vds = calculateRelationalFrequency(wordFreInDoc, wordFreInSent);
		sentenceObj.setChar1_5((vds * 5 / 100));
		sentenceObj.setChar1_95(vds * 95 / 100);
		sentenceObj.setChar1_Mean(vds / wordFreInDoc.size());

		// Char 3 gram
		wordFreInDoc = findWordFrequency(allCharGramListsDoc.get(1));
		wordFreInSent = findWordFrequency(allCharGramListsSent.get(1));

		vds = calculateRelationalFrequency(wordFreInDoc, wordFreInSent);
		sentenceObj.setChar3_5((vds * 5 / 100));
		sentenceObj.setChar3_95(vds * 95 / 100);
		sentenceObj.setChar3_Mean(vds / wordFreInDoc.size());

		// Char 4 gram
		wordFreInDoc = findWordFrequency(allCharGramListsDoc.get(2));
		wordFreInSent = findWordFrequency(allCharGramListsSent.get(2));

		vds = calculateRelationalFrequency(wordFreInDoc, wordFreInSent);
		sentenceObj.setChar4_5((vds * 5 / 100));
		sentenceObj.setChar4_95(vds * 95 / 100);
		sentenceObj.setChar4_Mean(vds / wordFreInDoc.size());

		sentenceObj.setLengthByChar(allCharGramListsSent.get(0).length);
		
		return sentenceObj;
	}

	public float calculateRelationalFrequency(Map<String, Integer> wordFreInDoc, Map<String, Integer> wordFreInSent) {

		int ndw = 0;
		float vdw, vds = 0;
		int maxNdw = (Collections.max(wordFreInDoc.values()));
		for (Entry<String, Integer> word : wordFreInSent.entrySet()) {
			if (wordFreInDoc.get(word.getKey()) != null){
				ndw = wordFreInDoc.get(word.getKey());
			}
			// problem some trigram-chars doesn't exist in document
			vdw = (float) Math.log(maxNdw / ((ndw - word.getValue()) + 1));
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
		while (i < wordArr.length) {
			for (Map.Entry<String, List<String>> posTag : posTags.entrySet()) {
				int j = 0;
				while (j < posTag.getValue().size()) {
					if (wordArr[i].substring(wordArr[i].lastIndexOf("_") + 1).equals(posTag.getValue().get(j))) {
						if (frequencyPos.get(posTag.getKey()) != null) {
							float n = frequencyPos.get(posTag.getKey());
							frequencyPos.put(posTag.getKey(), n + (float) 1 / wordArr.length);
							break;
						} else {
							frequencyPos.put(posTag.getKey(), (float) 1 / wordArr.length);
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
		for (Character ch : sentenceObj.getOriginalSentence().toCharArray()) {

			if (!(ch == ' ')) {
				if (!(ch >= 'a' && ch <= 'z')) {
					for (Character pu : punctuationList) {
						if (ch.equals(pu)) {
							if (frequencyPun.get(pu) != null) {
								float n = frequencyPun.get(pu);
								frequencyPun.put(pu, n + (float) 1 / sentenceObj.getLengthByWords());
								break;
							} else {
								frequencyPun.put(pu, (float) 1 / sentenceObj.getLengthByWords());
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
}
