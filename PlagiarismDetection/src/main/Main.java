package main;

public class Main {

	public static void main(String[] args) {

		Sentence sentence = new Sentence();
		Features features = new Features();
		Text texts = new Text();
		for (String doc : texts.splitToParagraphs(texts.readTextFile("corpus/train/12Esample01.txt")).values()) {
			texts.splitToSentences(doc);
//		   System.out.println(sentence);
		}
		

	}

}
