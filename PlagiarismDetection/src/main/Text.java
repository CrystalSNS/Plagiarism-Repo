package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.process.DocumentPreprocessor;

public class Text {

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

}
