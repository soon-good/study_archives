package io.study.tdd.tddforall.step4.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.study.tdd.tddforall.entity.Employee;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class EmployeeDto {

	@JsonProperty(value = "emp_name")
	private String username;

	@JsonProperty(value = "salary")
	private Double salary;

	@JsonProperty(value = "dept_name")
	private String deptName;

	public EmployeeDto(){}

	public EmployeeDto(Employee employee){
		this.username = employee.getUsername();
		this.deptName = employee.getDept().getDeptName();
		this.salary = employee.getSalary();
	}
}
