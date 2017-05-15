package main;

public class MainCl {

	public static void main(String[] args) {
		TextCl text = new TextCl();
		text.extractTextAndGroundTruth("corpus", true);
		// text.extractTextAndGroundTruth("corpusTest", false);
	}
}
