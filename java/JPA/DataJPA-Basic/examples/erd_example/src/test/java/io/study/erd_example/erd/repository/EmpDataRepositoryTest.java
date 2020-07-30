package io.study.erd_example.erd.repository;

import static org.assertj.core.api.Assertions.assertThat;

import io.study.erd_example.emp.entity.Department;
import io.study.erd_example.emp.entity.Employee;
import io.study.erd_example.emp.repository.DeptDataRepository;
import io.study.erd_example.emp.repository.EmpDataRepository;
import java.util.List;
import javax.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class EmpDataRepositoryTest {

	@Autowired
	private EntityManager em;

	@Autowired
	private EmpDataRepository empDataRepository;

	@Autowired
	private DeptDataRepository deptDataRepository;

	@BeforeEach
	void insertData(){
		Department police = new Department("POLICE");
		Department firefighter = new Department("FIREFIGHTER");

		deptDataRepository.save(police);
		deptDataRepository.save(firefighter);

		Employee empPolice = new Employee("경찰관1", 1000D, police);
		Employee empFireFighter = new Employee("소방관1", 1000D, firefighter);

		empDataRepository.save(empPolice);
		empDataRepository.save(empFireFighter);

		em.flush();
	}

	@Test
	@DisplayName("모든 사원을 출력해오자")
	void testFindAllEmployee(){
		List<Employee> all = empDataRepository.findAll();
		assertThat(all.size()).isEqualTo(2);
	}
}
