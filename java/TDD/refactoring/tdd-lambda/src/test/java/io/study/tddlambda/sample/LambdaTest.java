package io.study.tddlambda.sample;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.study.tddlambda.sample.lambda.ListTestLambdaManager;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LambdaTest {

	@Test
	@DisplayName("pass Mock Object as an Argument in lambda")
	void test_mock_lambda1(){
		List list = mock(List.class);
		list.add("1");

		ListTestLambdaManager
			.listTestLambda()
			.test(list);
	}

	@Test
	@DisplayName("Test 2")
	void test_mock_lambda2(){
		List list = mock(List.class);
		list.add("1");

		ListTestLambdaManager
			.listTestLambda()
			.test(list);

		list.add("2");

		verify(list, times(1)).add("2");
	}
}
