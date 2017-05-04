package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocumentCl {

	String originalDoc;
	List<Sentence> sentencesInDoc = new ArrayList<Sentence>();
	Map<Integer, Integer> offsetLenghtPla = new HashMap<Integer, Integer>();
	Map<Integer,String> passageLable = new HashMap<Integer, String>();

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
	public Map<Integer, Integer> getOffsetLenghtPla() {
		return offsetLenghtPla;
	}
	public void setOffsetLenghtPla(Map<Integer, Integer> offsetLenghtPla) {
		this.offsetLenghtPla = offsetLenghtPla;
	}
	public Map<Integer,String> getPassageLable() {
		return passageLable;
	}
	public void setPassageLable(Map<Integer,String> passageLable) {
		this.passageLable = passageLable;
	}

	

}
