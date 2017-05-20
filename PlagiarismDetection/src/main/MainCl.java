package main;

public class MainCl {

	public static void main(String[] args) {
//		TextCl text = new TextCl();
//		text.extractTextAndGroundTruth("corpus/2test-pan11/suspicious-docs", true);
//		text.extractTextAndGroundTruth("corpusTest", false);
		ClassifierCl classifier = new ClassifierCl();
		try {
//			classifier.learnClassifier();
			classifier.learnClassifierIncremental();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
