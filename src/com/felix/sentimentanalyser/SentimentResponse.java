package com.felix.sentimentanalyser;

public class SentimentResponse {
	private String _polarity = "", _posWords = "", _negWords = "";
	

	public SentimentResponse(String polarity, String posWords, String negWords) {
		super();
		this._polarity = polarity;
		this._posWords = posWords;
		this._negWords = negWords;
	}

	public String getPolarity() {
		return _polarity;
	}

	public void setPolarity(String polarity) {
		this._polarity = polarity;
	}

	public String getPosWords() {
		return _posWords;
	}

	public void setPosWords(String _posWords) {
		this._posWords = _posWords;
	}

	public String getNegWords() {
		return _negWords;
	}

	public void setNegWords(String _negWords) {
		this._negWords = _negWords;
	}

	
}
