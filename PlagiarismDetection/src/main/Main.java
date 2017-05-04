package main;

public class Main {

	public static void main(String[] args)  {

		Text text = new Text();
		String pt = "corpus/train-pan11-intrincic-plagiarism-from-PAN2010/suspicious-documents";
		text.extractTextAndGroundTruth(pt);
	}
}

