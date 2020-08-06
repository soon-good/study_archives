package io.study.erd_example.emp.repository;

import io.study.erd_example.emp.entity.Employee;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class EmpRepository {

	private final EntityManager em;

	public EmpRepository(EntityManager em){
		this.em = em;
	}

	public List<Employee> findEmployeesSalaryByPaging(Double salary, int offset, int limit){
		List employees = em.createQuery("select e from Employee e where e.salary = :salary order by e.username desc")
			.setParameter("salary", salary)
			.setFirstResult(offset)		// 데이터를 어느 페이지에서부터 가져올지를 지정
			.setMaxResults(limit)		// 갯수를 몇개 가져올지를 지정
			.getResultList();

		return employees;
	}

	public long totalCount(Double salary){
		Long count = em
			.createQuery("select count(e) from Employee e where e.salary = :salary", Long.class)
			.setParameter("salary", salary)
			.getSingleResult();

		return count;
	}

	public int bulkSalaryUpdate(Double ratio){
		int cnt = em.createQuery(
					"update Employee e set e.salary = e.salary + e.salary*:ratio"
					)
					.setParameter("ratio", ratio)
					.executeUpdate();

		return cnt;
	}
}
