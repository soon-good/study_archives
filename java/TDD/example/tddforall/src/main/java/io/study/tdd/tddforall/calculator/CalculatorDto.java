package io.study.tdd.tddforall.calculator;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class CalculatorDto {
	private FxType fxType;	// key (수식 타입)
	private Double value;	// value (수식에 대해 도출된 값)
}
