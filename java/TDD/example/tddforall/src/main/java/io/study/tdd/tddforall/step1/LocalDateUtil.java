package io.study.tdd.tddforall.step;

import java.time.LocalDate;

public class LocalDateUtil {

	public LocalDate currentDate(){
		final LocalDate now = LocalDate.now();
		return now;
	}
}
