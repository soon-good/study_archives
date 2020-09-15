package io.study.tdd.tddforall.step4;

import io.study.tdd.tddforall.employee.EmpRawJpaRepository;
import io.study.tdd.tddforall.employee.EmpServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Commit
class Step4Test {

	Step4TestLambdaManager testManager = new Step4TestLambdaManager();

	@Mock
	private EmpRawJpaRepository mockRepository;

	@InjectMocks
	private EmpServiceImpl mockService;

	@Autowired
	private EmpRawJpaRepository empRawJpaRepository;

	@Test
	@DisplayName("lambda 를 이용한 테스트 실습 #1")
	void testEmpRawJpaRepository(){
		testManager
//			.findAllEmployee(empRawJpaRepository)
			.findAllEmployee(mockRepository)
			.test(mockService);
	}
}
