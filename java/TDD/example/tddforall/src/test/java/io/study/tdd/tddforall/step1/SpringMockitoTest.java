package io.study.tdd.tddforall.step1;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SpringMockitoTest {

	@Test
	@DisplayName("List Mocking 테스트")
	void testMockList(){
		List mockList = mock(List.class);

		mockList.add("ONE");
		mockList.add("TWO");

		verify(mockList).add("ONE");
		verify(mockList).add("TWO");
	}
}
