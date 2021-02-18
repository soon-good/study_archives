package io.study.tdd.tddforall.step1;

import static org.assertj.core.api.Assertions.assertThat;

import io.study.tdd.tddforall.util.timezone.LocalDateUtil;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LocalDateUtilTest {

	@Test
	@DisplayName("LocalDate.now() Value 동일성 테스트 ")
	void testLocalDateNow(){
		LocalDate date = LocalDateUtil.currentDate();

		int month = date.getMonth().getValue();
		assertThat(month).isEqualTo(LocalDate.now().getMonth().getValue());
	}

	@Test
	@DisplayName("LocalDate.now() 객체 동일성 테스트")
	void testLocalDateNowReference(){
		LocalDate date = LocalDateUtil.currentDate();
		assertThat(date).isEqualTo(LocalDate.now());
	}
}
