package main;

import java.util.ArrayList;
import java.util.List;

public class Document {

	int id;
	int numSentences;
	String mostFrequencyWord;
	String originalDoc;
	List<Sentence> sentencesInDoc = new ArrayList<Sentence>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNumSentences() {
		return numSentences;
	}

	public void setNumSentences(int numSentences) {
		this.numSentences = numSentences;
	}

	public String getMostFrequencyWord() {
		return mostFrequencyWord;
	}

	public void setMostFrequencyWord(String mostFrequencyWord) {
		this.mostFrequencyWord = mostFrequencyWord;
	}

	public String getOriginalDoc() {
		return originalDoc;
	}

	public void setOriginalDoc(String originalDoc) {
		this.originalDoc = originalDoc;
	}

	public List<Sentence> getSentencesInDoc() {
		return sentencesInDoc;
	}

	public void setSentencesInDoc(List<Sentence> sentencesInDoc) {
		this.sentencesInDoc = sentencesInDoc;
	}

}
