package io.study.tdd.tddforall.employee;

import io.study.tdd.tddforall.employee.dto.EmployeeDto;
import java.util.List;

public class EmpServiceImpl implements EmpService {

	private final EmpRawJpaRepository repository;

	public EmpServiceImpl(EmpRawJpaRepository repository){
		this.repository = repository;
	}

	@Override
	public List<EmployeeDto> findAllEmployee() {
		return repository.findAllEmployee();
	}
}
