package io.study.tdd.tddforall.grade;

import io.study.tdd.tddforall.calculator.CalculatorDto;

public interface GradeService {
	public CalculatorDto getAverageScoreForAllEmployees();

	public GradeLevel getGradeLevelForAllEmployees();
}
