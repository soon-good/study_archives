package io.study.tdd.tddforall.grade;

public enum GradeLevel {
	A("A", 1){},
	B("B", 2){},
	C("C", 3){},
	D("D", 4){},
	E("E", 5){},
	F("F", 6){};

	private String gradeLevelNm;
	private int gradeLevelCode;

	GradeLevel(String gradeLevelNm, int gradeLevelCode){
		this.gradeLevelNm = gradeLevelNm;
		this.gradeLevelCode = gradeLevelCode;
	}

	public String getGradeLevelNm() {
		return gradeLevelNm;
	}

	public void setGradeLevelNm(String gradeLevelNm) {
		this.gradeLevelNm = gradeLevelNm;
	}

	public int getGradeLevelCode() {
		return gradeLevelCode;
	}

	public void setGradeLevelCode(int gradeLevelCode) {
		this.gradeLevelCode = gradeLevelCode;
	}
}
