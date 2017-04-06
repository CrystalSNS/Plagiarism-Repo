package main;

import java.util.HashMap;
import java.util.Map;

public class Document {

	Integer numSentences;
	String mostFrequencyWord;
	Map<Integer, Sentence> sentencesInDoc = new HashMap<Integer, Sentence>();

	public Map<Integer, Sentence> getSentencesInDoc() {
		return sentencesInDoc;
	}

	public void setSentencesInDoc(Map<Integer, Sentence> sentencesInDoc) {
		this.sentencesInDoc = sentencesInDoc;
	}

	public Integer getNumSentences() {
		return numSentences;
	}

	public void setNumSentences(Integer numSentences) {
		this.numSentences = numSentences;
	}

	public String getMostFrequencyWord() {
		return mostFrequencyWord;
	}

	public void setMostFrequencyWord(String mostFrequencyWord) {
		this.mostFrequencyWord = mostFrequencyWord;
	}

}
