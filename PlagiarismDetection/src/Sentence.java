import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class Sentence {
	
	Array words[];
	int lengthByWords; 
	int lengthByChar; 
	float word_Mean, word_5, word_95;
	float char1_Mean, char1_5, char1_95;
	float char3_Mean, char3_5, char3_95;
	float char4_Mean, char4_5, char4_95;
	Map<String, Float> num_punctuation = new HashMap<String, Float>();
	Map<String, Float> num_POS = new HashMap<String, Float>();
	
	public Array[] getWords() {
		return words;
	}
	public void setWords(Array[] words) {
		this.words = words;
	}
	public int getLengthByWords() {
		return lengthByWords;
	}
	public void setLengthByWords(int lengthByWords) {
		this.lengthByWords = lengthByWords;
	}
	public int getLengthByChar() {
		return lengthByChar;
	}
	public void setLengthByChar(int lengthByChar) {
		this.lengthByChar = lengthByChar;
	}
	public float getWord_Mean() {
		return word_Mean;
	}
	public void setWord_Mean(float word_Mean) {
		this.word_Mean = word_Mean;
	}
	public float getWord_5() {
		return word_5;
	}
	public void setWord_5(float word_5) {
		this.word_5 = word_5;
	}
	public float getWord_95() {
		return word_95;
	}
	public void setWord_95(float word_95) {
		this.word_95 = word_95;
	}
	public float getChar1_Mean() {
		return char1_Mean;
	}
	public void setChar1_Mean(float char1_Mean) {
		this.char1_Mean = char1_Mean;
	}
	public float getChar1_5() {
		return char1_5;
	}
	public void setChar1_5(float char1_5) {
		this.char1_5 = char1_5;
	}
	public float getChar1_95() {
		return char1_95;
	}
	public void setChar1_95(float char1_95) {
		this.char1_95 = char1_95;
	}
	public float getChar3_Mean() {
		return char3_Mean;
	}
	public void setChar3_Mean(float char3_Mean) {
		this.char3_Mean = char3_Mean;
	}
	public float getChar3_5() {
		return char3_5;
	}
	public void setChar3_5(float char3_5) {
		this.char3_5 = char3_5;
	}
	public float getChar3_95() {
		return char3_95;
	}
	public void setChar3_95(float char3_95) {
		this.char3_95 = char3_95;
	}
	public float getChar4_Mean() {
		return char4_Mean;
	}
	public void setChar4_Mean(float char4_Mean) {
		this.char4_Mean = char4_Mean;
	}
	public float getChar4_5() {
		return char4_5;
	}
	public void setChar4_5(float char4_5) {
		this.char4_5 = char4_5;
	}
	public float getChar4_95() {
		return char4_95;
	}
	public void setChar4_95(float char4_95) {
		this.char4_95 = char4_95;
	}
	public Map<String, Float> getNum_punctuation() {
		return num_punctuation;
	}
	public void setNum_punctuation(Map<String, Float> num_punctuation) {
		this.num_punctuation = num_punctuation;
	}
	public Map<String, Float> getNum_POS() {
		return num_POS;
	}
	public void setNum_POS(Map<String, Float> num_POS) {
		this.num_POS = num_POS;
	}
	
	
}
