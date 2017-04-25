package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

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
		while (matcher.find()) {
			Document document = new Document();
			String paragraph = matcher.group();
			document.setOriginalDoc(paragraph.toLowerCase());
			documents.add(document);
		}
		return documents;
	}

	public String removeStopWords(String str) throws Exception {

		StandardTokenizer stdToken = new StandardTokenizer();
		stdToken.setReader(new StringReader(str));
		TokenStream tokenStream;
		tokenStream = new StopFilter(stdToken, EnglishAnalyzer.getDefaultStopSet());
		tokenStream.reset();

		StringBuilder sb = new StringBuilder();
		CharTermAttribute token = tokenStream.getAttribute(CharTermAttribute.class);
		while (tokenStream.incrementToken()) {
			if (sb.length() > 0) {
				sb.append(" ");
			}
			sb.append(token.toString());
		}
		tokenStream.close();
		return sb.toString();
	}

	public List<Sentence> splitToSentences(String paragraph) {

		// problem with "" ()
		paragraph = paragraph.replaceAll("[\\(\\)]", "");

		Reader reader = new StringReader(paragraph);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		List<Sentence> sentenceList = new ArrayList<Sentence>();

		for (List<HasWord> sen : dp) {
			Sentence sentence = new Sentence();
			String sentenceString = SentenceUtils.listToString(sen);
			sentence.setOriginalSentence(sentenceString);
			sentenceList.add(sentence);
		}
		return sentenceList;
	}

	public String[] splitToWords(String str) {

		StringTokenizer st = new StringTokenizer(str, " ");
		String wordArr[] = new String[st.countTokens()];
		wordArr = str.split("([(.),!?:;'\"-]|\\s)+");

		return wordArr;
	}

	public List<String[]> splitToChar(String str) throws IOException {

		str = str.replaceAll("[ \\\n]", "");
		StringReader stringReader = new StringReader(str);
		NGramTokenizer tokenizer = new NGramTokenizer(3, 4);
		List<String[]> allCharGramLists = new ArrayList<>();
		List<String> trigram = new ArrayList<>();
		List<String> fourgram = new ArrayList<>();
		tokenizer.setReader(stringReader);
		List<String> unigram = new ArrayList<String>(Arrays.asList(str.split("")));
		tokenizer.reset();
		CharTermAttribute termAtt = tokenizer.getAttribute(CharTermAttribute.class);
		while (tokenizer.incrementToken()) {
			String tok = termAtt.toString();
			if (tok.length() == 3) {
				trigram.add(tok);
			} else {
				fourgram.add(tok);
			}
		}
		String[] stockArr = new String[unigram.size()];
		stockArr = unigram.toArray(stockArr);
		allCharGramLists.add(stockArr);

		stockArr = new String[trigram.size()];
		stockArr = trigram.toArray(stockArr);
		allCharGramLists.add(stockArr);

		stockArr = new String[fourgram.size()];
		stockArr = fourgram.toArray(stockArr);
		allCharGramLists.add(stockArr);

		tokenizer.end();
		tokenizer.close();

		return allCharGramLists;

	}

	public void writFeatureToTextFile(Sentence sentObj, boolean isLast, String pt) {

		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new FileOutputStream(new File(pt), true));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		pw.print(sentObj.char1_Mean + "\t");
		pw.print(sentObj.char1_5 + "\t");
		pw.print(sentObj.char1_95 + "\t");

		pw.print(sentObj.char3_Mean + "\t");
		pw.print(sentObj.char3_5 + "\t");
		pw.print(sentObj.char3_95 + "\t");

		pw.print(sentObj.char4_Mean + "\t");
		pw.print(sentObj.char4_5 + "\t");
		pw.print(sentObj.char4_95 + "\t");

		pw.print(sentObj.word_Mean + "\t");
		pw.print(sentObj.word_5 + "\t");
		pw.print(sentObj.word_95 + "\t");

		pw.print(sentObj.lengthByWords + "\t");
		pw.print(sentObj.lengthByChar + "\t");

		for (Entry<String, Float> pos : sentObj.num_POS.entrySet()) {
			pw.print(pos.getValue() + "\t");
		}

		for (Entry<Character, Float> pun : sentObj.num_punctuation.entrySet()) {
			pw.print(pun.getValue() + "\t");
		}

		pw.print("\n\n");

		if (isLast)
			pw.print("\n\n\n\n");
		
		pw.close();
	}
}
