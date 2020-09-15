package io.study.tdd.tddforall.step4;

import io.study.tdd.tddforall.step4.dto.EmployeeDto;
import java.util.List;
import org.mockito.Mockito;

public class Step4TestLambdaManager {

	public Step4TestLambda<Step4ServiceImpl> findAllEmployee ( EmpRawJpaRepository mockRepository ){
		Step4TestLambda<Step4ServiceImpl> lambda = (mockService -> {

			List<EmployeeDto> allEmployee = mockService.findAllEmployee();

			Mockito.verify(mockRepository, Mockito.times(1)).findAllEmployee();

		});

		return lambda;
	}

//	public Step4TestLambda<EmpRawJpaRepository> findAllEmployeeRepository(){
//		Step4TestLambda<EmpRawJpaRepository> lambda = (mock -> {
//
//		});
//	}
}
