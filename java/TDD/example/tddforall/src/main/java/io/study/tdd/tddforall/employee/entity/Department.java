package io.study.tdd.tddforall.employee.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
@Entity(name = "DEPARTMENT")
public class Department {

	@Id @GeneratedValue
	@Column(name = "DEPT_NO")
	private Long id;

	@Column(name = "DEPT_NAME")
	private String deptName;

	@OneToMany(mappedBy = "dept")
	private List<Employee> employees = new ArrayList<>();
}
