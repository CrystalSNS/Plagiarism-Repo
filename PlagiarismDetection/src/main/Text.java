package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.process.DocumentPreprocessor;
import tagger.POS_tagger;

public class Text {

	public List<String> punctuationList = Arrays.asList("!", ",", ".", "?", "-", ";");
	List<HashMap<String, String[]>> myMap;
	HashMap<String, String[]> test;

	public String readTextFile(String pathStr) {

		File file = new File(pathStr);
		FileInputStream fis;
		String str = "";
		try {
			fis = new FileInputStream(file);
			byte[] data = new byte[(int) file.length()];
			try {
				fis.read(data);
				fis.close();
				str = new String(data, "UTF-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		return str;
	}

	public List<Document> splitToParagraphes(String textOrig) {

		List<Document> documents = new ArrayList<Document>();
		Pattern pattern = Pattern.compile("(?:[^\n][\n]?)+", Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(textOrig);
		int i = 0;
		while (matcher.find()) {
			Document document = new Document();
			i++;
			String paragraph = matcher.group();
			document.setOriginalDoc(paragraph.toLowerCase());
			document.setId(i);
			documents.add(document);

		}
		return documents;
	}

	public List<Sentence> splitToSentences(String paragraph) {

		Reader reader = new StringReader(paragraph);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		List<Sentence> sentenceList = new ArrayList<Sentence>();

		int i = 0;
		for (List<HasWord> sen : dp) {
			Sentence sentence = new Sentence();
			i++;
			// SentenceUtils not Sentence
			String sentenceString = SentenceUtils.listToString(sen);
			sentence.setOriginalSentence(sentenceString);
			sentence.setId(i);
			sentenceList.add(sentence);
		}
		return sentenceList;
	}

	public String[] splitToWords(String sentence) {

		StringTokenizer st = new StringTokenizer(sentence, " ");
		String wordArr[] = new String[st.countTokens()];
		wordArr = sentence.split("([.,!?:;'\"-]|\\s)+");
		return wordArr;
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

	public Map<String, Integer> findPOSFrequency(String sentence) {

		Map<String, List<String>> posTags = new HashMap<>();
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

		POS_tagger posTaggerObj = new POS_tagger();
		String sen;
		String wordArr[] = null;
		try {
			sen = posTaggerObj.builtPOS(sentence);
			StringTokenizer st = new StringTokenizer(sen, " ");
			wordArr = new String[st.countTokens()];
			wordArr = sen.split("([.,!?:;'\"-]|\\s)+");
		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, Integer> frequencyPos = new HashMap<>();
		int i = 0;
		while (i < wordArr.length) {
			for (Map.Entry<String, List<String>> posTag : posTags.entrySet()) {
				int j = 0;
				while (j < posTag.getValue().size()) {
					if (wordArr[i].substring(wordArr[i].lastIndexOf("_") + 1).equals(posTag.getValue().get(j))) {
						if (frequencyPos.get(posTag.getKey()) != null) {
							int n = frequencyPos.get(posTag.getKey());
							frequencyPos.put(posTag.getKey(), ++n);
						} else {
							frequencyPos.put(posTag.getKey(), 1);
						}
					}
					j++;
				}
			}
			i++;
		}
		return frequencyPos;
	}

}
