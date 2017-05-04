package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.SentenceUtils;
import edu.stanford.nlp.process.DocumentPreprocessor;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;

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
		return str.toLowerCase();
	}

	public List<DocumentCl> splitToParagraphes(String textOrig) {

		List<DocumentCl> documents = new ArrayList<DocumentCl>();
		Pattern pattern = Pattern.compile("(?:[^\n][\n]?)+", Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(textOrig);
		while (matcher.find()) {
			DocumentCl document = new DocumentCl();
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

	public Map<Integer, Integer> getOffSetPlagiList(String pt) {
		Map<Integer, Integer> offsetLenghtPla = new TreeMap<Integer, Integer>();
		try {
			File fXmlFile = new File(pt);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("feature");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getAttributes().getNamedItem("name").getNodeValue().equals("plagiarism")) {
					offsetLenghtPla.put(
							Integer.valueOf(nNode.getAttributes().getNamedItem("this_offset").getNodeValue()),
							Integer.valueOf(nNode.getAttributes().getNamedItem("this_length").getNodeValue()));
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return offsetLenghtPla;
	}

	public DocumentCl setLableToPassage(DocumentCl document) {

		Map<Integer, String> passageLable = new TreeMap<Integer, String>();
		int begin = 0;
		int end = 0, m = 0;

		for (Entry<Integer, Integer> offs : document.offsetLenghtPla.entrySet()) {

			m++;
			if (begin != offs.getKey()) {
				// if plagiarism offset is just right next after each other
				end = offs.getKey() - 1;
				passageLable.put(m, document.getOriginalDoc().substring(begin, end));
			}

			m++;
			begin = offs.getKey();
			end = begin + offs.getValue();
			passageLable.put(m, document.getOriginalDoc().substring(begin, end));

			begin = end + 1;

		}
		if (end < document.getOriginalDoc().length() && (document.getOriginalDoc().length() - end) >= 10) {
			// get the last passage which is not plagiarism
			m++;
			passageLable.put(m, document.getOriginalDoc().substring(end + 1, document.getOriginalDoc().length()));
		}

		document.setPassageLable(passageLable);
		return document;
	}

	public void extractTextAndGroundTruth(String pt) {
		File folder = new File(pt);
		File[] listOfPart = folder.listFiles();
		for (int i = 0; i < listOfPart.length; i++) {
			if (listOfPart[i].isDirectory()) {
				File file = new File(pt + "/" + listOfPart[i].getName());
				File[] listOfFile = file.listFiles();
				if (listOfFile.length != 0) {
					DocumentCl document = new DocumentCl();
					for (int j = 0; j < listOfFile.length; j++) {
						boolean isPlagi = true;
						if (listOfFile[j].isFile() && listOfFile[j].getName().endsWith(".txt")) {
							document.setOriginalDoc(
									readTextFile(pt + "/" + listOfPart[i].getName() + "/" + listOfFile[j].getName()));
						}
						if (listOfFile[j].isFile() && listOfFile[j].getName().endsWith(".xml")) {

							document.setOffsetLenghtPla(getOffSetPlagiList(
									pt + "/" + listOfPart[i].getName() + "/" + listOfFile[j].getName()));

							if (document.getOffsetLenghtPla().isEmpty()) {
								isPlagi = false;
							}
						}

						if (!document.getOffsetLenghtPla().isEmpty() || !isPlagi) {
							Features feat = new Features();
							if (isPlagi) {
								document = feat.setFeatureToSentence(setLableToPassage(document), isPlagi);

							} else {
								document = feat.setFeatureToSentence(document, isPlagi);
							}
							String fileName = listOfFile[j].getName().substring(0, listOfFile[j].getName().length() - 4)
									+ ".arff";
							writFeatureToFile(document, "result" + "/" + listOfPart[i].getName() + "/" + fileName);

							document = new DocumentCl();
						}
					}
				}
			}
		}
	}

	public void writFeatureToFile(DocumentCl docObj, String pt) {

		ArrayList<Attribute> atts = new ArrayList<Attribute>();
		ArrayList<Instance> instanceList = new ArrayList<Instance>();
		atts.add(new Attribute("c1_M", 0));
		atts.add(new Attribute("c1_5", 1));
		atts.add(new Attribute("c1_95", 2));
		atts.add(new Attribute("c3_M", 3));
		atts.add(new Attribute("c3_5", 4));
		atts.add(new Attribute("c3_95", 5));
		atts.add(new Attribute("c4_M", 6));
		atts.add(new Attribute("c4_5", 7));
		atts.add(new Attribute("c4_95", 8));
		atts.add(new Attribute("w_M", 9));
		atts.add(new Attribute("w_5", 10));
		atts.add(new Attribute("w_95", 11));
		atts.add(new Attribute("lw", 12));
		atts.add(new Attribute("lc", 13));
		atts.add(new Attribute("det", 14));
		atts.add(new Attribute("adv", 15));
		atts.add(new Attribute("prt", 16));
		atts.add(new Attribute("pron", 17));
		atts.add(new Attribute("verb", 18));
		atts.add(new Attribute("adj", 19));
		atts.add(new Attribute("conj", 20));
		atts.add(new Attribute("num", 21));
		atts.add(new Attribute("adp", 22));
		atts.add(new Attribute("noun", 23));
		atts.add(new Attribute("!", 24));
		atts.add(new Attribute("semi", 25));
		atts.add(new Attribute("+", 26));
		atts.add(new Attribute(",", 27));
		atts.add(new Attribute("-", 28));
		atts.add(new Attribute(".", 29));
		atts.add(new Attribute("?", 30));
		atts.add(new Attribute("y", 31));

		for (Sentence sentObj : docObj.getSentencesInDoc()) {
			Instance inst = new DenseInstance(31);

			inst.setValue(atts.get(0), sentObj.char1_Mean);
			inst.setValue(atts.get(1), sentObj.char1_5);
			inst.setValue(atts.get(2), sentObj.char1_95);
			inst.setValue(atts.get(3), sentObj.char3_Mean);
			inst.setValue(atts.get(4), sentObj.char3_5);
			inst.setValue(atts.get(5), sentObj.char3_95);
			inst.setValue(atts.get(6), sentObj.char4_Mean);
			inst.setValue(atts.get(7), sentObj.char4_5);
			inst.setValue(atts.get(8), sentObj.char4_95);
			inst.setValue(atts.get(9), sentObj.word_Mean);
			inst.setValue(atts.get(10), sentObj.word_5);
			inst.setValue(atts.get(11), sentObj.word_95);
			inst.setValue(atts.get(12), sentObj.lengthByWords);
			inst.setValue(atts.get(13), sentObj.lengthByChar);
			int k = 14;
			for (Entry<String, Float> pos : sentObj.num_POS.entrySet()) {
				inst.setValue(atts.get(k), pos.getValue());
				k++;
			}

			int m = k;

			for (Entry<Character, Float> pun : sentObj.num_punctuation.entrySet()) {
				inst.setValue(atts.get(m), pun.getValue());

				m++;
			}

			inst.setValue(atts.get(30), sentObj.y);
			instanceList.add(inst);
		}

		Instances newDataset = new Instances("Sentence", atts, instanceList.size());

		for (Instance insts : instanceList)
			newDataset.add(insts);

		ArffSaver saver = new ArffSaver();
		saver.setInstances(newDataset);
		try {
			saver.setFile(new File(pt));
			saver.writeBatch();
			System.out.println("Save");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
