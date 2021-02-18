package io.study.tdd.tddforall.employee.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
@Entity(name = "EMPLOYEE")
public class Employee {

	@Id @GeneratedValue
	@Column(name = "EMP_NO")
	private Long id;

	@Column(name = "EMP_NAME")
	private String username;

	@Column(name = "SALARY")
	private Double salary;

	@ManyToOne
	@JoinColumn(name = "DEPT_NO")
	private Department dept;
}
