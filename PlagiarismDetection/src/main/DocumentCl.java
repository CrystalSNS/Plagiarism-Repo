package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentCl {

	String originalDoc;
	String noStopWordDoc;
	String wordArrInDoc[];
	List<String[]> allCharGramListsInDoc;
	List<SentenceCl> sentencesInDoc = new ArrayList<SentenceCl>();
	Map<String, Integer> wordFrequenInDoc = new HashMap<String, Integer>();
	Map<String, Integer> char1FrequenInDoc = new HashMap<String, Integer>();
	Map<String, Integer> char3FrequenInDoc = new HashMap<String, Integer>();
	Map<String, Integer> char4FrequenInDoc = new HashMap<String, Integer>();
	List<OffSetCl> offSetInDoc = new ArrayList<OffSetCl>();

	public String getOriginalDoc() {
		return originalDoc;
	}

	public void setOriginalDoc(String originalDoc) {
		this.originalDoc = originalDoc;
	}

	public String getNoStopWordDoc() {
		return noStopWordDoc;
	}

	public void setNoStopWordDoc(String noStopWordDoc) {
		this.noStopWordDoc = noStopWordDoc;
	}

	public String[] getWordArrInDoc() {
		return wordArrInDoc;
	}

	public void setWordArrInDoc(String[] wordArrInDoc) {
		this.wordArrInDoc = wordArrInDoc;
	}

	public List<String[]> getAllCharGramListsInDoc() {
		return allCharGramListsInDoc;
	}

	public void setAllCharGramListsInDoc(List<String[]> allCharGramListsInDoc) {
		this.allCharGramListsInDoc = allCharGramListsInDoc;
	}

	public List<SentenceCl> getSentencesInDoc() {
		return sentencesInDoc;
	}

	public void setSentencesInDoc(List<SentenceCl> sentencesInDoc) {
		this.sentencesInDoc = sentencesInDoc;
	}

	public Map<String, Integer> getWordFrequenInDoc() {
		return wordFrequenInDoc;
	}

	public void setWordFrequenInDoc(Map<String, Integer> wordFrequenInDoc) {
		this.wordFrequenInDoc = wordFrequenInDoc;
	}

	public Map<String, Integer> getChar1FrequenInDoc() {
		return char1FrequenInDoc;
	}

	public void setChar1FrequenInDoc(Map<String, Integer> char1FrequenInDoc) {
		this.char1FrequenInDoc = char1FrequenInDoc;
	}

	public Map<String, Integer> getChar3FrequenInDoc() {
		return char3FrequenInDoc;
	}

	public void setChar3FrequenInDoc(Map<String, Integer> char3FrequenInDoc) {
		this.char3FrequenInDoc = char3FrequenInDoc;
	}

	public Map<String, Integer> getChar4FrequenInDoc() {
		return char4FrequenInDoc;
	}

	public void setChar4FrequenInDoc(Map<String, Integer> char4FrequenInDoc) {
		this.char4FrequenInDoc = char4FrequenInDoc;
	}

	public List<OffSetCl> getOffSetInDoc() {
		return offSetInDoc;
	}

	public void setOffSetInDoc(List<OffSetCl> offSetInDoc) {
		this.offSetInDoc = offSetInDoc;
	}

}
