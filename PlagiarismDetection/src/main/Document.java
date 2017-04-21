package main;

import java.util.ArrayList;
import java.util.List;

public class Document {

	String originalDoc;
	List<Sentence> sentencesInDoc = new ArrayList<Sentence>();

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
