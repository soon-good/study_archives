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

	public static GradeLevel gradeLevel(Double score){
		if(score >= 90 && score <= 100){
			return GradeLevel.A;
		}
		else if(score >=80 && score < 90){
			return GradeLevel.B;
		}
		else if(score >=70 && score < 80){
			return GradeLevel.C;
		}
		else if(score >= 60 && score < 70){
			return GradeLevel.D;
		}
		else if(score >= 50 && score < 60){
			return GradeLevel.E;
		}
		else {
			return GradeLevel.F;
		}
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
