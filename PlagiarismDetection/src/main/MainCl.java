package main;

public class MainCl {

	public static void main(String[] args) {
		TextCl text = new TextCl();
//		text.extractTextAndGroundTruth("corpus/1train-pan11/part" + args[0], true);
//		text.extractTextAndGroundTruth("corpus/1train-pan11/part26-" + args[0], true);
//		text.extractTextAndGroundTruth("corpus/2test-pan11/suspisious-docs", true);
//		text.extractTextAndGroundTruth("corpus/2test-pan11/suspicious-docs-"+ args[0] , true);
//		text.extractTextAndGroundTruth("corpus/3train-pan16/suspisious-docs", true);
		text.extractTextAndGroundTruth("corpusTest/3train-pan16/suspisious-docs", false);

//		ClassifierCl classifier = new ClassifierCl();
//		try {
//			classifier.learnClassifier();
//			classifier.learnClassifierIncremental();
			
//			classifier.mergeArff("corpusTrain/test1");
//			classifier.mergeArff("corpusTrain/mergTrainingData");

//			classifier.renameFile("corpusTrain/test");
//			classifier.renameFile("corpusTrain/mergTrainingData");
			
//			classifier.voteClassifier("result/mergedDataInst.arff","result/index.txt");
			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
