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

public class TextCl {

	ArrayList<Attribute> atts = new ArrayList<Attribute>();
	ArrayList<Instance> instanceList;
	private static final String attName[] = new String[] { "char1_mean", "char1_5p", "char1_95p", "char3_mean", "char3_5p", "char3_95p", "char4_mean", "char4_5p", "char4_95p",
			"word_mean", "word_5p", "word_95p", "length_by_word", "length_by-char", "det", "adv", "prt", "pron", "verb", "adj", "conj", "num", "adp", "noun", "!", ";", "comma",
			"-", ".", "?", "Y" };

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

	public List<SentenceCl> splitToSentences(String paragraph) {

		// problem with "" ()
		paragraph = paragraph.replaceAll("[\\(\\)]", "");

		Reader reader = new StringReader(paragraph);
		DocumentPreprocessor dp = new DocumentPreprocessor(reader);
		List<SentenceCl> sentenceList = new ArrayList<SentenceCl>();

		for (List<HasWord> sen : dp) {
			SentenceCl sentence = new SentenceCl();
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
							
							if(end != begin1){
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

	public void extractTextAndGroundTruth(String pt, Boolean hasGroundTruth) {
//		File folder = new File(pt);

//		File[] listOfPart1 = folder.listFiles();
//		for (int i = 0; i < listOfPart1.length; i++) {
//			if (listOfPart1[i].isDirectory()) {
//				File[] listOfPart2 = listOfPart1[i].listFiles();
//				for (int m = 0; m < listOfPart2.length; m++) {
//					if (listOfPart2[m].isDirectory()) {
//						System.out.println("Path:" + listOfPart2[m]);
						System.out.println("Path:" + pt);
//						File file = new File(pt + "/" + listOfPart1[i].getName() + "/" + listOfPart2[m].getName());
						File file = new File(pt);
						File[] listOfFile = file.listFiles();
						FeaturesCl feat = new FeaturesCl();

						createAttNameArr();

						if (listOfFile.length != 0 && hasGroundTruth) {
							DocumentCl document = new DocumentCl();
							for (int j = 0; j <= listOfFile.length; j++) {
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
									

//									document.setOriginalDoc(readTextFile(pt + "/" + listOfPart1[i].getName() + "/" + listOfPart2[m].getName() + "/" + file1NoExtension + ".txt"));
									document.setOriginalDoc(readTextFile(pt + "/" + file1NoExtension + ".txt"));
									if(!document.getOriginalDoc().isEmpty() && document.getOriginalDoc() != "" && document.getOriginalDoc().length() > 50  ){
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
	
	//										document.setOffSetInDoc(getOffSetPlagiListFromXml(pt + "/" + listOfPart1[i].getName() + "/" + listOfPart2[m].getName() + "/" + file1NoExtension + ".xml",document.getOriginalDoc().length()));
											document.setOffSetInDoc(getOffSetPlagiListFromXml(pt + "/" + file1NoExtension + ".xml",document.getOriginalDoc().length()));
											
											if (document.getOffSetInDoc().isEmpty()) {
												isPlagi = false;
											}
										}
										if (file1.endsWith(".truth") || file2.endsWith(".truth")) {
	
	//										document.setOffSetInDoc(getOffSetPlagiListFromJson(pt + "/" + listOfPart1[i].getName() + "/" + listOfPart2[m].getName() + "/" + file1NoExtension + ".truth"));
											document.setOffSetInDoc(getOffSetPlagiListFromJson(pt + "/" + file1NoExtension + ".truth"));
										}
	
										if ((document.getOffSetInDoc().isEmpty() && !isPlagi) || (!document.getOffSetInDoc().isEmpty())) {
											if (isPlagi) {
												document = feat.setFeatureToSentence(setLableToPassage(document), 1, hasGroundTruth);
											} else {
												document = feat.setFeatureToSentence(document, 0, hasGroundTruth);
											}
	
											String fileName = listOfFile[j].getName().substring(0, listOfFile[j].getName().length() - 4) + ".arff";
	//										writFeatureToFile(addFeatureToArray(document),"result/train/" + listOfPart1[i].getName() + "/" + listOfPart2[m].getName() + "/" + fileName);
											writFeatureToFile(addFeatureToArray(document),"result/train/" + pt.substring(7, pt.length()) + "/" + fileName);
	
											document = new DocumentCl();
										}
									}else{
										System.out.println("File text is empty or Length less than 50!");
									}
								
								}
								
							}
						} else if (listOfFile.length != 0 && !hasGroundTruth) {
							DocumentCl document = new DocumentCl();
							for (int j = 0; j < listOfFile.length; j++) {
								if (listOfFile[j].getName().endsWith("txt")) {
//									document.setOriginalDoc(readTextFile(pt + "/" + listOfPart1[i].getName() + "/" + listOfPart2[m].getName() + "/" + listOfFile[j].getName()));
									document.setOriginalDoc(readTextFile(pt + "/" + listOfFile[j].getName()));
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
									document = feat.setFeatureToSentence(document, 0, hasGroundTruth);

									String fileName = listOfFile[j].getName().substring(0, listOfFile[j].getName().length() - 4) + ".arff";
									writFeatureToFile(addFeatureToArray(document),"result/test/" + pt.substring(6, pt.length()-1) + "/" + fileName);

									document = new DocumentCl();
								}
							}
						}
					}
//				}
//			}
//		}
//	}

	public Map<Integer, Float[]> addFeatureToArray(DocumentCl docObj) {
		Map<Integer, Float[]> allfeatureArr = new TreeMap<Integer, Float[]>();
		Float featArr[];
		int i = 0;
		for (SentenceCl sentObj : docObj.getSentencesInDoc()) {
			featArr = new Float[31];
			featArr[0] = sentObj.char1_Mean;
			featArr[1] = sentObj.char1_5;
			featArr[2] = sentObj.char1_95;
			featArr[3] = sentObj.char3_Mean;
			featArr[4] = sentObj.char3_5;
			featArr[5] = sentObj.char3_95;
			featArr[6] = sentObj.char4_Mean;
			featArr[7] = sentObj.char4_5;
			featArr[8] = sentObj.char4_95;
			featArr[9] = sentObj.word_Mean;
			featArr[10] = sentObj.word_5;
			featArr[11] = sentObj.word_95;
			featArr[12] = (float) sentObj.lengthByWords;
			featArr[13] = (float) sentObj.lengthByChar;

			int k = 13;
			for (Entry<String, Float> pos : sentObj.num_POS.entrySet()) {
				k++;
				featArr[k] = pos.getValue();
			}

			for (Entry<Character, Float> pun : sentObj.num_punctuation.entrySet()) {
				k++;
				featArr[k] = pun.getValue();
			}
			k++;
			featArr[k] = (float) sentObj.y;
			allfeatureArr.put(i, featArr);
			i++;
		}

		return allfeatureArr;
	}

	public void writFeatureToFile(Map<Integer, Float[]> allfeatureArr, String pt) {

		Float zeroArr[], arr_im1[], arr_im2[], arr_i[], arr_ip1[], arr_ip2[];

		zeroArr = new Float[30];
		for (int j = 0; j < 30; j++) {
			zeroArr[j] = (float) 0;
		}
		instanceList = new ArrayList<Instance>();
		for (int i = 0; i < allfeatureArr.size(); i++) {

			arr_im1 = new Float[allfeatureArr.get(i).length - 1];
			arr_im2 = new Float[allfeatureArr.get(i).length - 1];
			arr_i = new Float[allfeatureArr.get(i).length - 1];
			arr_ip1 = new Float[allfeatureArr.get(i).length - 1];
			arr_ip2 = new Float[allfeatureArr.get(i).length - 1];

			if(allfeatureArr.size()>4){
				if (i == 0) {
					arr_im2 = zeroArr;
					arr_im1 = zeroArr;
					arr_ip1 = Arrays.copyOf(allfeatureArr.get(i + 1), allfeatureArr.get(i + 1).length - 1);
					arr_ip2 = Arrays.copyOf(allfeatureArr.get(i + 2), allfeatureArr.get(i + 2).length - 1);
				} else if (i == 1) {
					arr_im2 = zeroArr;
					arr_im1 = Arrays.copyOf(allfeatureArr.get(i - 1), allfeatureArr.get(i - 1).length - 1);
					arr_ip1 = Arrays.copyOf(allfeatureArr.get(i + 1), allfeatureArr.get(i + 1).length - 1);
					arr_ip2 = Arrays.copyOf(allfeatureArr.get(i + 2), allfeatureArr.get(i + 2).length - 1);
				} else if (i == allfeatureArr.size() - 2) {
					arr_im2 = Arrays.copyOf(allfeatureArr.get(i - 2), allfeatureArr.get(i - 2).length - 1);
					arr_im1 = Arrays.copyOf(allfeatureArr.get(i - 1), allfeatureArr.get(i - 1).length - 1);
					arr_ip1 = Arrays.copyOf(allfeatureArr.get(i + 1), allfeatureArr.get(i + 1).length - 1);
					arr_ip2 = zeroArr;
				} else if (i == allfeatureArr.size() - 1) {
					arr_im2 = Arrays.copyOf(allfeatureArr.get(i - 2), allfeatureArr.get(i - 2).length - 1);
					arr_im1 = Arrays.copyOf(allfeatureArr.get(i - 1), allfeatureArr.get(i - 1).length - 1);
					arr_ip1 = zeroArr;
					arr_ip2 = zeroArr;
				} else {
					arr_im2 = Arrays.copyOf(allfeatureArr.get(i - 2), allfeatureArr.get(i - 2).length - 1);
					arr_im1 = Arrays.copyOf(allfeatureArr.get(i - 1), allfeatureArr.get(i - 1).length - 1);
					arr_ip1 = Arrays.copyOf(allfeatureArr.get(i + 1), allfeatureArr.get(i + 1).length - 1);
					arr_ip2 = Arrays.copyOf(allfeatureArr.get(i + 2), allfeatureArr.get(i + 2).length - 1);
				}
			}else if(allfeatureArr.size()==4){
				if(i==0){
					arr_im2 = zeroArr;
					arr_im1 = zeroArr;
					arr_ip1 = Arrays.copyOf(allfeatureArr.get(i + 1), allfeatureArr.get(i + 1).length - 1);
					arr_ip2 = Arrays.copyOf(allfeatureArr.get(i + 2), allfeatureArr.get(i + 2).length - 1);
				}else if(i==1){
					arr_im2 = zeroArr;
					arr_im1 = Arrays.copyOf(allfeatureArr.get(i - 1), allfeatureArr.get(i - 1).length - 1);
					arr_ip1 = Arrays.copyOf(allfeatureArr.get(i + 1), allfeatureArr.get(i + 1).length - 1);
					arr_ip2 = Arrays.copyOf(allfeatureArr.get(i + 2), allfeatureArr.get(i + 2).length - 1);
				}else if(i==2){
					arr_im2 = Arrays.copyOf(allfeatureArr.get(i - 2), allfeatureArr.get(i - 2).length - 1);
					arr_im1 = Arrays.copyOf(allfeatureArr.get(i - 1), allfeatureArr.get(i - 1).length - 1);
					arr_ip1 = Arrays.copyOf(allfeatureArr.get(i + 1), allfeatureArr.get(i + 1).length - 1);
					arr_ip2 = zeroArr;
				}else if(i==3){
					arr_im2 = Arrays.copyOf(allfeatureArr.get(i - 2), allfeatureArr.get(i - 2).length - 1);
					arr_im1 = Arrays.copyOf(allfeatureArr.get(i - 1), allfeatureArr.get(i - 1).length - 1);
					arr_ip1 = zeroArr;
					arr_ip2 = zeroArr;
				}
			}else if(allfeatureArr.size()==3){
				if(i==0){
					arr_im2 = zeroArr;
					arr_im1 = zeroArr;
					arr_ip1 = Arrays.copyOf(allfeatureArr.get(i + 1), allfeatureArr.get(i + 1).length - 1);
					arr_ip2 = Arrays.copyOf(allfeatureArr.get(i + 2), allfeatureArr.get(i + 2).length - 1);
				}else if(i==1){
					arr_im2 = zeroArr;
					arr_im1 = Arrays.copyOf(allfeatureArr.get(i - 1), allfeatureArr.get(i - 1).length - 1);
					arr_ip1 = Arrays.copyOf(allfeatureArr.get(i + 1), allfeatureArr.get(i + 1).length - 1);
					arr_ip2 = zeroArr;
				}else if(i==2){
					arr_im2 = Arrays.copyOf(allfeatureArr.get(i - 2), allfeatureArr.get(i - 2).length - 1);
					arr_im1 = Arrays.copyOf(allfeatureArr.get(i - 1), allfeatureArr.get(i - 1).length - 1);
					arr_ip1 = zeroArr;
					arr_ip2 = zeroArr;
				}
			}else if(allfeatureArr.size()==2){
				if(i==0){
					arr_im2 = zeroArr;
					arr_im1 = zeroArr;
					arr_ip1 = Arrays.copyOf(allfeatureArr.get(i + 1), allfeatureArr.get(i + 1).length - 1);
					arr_ip2 = zeroArr;
				}else if(i==1){
					arr_im2 = zeroArr;
					arr_im1 = Arrays.copyOf(allfeatureArr.get(i - 1), allfeatureArr.get(i - 1).length - 1);
					arr_ip1 = zeroArr;
					arr_ip2 = zeroArr;
				}
			}else if(allfeatureArr.size()==1){
				arr_im2 = zeroArr;
				arr_im1 = zeroArr;
				arr_ip1 = zeroArr;
				arr_ip2 = zeroArr;
			}

			arr_i = Arrays.copyOf(allfeatureArr.get(i), allfeatureArr.get(i).length - 1);

			Float[] tempArr1 = new Float[arr_im2.length + arr_im1.length];
			System.arraycopy(arr_im2, 0, tempArr1, 0, arr_im2.length);
			System.arraycopy(arr_im1, 0, tempArr1, arr_im2.length, arr_im1.length);
			Float[] tempArr2 = new Float[tempArr1.length + arr_i.length];
			System.arraycopy(tempArr1, 0, tempArr2, 0, tempArr1.length);
			System.arraycopy(arr_i, 0, tempArr2, tempArr1.length, arr_i.length);
			tempArr1 = new Float[tempArr2.length + arr_ip1.length];
			System.arraycopy(tempArr2, 0, tempArr1, 0, tempArr2.length);
			System.arraycopy(arr_ip1, 0, tempArr1, tempArr2.length, arr_ip1.length);
			tempArr2 = new Float[tempArr1.length + arr_ip2.length + 1];
			System.arraycopy(tempArr1, 0, tempArr2, 0, tempArr1.length);
			System.arraycopy(arr_ip2, 0, tempArr2, tempArr1.length, arr_ip2.length);
			Float temp[] = allfeatureArr.get(i);
			tempArr2[tempArr2.length - 1] = temp[allfeatureArr.get(i).length - 1];
			arr_im1 = null;
			arr_im2 = null;
			arr_i = null;
			arr_ip1 = null;
			arr_ip2 = null;
			temp = null;
			tempArr1 = null;
			Instance inst = new DenseInstance(tempArr2.length);

			for (int k = 0; k < tempArr2.length; k++) {
				if (k != tempArr2.length - 1) {
					inst.setValue(atts.get(k), tempArr2[k]);

				} else {
					if (tempArr2[k].intValue() != 3) {
						inst.setValue(tempArr2.length - 1, tempArr2[k].intValue());
					}
				}
			}
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
			System.out.println("Saved file: " + pt);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createAttNameArr() {
		int indexAtt = 0;
		for (int j = 0; j < 5; j++) {
			String suffix_str = "";
			if (j == 0) {
				suffix_str = "_i-2";
			} else if (j == 1) {
				suffix_str = "_i-1";
			} else if (j == 2) {
				suffix_str = "_i";
			} else if (j == 3) {
				suffix_str = "_i+1";
			} else if (j == 4) {
				suffix_str = "_i+2";
			}
			for (int k = 0; k < attName.length; k++) {
				if (!attName[k].equals("Y")) {
					atts.add(new Attribute(attName[k] + suffix_str, indexAtt));
					indexAtt++;
				}
			}
		}
		Attribute classAtt = new Attribute("Y_i", new ArrayList<String>(Arrays.asList(new String[] { "0", "1" })));
		atts.add(classAtt);
	}

}
