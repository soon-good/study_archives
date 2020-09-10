package io.study.erd_example.manytoone_oneway.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
@Entity(name = "DEPARTMENT")
public class Department {

	@Id
	@GeneratedValue
	@Column(name = "DEPT_NO")
	private Long deptNo;

	@Column(name = "DEPT_NAME")
	private String deptName;

//	@OneToMany(mappedBy = "department") 					// "department"는 Employee 엔티티 내의 Department 변수이다.
//	private List<Employee> employees = new ArrayList<>();	// reflection 으로 내부 작업을 하기 때문에 변수 명을 지정해 주어야 한다.

}
