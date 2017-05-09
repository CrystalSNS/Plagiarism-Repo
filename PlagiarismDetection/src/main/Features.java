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
import java.util.StringTokenizer;

import tagger.POS_tagger;

public class Features {

	private static final List<Character> punctuationList = Arrays.asList('!', ',', '.', '?', '-', ';');
	private static Map<String, List<String>> posTags = new HashMap<>();
	private POS_tagger posTaggerObj = new POS_tagger();

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

	public Sentence findRelationalFrequency(DocumentCl doc, Sentence sentenceObj) {

		Map<String, Integer> wordFreInSent = findWordFrequency(sentenceObj.getWordArrInSent());

		float vds = calculateRelationalFrequency(doc.getWordFrequenInDoc(), wordFreInSent);

		sentenceObj.setWord_5(round((float) (vds * 0.05)));
		sentenceObj.setWord_95(round((float) (vds * 0.95)));
		sentenceObj.setWord_Mean(round(vds / doc.getWordFrequenInDoc().size()));

		// Char 1 gram
		wordFreInSent = findWordFrequency(sentenceObj.getAllCharGramListsInSent().get(0));

		vds = calculateRelationalFrequency(doc.getChar1FrequenInDoc(), wordFreInSent);
		sentenceObj.setChar1_5(round((float) (vds * 0.05)));
		sentenceObj.setChar1_95(round((float) (vds * 0.95)));
		sentenceObj.setChar1_Mean(round(vds / doc.getChar1FrequenInDoc().size()));

		// Char 3 gram
		wordFreInSent = findWordFrequency(sentenceObj.getAllCharGramListsInSent().get(1));

		vds = calculateRelationalFrequency(doc.getChar3FrequenInDoc(), wordFreInSent);
		sentenceObj.setChar3_5(round((float) (vds * 0.05)));
		sentenceObj.setChar3_95(round((float) (vds * 0.95)));
		sentenceObj.setChar3_Mean(round((vds / doc.getChar3FrequenInDoc().size())));

		// Char 4 gram
		wordFreInSent = findWordFrequency(sentenceObj.getAllCharGramListsInSent().get(2));

		vds = calculateRelationalFrequency(doc.getChar4FrequenInDoc(), wordFreInSent);
		sentenceObj.setChar4_5(round((float) (vds * 0.05)));
		sentenceObj.setChar4_95(round((float) (vds * 0.95)));
		sentenceObj.setChar4_Mean(round(vds / doc.getChar4FrequenInDoc().size()));

		sentenceObj.setLengthByChar(sentenceObj.getAllCharGramListsInSent().get(0).length);

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

		String wordArrWithPOS[] = null;
		try {
			String sen = posTaggerObj.builtPOS(sentence.getNoStopWordSentence());
			StringTokenizer st = new StringTokenizer(sen, " ");
			wordArrWithPOS = new String[st.countTokens()];
			wordArrWithPOS = sen.split("([.,!?:;'\"-]|\\s)+");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, Float> frequencyPos = new HashMap<>();
		int i = 0;
		for (Map.Entry<String, List<String>> posTag : posTags.entrySet()) {
			frequencyPos.put(posTag.getKey(), (float) 0);
		}

		while (i < wordArrWithPOS.length) {
			for (Map.Entry<String, List<String>> posTag : posTags.entrySet()) {
				int j = 0;
				while (j < posTag.getValue().size()) {
					if (wordArrWithPOS[i].substring(wordArrWithPOS[i].lastIndexOf("_") + 1).equals(posTag.getValue().get(j))) {
						if (frequencyPos.get(posTag.getKey()) != 0) {
							float n = frequencyPos.get(posTag.getKey());
							frequencyPos.put(posTag.getKey(), round(n + (float) 1 / sentence.getWordArrInSent().length));
							break;
						} else {
							frequencyPos.put(posTag.getKey(), round((float) 1 / sentence.getWordArrInSent().length));
							break;
						}
					}
					j++;
				}
			}
			i++;
		}
		sentence.setNum_POS(frequencyPos);
		sentence.setLengthByWords(sentence.getWordArrInSent().length);

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

	public DocumentCl setFeatureToSentence(DocumentCl document, Integer isPlagi) {

		Text text = new Text();
		List<Sentence> sentenceListNew = new ArrayList<>();
		Sentence sentObj = null;

		if (isPlagi == 1) {
			for (int i = 0; i < document.getOffSetInDoc().size(); i++) {
				List<Sentence> sentenceList = text.splitToSentences(document.getOffSetInDoc().get(i).getPassage());
				for (int j = 0; j < sentenceList.size(); j++) {
					sentObj = new Sentence();
					sentObj.setOriginalSentence(sentenceList.get(j).getOriginalSentence());
					sentObj = constructFeature(document, sentObj);
					sentObj.y = document.getOffSetInDoc().get(i).getLable();
					sentenceListNew.add(sentObj);
				}
			}
		} else {
			List<Sentence> sentenceList = text.splitToSentences(document.originalDoc);
			for (int j = 0; j < sentenceList.size(); j++) {
				sentObj = new Sentence();
				sentObj.setOriginalSentence(sentenceList.get(j).getOriginalSentence());
				sentObj = constructFeature(document, sentObj);
				sentObj.y = 0;
				sentenceListNew.add(sentObj);
			}
		}
		document.setSentencesInDoc(sentenceListNew);

		return document;
	}

	public Sentence constructFeature(DocumentCl document, Sentence sentObj) {
		Text text = new Text();
		try {
			sentObj.setNoStopWordSentence(text.removeStopWords(sentObj.getOriginalSentence()));
			sentObj.setAllCharGramListsInSent(text.splitToChar(sentObj.getNoStopWordSentence()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		sentObj.setWordArrInSent(text.splitToWords(sentObj.getNoStopWordSentence()));
		sentObj = findPOSFrequency(sentObj);
		sentObj = findPunctuationFrequency(sentObj);
		sentObj = findRelationalFrequency(document, sentObj);
		
		return sentObj;
	}

}
