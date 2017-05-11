package main;

public class Main {

	public static void main(String[] args)  {
		Text text = new Text();
		text.extractTextAndGroundTruth("corpus", true);
//		text.extractTextAndGroundTruth("corpusTest", false);
	}
}

