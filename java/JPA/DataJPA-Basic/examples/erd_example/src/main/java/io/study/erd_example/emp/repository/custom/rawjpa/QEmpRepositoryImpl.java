package io.study.erd_example.emp.repository.custom.rawjpa;

import io.study.erd_example.emp.entity.Employee;
import java.util.List;
import javax.persistence.EntityManager;

public class QEmpRepositoryImpl implements QEmpRepository {

	private EntityManager em;

	public QEmpRepositoryImpl(EntityManager em){
		this.em = em;
	}

	@Override
	public List<Employee> selectAllEmployees() {
		return em.createQuery("select e from Employee e")
			.getResultList();
	}
}
