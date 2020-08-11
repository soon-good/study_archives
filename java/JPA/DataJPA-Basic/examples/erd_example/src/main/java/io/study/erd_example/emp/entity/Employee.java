package io.study.erd_example.emp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.study.erd_example.emp.entity.base.ErdBaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


//@Data
@Getter @Setter
@ToString(exclude = "dept")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "EMPLOYEE")
//public class Employee extends JpaBaseEntity{
//public class Employee extends DataJpaBaseEntity{
public class Employee extends ErdBaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long empNo;

	@Column(name = "USERNAME")
	private String username;

	@Column(name = "SALARY")
	private Double salary;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DEPT_NO")
	@JsonIgnore
	private Department dept;

//	public Employee(){}

	public Employee (String username, Double salary, Department dept){
		this.username = username;
		this.salary = salary;
		this.dept = dept;
		dept.getEmployees().add(this);
	}

}
