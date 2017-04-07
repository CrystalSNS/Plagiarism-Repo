package main;

import java.util.HashMap;
import java.util.Map;

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

	public Map<String, Float> normalizePOS(Map<String, Integer> frequencyPos, int numWordsOfSent) {
		Map<String, Float> posNor = new  HashMap<String, Float>() ;
		for (Map.Entry<String, Integer> pos : frequencyPos.entrySet()){
			posNor.put(pos.getKey(), (float) (pos.getValue()/numWordsOfSent));
		}
		return posNor;
	}

}
