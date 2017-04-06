package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

	public Map<Integer, String> splitToParagraphs(String textOrig) {

		Map<Integer, String> documents = new HashMap<Integer, String>();

		Pattern pattern = Pattern.compile("(?:[^\n][\n]?)+", Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(textOrig);
		int i = 0;
		while (matcher.find()) {
			i++;
			String paragraph = matcher.group();
			documents.put(i, paragraph);
//			System.out.println("Paragraph: " + paragraph.trim() + "\n");

		}
		return documents;
	}

	public Map<Integer, String> splitToSentences(String paragraph) {
	
		Map<Integer, String> sentences = new HashMap<Integer, String>();
		Reader reader = new StringReader(paragraph);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		List<String> sentenceList = new ArrayList<String>();

		for (List<HasWord> sentence : dp) {
		   // SentenceUtils not Sentence
		   String sentenceString = SentenceUtils.listToString(sentence);
		   sentenceList.add(sentenceString);
		}
		int i = 0;
		
		for (String sentence : sentenceList) {
			i++;
			sentences.put(i, sentence);
//		   System.out.println(sentence);
		}
		return sentences;
	}	

}
