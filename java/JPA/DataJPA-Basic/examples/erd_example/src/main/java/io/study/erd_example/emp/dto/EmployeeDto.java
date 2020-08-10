package io.study.erd_example.emp.dto;

import io.study.erd_example.emp.entity.Employee;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EmployeeDto {
	private String username;
	private Double salary;

	public EmployeeDto(String username, Double salary){
		this.username = username;
		this.salary = salary;
	}

	public EmployeeDto(Employee employee){
		this.username = employee.getUsername();
		this.salary = employee.getSalary();
	}
}
