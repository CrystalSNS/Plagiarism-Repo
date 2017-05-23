package main;

public class MainCl {

	public static void main(String[] args) {
//		TextCl text = new TextCl();
//		text.extractTextAndGroundTruth("corpus/1train-pan11/part" + args[0], true);
//		text.extractTextAndGroundTruth("corpus/1train-pan11/part26-" + args[0], true);
//		text.extractTextAndGroundTruth("corpus/2test-pan11/suspisious-docs", true);
//		text.extractTextAndGroundTruth("corpus/2test-pan11/suspicious-docs-"+ args[0] , true);
//		text.extractTextAndGroundTruth("corpus/3train-pan16/suspisious-docs", true);
//		text.extractTextAndGroundTruth("corpusTest/2test-pan11/suspicious-docs", false);

		ClassifierCl classifier = new ClassifierCl();
		try {
//			classifier.learnClassifier();
//			classifier.learnClassifierIncremental();

//			classifier.voteClassifier("corpusTrain/test");
			classifier.renameFile("corpusTrain/test");
//			classifier.voteClassifier("corpusTrain/mergTrainingData");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
