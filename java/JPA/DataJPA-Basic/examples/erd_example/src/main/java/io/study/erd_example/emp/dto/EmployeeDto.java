package io.study.erd_example.emp.dto;

import io.study.erd_example.emp.entity.Employee;
import lombok.Data;

@Data
public class EmployeeDto {
	private String username;
	private Double salary;

	public EmployeeDto(){}

	public EmployeeDto(String username, Double salary){
		this.username = username;
		this.salary = salary;
	}

	public EmployeeDto(Employee employee){
		this.username = employee.getUsername();
		this.salary = employee.getSalary();
	}
}
