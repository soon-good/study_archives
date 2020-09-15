package io.study.tdd.tddforall.step4;

import io.study.tdd.tddforall.step4.dto.EmployeeDto;
import java.util.List;

public class Step4ServiceImpl implements Step4Service {

	private final EmpRawJpaRepository repository;

	public Step4ServiceImpl(EmpRawJpaRepository repository){
		this.repository = repository;
	}

	@Override
	public List<EmployeeDto> findAllEmployee() {
		return repository.findAllEmployee();
	}
}
