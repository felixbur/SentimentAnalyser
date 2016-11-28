package com.felix.sentimentanalyser;

public class GateResponse {
	private String answerType = "", sparql = "";
	

	public GateResponse(String _answerType, String _sparql) {
		super();
		this.answerType = _answerType;
		this.sparql = _sparql;
	}

	public String get_answerType() {
		return answerType;
	}

	public void set_answerType(String _answerType) {
		this.answerType = _answerType;
	}

	public String get_sparql() {
		return sparql;
	}

	public void set_sparql(String _sparql) {
		this.sparql = _sparql;
	}
	
}
