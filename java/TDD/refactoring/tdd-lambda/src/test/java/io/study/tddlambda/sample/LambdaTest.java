package io.study.tddlambda.sample;

import static org.mockito.Mockito.mock;

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
	void test_mock_lambda(){
		List list = mock(List.class);

		ListTestLambdaManager
			.listTestLambda()
			.test(list);
	}
}
