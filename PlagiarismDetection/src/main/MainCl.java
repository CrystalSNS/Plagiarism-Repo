package main;

public class MainCl {

	public static void main(String[] args) {
		TextCl text = new TextCl();
		text.extractTextAndGroundTruth("corpus/1train-pan11/part13", true);
		// text.extractTextAndGroundTruth("corpusTest", false);
	}
}
