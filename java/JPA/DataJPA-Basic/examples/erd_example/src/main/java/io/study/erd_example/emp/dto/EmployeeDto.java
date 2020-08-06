package io.study.erd_example.emp.dto;

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
}
