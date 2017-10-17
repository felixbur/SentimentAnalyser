package com.felix.sentimentanalyser;

import com.felix.util.StringUtil;

public class SentimentResponse {
	private String polarity = "";
	private String[] posWords, negWords;
	private double[] probabilities;

	public SentimentResponse(String polarity, String posWordsIn, String negWordsIn, int wordCount) {
		super();
		this.polarity = polarity;
		this.posWords = StringUtil.stringToArray(posWordsIn);
		this.negWords = StringUtil.stringToArray(negWordsIn);
		probabilities = calcProbabilites(posWords.length, negWords.length, wordCount);
	}

	double[] calcProbabilites(int posNum, int negNum, int total) {
		double ret[] = new double[3];
		int all = posNum + negNum;
		if (total == 0) {
			ret = new double[] { 0.0, 0.0, 0.0 };
		} else if (all == 0) {
			ret = new double[] { 1.0, 0.0, 0.0 };
		} else if (posNum == negNum) {
			int neutNum = total - all;
			ret[0] = (double) neutNum / total;
			ret[1] = (double) posNum / total;
			ret[2] = (double) negNum / total;
		} else {
			ret[0] = 0.0;
			ret[1] = (double) posNum / all;
			ret[2] = (double) negNum / all;
		}
		return ret;
	}

	public int getPolarityAsInt() {
		if (polarity.compareTo("negative") == 0) {
			return -1;
		} else if (polarity.compareTo("positive") == 0) {
			return 1;
		} else {
			return 0;
		}

	}

	public String getPolarity() {
		return polarity;
	}

	public String[] getPosWords() {
		return posWords;
	}

	public String[] getNegWords() {
		return negWords;
	}

	public double[] getProbabilities() {
		return probabilities;
	}

}
