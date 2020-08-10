package io.study.erd_example.erd.repository;

import io.study.erd_example.emp.entity.Department;
import io.study.erd_example.emp.entity.Employee;
import io.study.erd_example.emp.repository.DeptDataRepository;
import io.study.erd_example.emp.repository.custom.EmpCustomDataRepository;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 정의 리포지터리 테스트
 */
@SpringBootTest
@Transactional
public class EmpCustomDataRepositoryTest {

	@Autowired
	DeptDataRepository deptDataRepository;

	@Autowired
	EmpCustomDataRepository qEmpRepository;

	@Autowired
	EntityManager em;

	@BeforeEach
	void insertData(){
		Department police = new Department("POLICE");
		Department firefighter = new Department("FIREFIGHTER");

		deptDataRepository.save(police);
		deptDataRepository.save(firefighter);

		Employee empPolice1 = new Employee("경찰관1", 1000D, police);
		Employee empPolice2 = new Employee("경찰관2", 1000D, police);
		Employee empPolice3 = new Employee("경찰관3", 1000D, police);
		Employee empPolice4 = new Employee("경찰관4", 1000D, police);
		Employee empPolice5 = new Employee("경찰관5", 1000D, police);
		Employee empFireFighter = new Employee("소방관1", 1000D, firefighter);

		qEmpRepository.save(empPolice1);
		qEmpRepository.save(empPolice2);
		qEmpRepository.save(empPolice3);
		qEmpRepository.save(empPolice4);
		qEmpRepository.save(empPolice5);

		qEmpRepository.save(empFireFighter);

		em.flush();
		em.clear();
	}

	@Test
	@DisplayName("커스텀 리포지터리 테스트")
	void testCustomRepository(){
		List<Employee> employeeCustom = qEmpRepository.selectAllEmployees();

		System.out.println(employeeCustom);
	}
}
