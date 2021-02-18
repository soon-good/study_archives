package io.study.tdd.tddforall.employee;

import io.study.tdd.tddforall.employee.entity.Employee;
import io.study.tdd.tddforall.employee.dto.EmployeeDto;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class EmpRawJpaRepository {

	private final EntityManager entityManager;

	public EmpRawJpaRepository (EntityManager entityManager){
		this.entityManager = entityManager;
	}

	public List<EmployeeDto> findAllEmployee(){
		List<Employee> employees = entityManager.createQuery("select e from EMPLOYEE e").getResultList();

		return employees
			.stream()
//			.map(EmployeeDto::new)
			.map(employee->{
				EmployeeDto dto = new EmployeeDto();
				dto.setUsername(employee.getUsername());
				dto.setDeptName(employee.getDept().getDeptName());
				dto.setSalary(employee.getSalary());
				return dto;
			})
			.collect(Collectors.toList());
	}
}
