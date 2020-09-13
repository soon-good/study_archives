package io.study.tdd.tddforall.step4.repository;

import io.study.tdd.tddforall.step3.entity.Employee;
import java.util.List;
import javax.persistence.EntityManager;

public class EmpRawJpaRepository {

	private final EntityManager entityManager;

	public EmpRawJpaRepository (EntityManager entityManager){
		this.entityManager = entityManager;
	}

	public List<Employee> findAllEmployee(){
		return entityManager.createQuery("select e from EMPLOYEE e").getResultList();
	}
}
