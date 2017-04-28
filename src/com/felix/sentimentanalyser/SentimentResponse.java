package com.felix.sentimentanalyser;

import com.felix.util.StringUtil;

public class SentimentResponse {
	private String _polarity = "";
	private String[] _posWords, _negWords;
	private double[] _probabilities;

	public SentimentResponse(String polarity, String posWordsIn, String negWordsIn, int wordCount) {
		super();
		this._polarity = polarity;
		this._posWords = StringUtil.stringToArray(posWordsIn);
		this._negWords = StringUtil.stringToArray(negWordsIn);
		_probabilities = calcProbabilites(_posWords.length, _negWords.length, wordCount);
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
		if (_polarity.compareTo("negative") == 0) {
			return -1;
		} else if (_polarity.compareTo("positve") == 0) {
			return 1;
		} else {
			return 0;
		}

	}

	public String getPolarity() {
		return _polarity;
	}

	public String[] getPosWords() {
		return _posWords;
	}

	public String[] getNegWords() {
		return _negWords;
	}

	public double[] getProbabilities() {
		return _probabilities;
	}

}
