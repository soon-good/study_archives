package io.study.tdd.tddforall.step4;

import io.study.tdd.tddforall.employee.EmpRawJpaRepository;
import io.study.tdd.tddforall.employee.EmpServiceImpl;
import io.study.tdd.tddforall.employee.dto.EmployeeDto;
import java.util.List;
import org.mockito.Mockito;

public class Step4TestLambdaManager {

	public Step4TestLambda<EmpServiceImpl> findAllEmployee ( EmpRawJpaRepository mockRepository ){
		Step4TestLambda<EmpServiceImpl> lambda = (mockService -> {

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
