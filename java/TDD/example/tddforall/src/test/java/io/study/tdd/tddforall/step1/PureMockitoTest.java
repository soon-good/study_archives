package io.study.tdd.tddforall.step1;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PureMockitoTest {

	@Test
	@DisplayName("순수 Mockito 테스트")
	void testListAdd(){
		List mockList = mock(List.class);

		mockList.add("one");
		mockList.add("two");

		verify(mockList).add("one");
		verify(mockList).add("two");
	}
}
