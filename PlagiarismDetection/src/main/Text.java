package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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

	public List<OffSetCl> getOffSetPlagiListFromXml(String pt, Integer docLen) {
		List<OffSetCl> offSetList = new ArrayList<OffSetCl>();
		OffSetCl offSet;
		try {
			File fXmlFile = new File(pt);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("feature");
			int begin, end = 0, len, first = 0;
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				offSet = new OffSetCl();
				if (nNode.getAttributes().getNamedItem("name").getNodeValue().equals("plagiarism")) {
					first++;
					begin = Integer.valueOf(nNode.getAttributes().getNamedItem("this_offset").getNodeValue());
					len = Integer.valueOf(nNode.getAttributes().getNamedItem("this_length").getNodeValue());
					if (first == 1 && begin != 0) {
						end = begin - 1;
						offSet.setBegin(0);
						offSet.setEnd(end);
						offSet.setLable(0);
						offSetList.add(offSet);
					}

					if (begin + len != begin) {
						offSet = new OffSetCl();
						end = begin + len;
						offSet.setBegin(begin);
						offSet.setEnd(end);
						offSet.setLable(1);
						offSetList.add(offSet);
					}

					if (temp != nList.getLength() - 1) {
						nNode = nList.item(temp + 1);
						int begin1 = Integer.valueOf(nNode.getAttributes().getNamedItem("this_offset").getNodeValue());
						if (end + 1 != begin1) {
							offSet = new OffSetCl();
							begin = end + 1;
							end = begin1 - 1;
							offSet.setBegin(begin);
							offSet.setEnd(end);
							offSet.setLable(0);
							offSetList.add(offSet);
						}
					}
				}
			}
			if (end < docLen && (docLen - end) >= 10) {
				offSet = new OffSetCl();
				begin = end + 1;
				end = docLen;
				offSet.setBegin(begin);
				offSet.setEnd(end);
				offSet.setLable(0);
				offSetList.add(offSet);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return offSetList;
	}

	public List<OffSetCl> getOffSetPlagiListFromJson(String pt) {
		List<OffSetCl> offSetList = new ArrayList<OffSetCl>();
		OffSetCl offSet;
		JSONParser parser = new JSONParser();

		try {
			Object obj = parser.parse(new FileReader(pt));
			JSONObject authorObject = (JSONObject) obj;
			JSONArray authorsArr = (JSONArray) authorObject.get("authors");

			if (authorsArr != null) {
				int maxOffSet = 0, indexMax = 0;
				for (int i = 0; i < authorsArr.size(); i++) {
					JSONArray offSetArr = (JSONArray) authorsArr.get(i);
					if (offSetArr.size() > maxOffSet) {
						maxOffSet = offSetArr.size();
						indexMax = i;
					}
				}
				for (int i = 0; i < authorsArr.size(); i++) {
					JSONArray offSetArr = (JSONArray) authorsArr.get(i);
					int isPlagi = 0;
					if (indexMax != i) {
						isPlagi = 1;
					}
					for (int j = 0; j < offSetArr.size(); j++) {
						offSet = new OffSetCl();
						JSONObject offSetValue = (JSONObject) offSetArr.get(j);
						offSet.setBegin(((Long) offSetValue.get("from")).intValue());
						offSet.setEnd(((Long) offSetValue.get("to")).intValue());
						offSet.setLable(isPlagi);
						offSetList.add(offSet);
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return offSetList;
	}

	public DocumentCl setLableToPassage(DocumentCl document) {

		int begin = 0, end = 0;
		List<OffSetCl> offSetInDoc = document.getOffSetInDoc();
		Collections.sort(offSetInDoc, (o1, o2) -> o1.getBegin() - o2.getBegin());

		for (int i = 0; i < offSetInDoc.size(); i++) {
			begin = offSetInDoc.get(i).getBegin();
			end = offSetInDoc.get(i).getEnd();
			offSetInDoc.get(i).setPassage(document.getOriginalDoc().substring(begin, end));
		}

		document.setOffSetInDoc(offSetInDoc);

		return document;
	}

	public void extractTextAndGroundTruth(String pt) {
		File folder = new File(pt);
		File[] listOfPart = folder.listFiles();
		for (int i = 0; i < listOfPart.length; i++) {
			if (listOfPart[i].isDirectory()) {
				File file = new File(pt + "/" + listOfPart[i].getName());
				File[] listOfFile = file.listFiles();
				Features feat = new Features();

				if (listOfFile.length != 0) {
					DocumentCl document = new DocumentCl();
					for (int j = 0; j < listOfFile.length; j++) {
						boolean isPlagi = true;
						String file1 = "", file1NoExtension = "", file2 = "", file2NoExtension = "";
						if (j != listOfFile.length - 1) {
							file1 = listOfFile[j].getName();
							file1NoExtension = listOfFile[j].getName().substring(0, file1.lastIndexOf("."));
							file2 = listOfFile[j + 1].getName();
							file2NoExtension = listOfFile[j + 1].getName().substring(0, file2.lastIndexOf("."));
						}

						if (listOfFile[j].isFile() && (file1NoExtension == file2NoExtension) && (file1.endsWith(".txt") && file2.endsWith(".xml"))
								|| (file1.endsWith(".truth") && file2.endsWith(".txt")) || (file2.endsWith(".txt") && file1.endsWith(".xml"))
								|| (file2.endsWith(".truth") && file1.endsWith(".txt"))) {

							document.setOriginalDoc(readTextFile(pt + "/" + listOfPart[i].getName() + "/" + file1NoExtension + ".txt"));
							try {
								document.setNoStopWordDoc(removeStopWords(document.getOriginalDoc()));
								document.setWordArrInDoc(splitToWords(document.getNoStopWordDoc()));
								document.setAllCharGramListsInDoc(splitToChar(document.getNoStopWordDoc()));
								document.setWordFrequenInDoc(feat.findWordFrequency(document.getWordArrInDoc()));
								document.setChar1FrequenInDoc(feat.findWordFrequency(document.getAllCharGramListsInDoc().get(0)));
								document.setChar3FrequenInDoc(feat.findWordFrequency(document.getAllCharGramListsInDoc().get(1)));
								document.setChar4FrequenInDoc(feat.findWordFrequency(document.getAllCharGramListsInDoc().get(2)));
							} catch (Exception e) {
								e.printStackTrace();
							}

							if (file1.endsWith(".xml") || file2.endsWith(".xml")) {

								document.setOffSetInDoc(getOffSetPlagiListFromXml(pt + "/" + listOfPart[i].getName() + "/" + file1NoExtension + ".xml", document.getOriginalDoc().length()));
								if (document.getOffSetInDoc().isEmpty()) {
									isPlagi = false;
								}
							}
							if (file1.endsWith(".truth") || file2.endsWith(".truth")) {

								document.setOffSetInDoc(getOffSetPlagiListFromJson(pt + "/" + listOfPart[i].getName() + "/" + file1NoExtension + ".truth"));
							}

							if ((document.getOffSetInDoc().isEmpty() && !isPlagi) || (!document.getOffSetInDoc().isEmpty())) {
								if (isPlagi) {
									document = feat.setFeatureToSentence(setLableToPassage(document), 1);
								} else {
									document = feat.setFeatureToSentence(document, 0);
								}
								String fileName = listOfFile[j].getName().substring(0, listOfFile[j].getName().length() - 4) + ".arff";
								writFeatureToFile(document, "result" + "/" + listOfPart[i].getName() + "/" + fileName);

								document = new DocumentCl();
							}
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
		atts.add(new Attribute(",", 26));
		atts.add(new Attribute("-", 27));
		atts.add(new Attribute(".", 28));
		atts.add(new Attribute("?", 29));
		atts.add(new Attribute("y", 30));

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

			int k = 13;
			for (Entry<String, Float> pos : sentObj.num_POS.entrySet()) {
				k++;
				inst.setValue(atts.get(k), pos.getValue());
			}

			for (Entry<Character, Float> pun : sentObj.num_punctuation.entrySet()) {
				k++;
				inst.setValue(atts.get(k), pun.getValue());
			}
			k++;
			inst.setValue(atts.get(k), sentObj.y);
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
			e.printStackTrace();
		}
	}

}
