package io.study.tdd.tddforall.employee;

import io.study.tdd.tddforall.employee.dto.EmployeeDto;
import java.util.List;

public interface EmpService {

	List<EmployeeDto> findAllEmployee();
}
