package main;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

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

	public float findRelationalFrequencyOfWord(Integer numMostWordDoc, Integer numWordDoc, Integer numWordSent) {

		return (float) Math.log(numMostWordDoc / (numWordDoc - numWordSent + 1));
	}

	public float findMean(float[] setOfValue) {
		double sum = 0;
		for (int i = 0; i < setOfValue.length; i++) {
			sum += setOfValue[i];
		}
		return (float) (sum / setOfValue.length);
	}

	public float find5Percent(float[] setOfValue) {
		double sum = 0;
		for (int i = 0; i < setOfValue.length; i++) {
			sum += setOfValue[i];
		}
		return (float) (sum * 5 / 100);
	}

	public float find95Percent(float[] setOfValue) {
		double sum = 0;
		for (int i = 0; i < setOfValue.length; i++) {
			sum += setOfValue[i];
		}
		return (float) (sum * 95 / 100);
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

	public Map<String, Float> findPOSFrequency(Sentence sentence) {

		POS_tagger posTaggerObj = new POS_tagger();
		String sen;
		String wordArr[] = null;
		try {
			sen = posTaggerObj.builtPOS(sentence.getOriginalSentence());
			StringTokenizer st = new StringTokenizer(sen, " ");
			wordArr = new String[st.countTokens()];
			wordArr = sen.split("([.,!?:;'\"-]|\\s)+");
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
							frequencyPos.put(posTag.getKey(), n + (float) 1 / sentence.getLengthByWords());
							break;
						} else {
							frequencyPos.put(posTag.getKey(), (float) 1 / sentence.getLengthByWords());
							break;
						}
					}
					j++;
				}
			}
			i++;
		}
		return frequencyPos;
	}

	public Sentence findPunctuationFrequency(Sentence sentence) {

		Map<Character, Float> frequencyPun = new HashMap<>();
		int charCounter = 0;
		for (Character ch : sentence.getOriginalSentence().toCharArray()) {
			
			if (!(ch == ' ')) {
				charCounter++;
				if (!(ch >= 'a' && ch <= 'z')) {
					for (Character pu : punctuationList) {
						if (ch.equals(pu)) {
							if (frequencyPun.get(pu) != null) {
								float n = frequencyPun.get(pu);
								frequencyPun.put(pu, n + (float) 1 / sentence.getLengthByWords());
								break;
							} else {
								frequencyPun.put(pu, (float) 1 / sentence.getLengthByWords());
								break;
							}
						}
					}
				}
			}
		}
		sentence.setNum_punctuation(frequencyPun);
		sentence.setLengthByChar(charCounter);
		return sentence;

	}
}
