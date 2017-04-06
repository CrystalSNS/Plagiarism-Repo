package main;

public class Features {

	public float findRelationalFrequencyOfWord(Integer numMostWordDoc, Integer numWordDoc, Integer numWordSent) {

		return (float) Math.log(numMostWordDoc / (numWordDoc - numWordSent + 1));
	}

	public float findMean(float[] setOfValue) {
		double sum = 0;
		for (int i = 0; i < setOfValue.length; i++) {
			sum += setOfValue[i];
		}
		return (float) (sum / setOfValue.length);
	}

	public float find5Percent(float[] setOfValue) {
		double sum = 0;
		for (int i = 0; i < setOfValue.length; i++) {
			sum += setOfValue[i];
		}
		return (float) (sum * 5 / 100);
	}

	public float find95Percent(float[] setOfValue) {
		double sum = 0;
		for (int i = 0; i < setOfValue.length; i++) {
			sum += setOfValue[i];
		}
		return (float) (sum * 95 / 100);
	}

	public float normalizePunctuation(int numPunctuation, int numWordsOfSent) {
		return numPunctuation / numWordsOfSent;
	}

	public float normalizePOS(int numPOS, int numWordsOfSent) {
		return numPOS / numWordsOfSent;
	}

}
