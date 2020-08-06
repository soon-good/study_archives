package jpabasic.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EMP")
public class Employee {
	@Id
	private Long id;

	@Column(name = "v_name")
	private String name;

	@Column(name = "d_salary")
	private Double salary;

	@Column(name = "d_bonus")
	private Double bonus;

	public Employee(){}

	public Employee(Long id, String name, Double salary, Double bonus){
		this.id = id;
		this.name = name;
		this.salary = salary;
		this.bonus = bonus;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getSalary() {
		return salary;
	}

	public void setSalary(Double salary) {
		this.salary = salary;
	}

	public Double getBonus() {
		return bonus;
	}

	public void setBonus(Double bonus) {
		this.bonus = bonus;
	}
}
