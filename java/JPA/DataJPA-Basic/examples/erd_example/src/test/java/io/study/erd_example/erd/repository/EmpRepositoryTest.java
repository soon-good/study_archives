package io.study.erd_example.erd.repository;

import static org.assertj.core.api.Assertions.assertThat;

import io.study.erd_example.emp.entity.Department;
import io.study.erd_example.emp.entity.Employee;
import io.study.erd_example.emp.repository.DeptDataRepository;
import io.study.erd_example.emp.repository.EmpDataRepository;
import io.study.erd_example.emp.repository.EmpRepository;
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
public class EmpRepositoryTest {

	@Autowired
	private EntityManager em;

	@Autowired
	private EmpRepository empRepository;

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

		Employee empPolice1 = new Employee("경찰관1", 1000D, police);
		Employee empPolice2 = new Employee("경찰관2", 1000D, police);
		Employee empPolice3 = new Employee("경찰관3", 1000D, police);
		Employee empPolice4 = new Employee("경찰관4", 1000D, police);
		Employee empPolice5 = new Employee("경찰관5", 1000D, police);
		Employee empFireFighter = new Employee("소방관1", 1000D, firefighter);

		empDataRepository.save(empPolice1);
		empDataRepository.save(empPolice2);
		empDataRepository.save(empPolice3);
		empDataRepository.save(empPolice4);
		empDataRepository.save(empPolice5);

		empDataRepository.save(empFireFighter);

		em.flush();
		em.clear();
	}

	@Test
	@DisplayName("페이징 & total count #1")
	public void testPagingAndTotalCount1(){
		final Double paramSalary = 1000D;
		int offset = 2;
		int limit = 2;

		// 디버깅 콘솔에서 MySQL의 limit 구문으로 limit 2,2; 가 나오는 것을 확인 가능하다.
		List<Employee> result = empRepository
			.findEmployeesSalaryByPaging(paramSalary, offset, limit);

		long cnt = empRepository.totalCount(paramSalary);

		assertThat(result.size()).isEqualTo(2);
		assertThat(cnt).isEqualTo(6);
	}

	@Test
	@DisplayName("페이징 & total count #2")
	public void testPagingAndTotalCount2(){
		final Double paramSalary = 1000D;
		int offset = 0;
		int limit = 2;

		// 디버깅 콘솔에서 MySQL의 limit 구문으로 limit 2; 가 나오는 것을 확인 가능하다.
		// (offset 이 0 이기 때문)
		List<Employee> result = empRepository
			.findEmployeesSalaryByPaging(paramSalary, offset, limit);

		long cnt = empRepository.totalCount(paramSalary);

		assertThat(result.size()).isEqualTo(limit);
		assertThat(cnt).isEqualTo(6);
	}

	@Test
	@DisplayName("페이징 & total count #3")
	public void testPagingAndTotalCount3(){
		final Double paramSalary = 1000D;

		long count = empRepository.totalCount(paramSalary);
		assertThat(count).isEqualTo(6);
	}

	@Test
	@DisplayName("bulk update salary - 연봉을 20% 인상")
	public void testBulkSalaryUpdate(){
		int i = empRepository.bulkSalaryUpdate(0.2D);
		assertThat(i).isEqualTo(6);
	}
}
