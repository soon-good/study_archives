package io.study.tdd.tddforall.calculator;

public enum FxType {
	AVG("평균", 1){},
	SUM("총합", 2){},
	MAX("최대", 3){},
	MIN("최소", 4){};

	private String fxTypeNm;
	private int fxTypeCode;

	FxType(String fxTypeNm, int fxTypeCode){
		this.fxTypeNm = fxTypeNm;
		this.fxTypeCode = fxTypeCode;
	}

	public String getFxTypeNm() {
		return fxTypeNm;
	}

	public void setFxTypeNm(String fxTypeNm) {
		this.fxTypeNm = fxTypeNm;
	}

	public int getFxTypeCode() {
		return fxTypeCode;
	}

	public void setFxTypeCode(int fxTypeCode) {
		this.fxTypeCode = fxTypeCode;
	}
}
