package io.study.tdd.tddforall.step3.entity;

import static org.assertj.core.api.Assertions.assertThat;

import io.study.tdd.tddforall.employee.entity.Department;
import io.study.tdd.tddforall.employee.entity.Employee;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
@Commit
class EmployeeDepartmentTest {

	@Resource
	private EntityManager em;

	@Test
	@DisplayName("Entity 테스트 (Employee, Department) ")
	void testEmployeeDepartment(){
		Department dept = Department.builder()
			.deptName("종로소방서")
			.employees(new ArrayList<>())
			.build();

		em.persist(dept);

		Employee employee = Employee.builder()
			.username("소방관#1")
			.salary(2000D)
			.dept(dept)
			.build();

		em.persist(employee);

		List<Employee> employees = em
			.createQuery("SELECT e FROM EMPLOYEE e")
			.getResultList();

		assertThat(employees.get(0).getUsername()).isEqualTo(employee.getUsername());
		assertThat(employees.get(0)).isEqualTo(employee);
	}
}
