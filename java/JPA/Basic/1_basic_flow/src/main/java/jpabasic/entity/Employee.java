package jpabasic.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "EMP")
@Getter @Setter
public class Employee {
	@Id
	private Long id;

	@Column(name = "v_name")
	private String name;

	@Column(name = "d_salary")
	private Double salary;

	@Column(name = "d_bonus")
	private Double bonus;

	@OneToMany(mappedBy = "employee")
	private List<EmployeeDevice> employeeDevices = new ArrayList<EmployeeDevice>();

	public Employee(){}

	public Employee(Long id, String name, Double salary, Double bonus){
		this.id = id;
		this.name = name;
		this.salary = salary;
		this.bonus = bonus;
	}
}
