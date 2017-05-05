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
	List<Sentence> sentencesInDoc = new ArrayList<Sentence>();
	Map<Integer, Integer> offsetLenghtPla = new HashMap<Integer, Integer>();
	Map<Integer, String> passageLable = new HashMap<Integer, String>();
	Map<String, Integer> wordFrequenInDoc = new HashMap<String, Integer>();
	Map<String, Integer> char1FrequenInDoc = new HashMap<String, Integer>();
	Map<String, Integer> char3FrequenInDoc = new HashMap<String, Integer>();
	Map<String, Integer> char4FrequenInDoc = new HashMap<String, Integer>();

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

	public List<Sentence> getSentencesInDoc() {
		return sentencesInDoc;
	}

	public void setSentencesInDoc(List<Sentence> sentencesInDoc) {
		this.sentencesInDoc = sentencesInDoc;
	}

	public Map<Integer, Integer> getOffsetLenghtPla() {
		return offsetLenghtPla;
	}

	public void setOffsetLenghtPla(Map<Integer, Integer> offsetLenghtPla) {
		this.offsetLenghtPla = offsetLenghtPla;
	}

	public Map<Integer, String> getPassageLable() {
		return passageLable;
	}

	public void setPassageLable(Map<Integer, String> passageLable) {
		this.passageLable = passageLable;
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

}
