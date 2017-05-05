package main;

public class Main {

	public static void main(String[] args)  {

		Text text = new Text();
		String pt = "corpus/train-pan11-intrincic-plagiarism-from-PAN2010/suspicious-documents";
//		String pt = "corpus/test-pan11-intrinsic-plagiarism";
//		String pt = "corpus/train-pan16-problem-a-plagiarism";
		text.extractTextAndGroundTruth(pt);
	}
}

