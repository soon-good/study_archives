package io.study.erd_example.erd.repository;

import static org.assertj.core.api.Assertions.assertThat;

import io.study.erd_example.emp.dto.EmployeeDto;
import io.study.erd_example.emp.entity.Department;
import io.study.erd_example.emp.entity.Employee;
import io.study.erd_example.emp.repository.DeptDataRepository;
import io.study.erd_example.emp.repository.EmpDataRepository;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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

	private Department police;

	@BeforeEach
	void insertData(){
		police = new Department("POLICE");
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
	@DisplayName("모든 사원을 출력해오자")
	void testFindAllEmployee(){
		List<Employee> all = empDataRepository.findAll();
		assertThat(all.size()).isEqualTo(2);

		all.stream().forEach(e -> {
			System.out.println(e);
		});
	}

	@Test
	@DisplayName("EntityGraph 예제 #1")
	void testEntityGraph1(){

		// 테스트를 위해 1차 캐시의 내용을 모두 비워놓는 과정
		// 로직에서 해당 Entity의 데이터가 필요할 때에만 호출하는 지(지연로딩이 되는 지)를 확인해보기 위해 추가
		em.clear();

		List<Employee> all = empDataRepository.findAll();

		all.stream().forEach(e->{
			System.out.println("employee >>> " + e.getUsername());
			System.out.println("------- ------- ------- ------- -------");

			System.out.println("------- ------- ------- ------- -------");
			System.out.println("employee.team getName >>> " + e.getDept().getDeptName());
			System.out.println("------- ------- ------- ------- -------");
			System.out.println("employee.team 의 클래스 시그내처 >>> " + e.getDept().getClass());

//			ex)
//				class io.study.erd_example.emp.entity.Department$HibernateProxy$WiYnMgOA 와 같은 모양이 찍힌다.
//				마치 HibernateProxy라는 inner class 가 추가된 것 같은 모양이다.
		});
	}

	@Test
	@DisplayName("EntityGraph 예제 #2 - FetchJoin")
	void testEntityGraph2(){
		em.clear();

		List<Employee> all = empDataRepository.findAllFetchJoin();
		all.stream().forEach(e->{
			System.out.println("employee >>> " + e.getUsername());
			System.out.println("------- ------- ------- ------- -------");

			System.out.println("------- ------- ------- ------- -------");
			System.out.println("employee.team getName >>> " + e.getDept().getDeptName());
			System.out.println("------- ------- ------- ------- -------");
			System.out.println("employee.team 의 클래스 시그내처 >>> " + e.getDept().getClass());
		});
	}

	@Test
	@DisplayName("EntityGraph 예제 #3 - FetchJoin")
	void testEntityGraph3(){
		em.clear();

		List<Employee> all = empDataRepository.findAllEntityGraph();
		all.stream().forEach(e->{
			System.out.println("employee >>> " + e.getUsername());
			System.out.println("------- ------- ------- ------- -------");

			System.out.println("------- ------- ------- ------- -------");
			System.out.println("employee.team getName >>> " + e.getDept().getDeptName());
			System.out.println("------- ------- ------- ------- -------");
			System.out.println("employee.team 의 클래스 시그내처 >>> " + e.getDept().getClass());
		});
	}

	@Test
	@DisplayName("JPA Hint 예제 #1")
	void testQueryHint1(){
		List<Employee> all = empDataRepository.findAll();
		Employee employee = all.get(1);
		employee.setUsername("나는 그대의 소방관 ~ ");
		em.flush();
	}

	@Test
	@DisplayName("JPA Hint 예제 #2")
	void testQueryHint2(){
		Employee police1 = empDataRepository.findReadOnlyByUsername("경찰관1");
		police1.setUsername("나는 그대의 소방관 ~ ");
		em.flush();
	}

	@Test
	@DisplayName("JPA Lock #1")
	void testLock1(){
		List<Employee> police1 = empDataRepository.findLockByUsername("경찰관1");
	}

	@Test
	@DisplayName("Spring Data JPA 페이징 #1")
	void testDataJpaPaging1(){
		// 페이지 인덱스가 0 부터 시작한다.
		// 스프링 Data JPA는 페이지를 1부터 시작하는 것이 아니라 0부터 시작한다.
		PageRequest pageRequest =
			PageRequest.of(0, 2, Sort.by(Direction.DESC, "username"));// 0 페이지에서 2개 들고와~

		// PageRequest 의 최상위 부모 클래스는 Pageable 이므로 PageRequest 인스턴스를 전달해주어도 된다.
		// (PageRequest 는 Pageable 의 구현체이다.)
		Page<Employee> bySalary = empDataRepository.findBySalary(1000D, pageRequest);

		List<Employee> content = bySalary.getContent();
		long totalElements = bySalary.getTotalElements();

		content.stream().forEach(e->{
			System.out.println("employee :: " + e);
		});

		assertThat(content.size()).isEqualTo(2);
		assertThat(bySalary.getTotalElements()).isEqualTo(6);

		// page 번호를 가져올때는 getNumber() 메서드를 사용한다.
		assertThat(bySalary.getNumber()).isEqualTo(0);

		// 전체 페이지 갯수는 ? 6/2 = 3
		assertThat(bySalary.getTotalPages()).isEqualTo(3);

		// 첫번째 페이지가 맞는지?
		assertThat(bySalary.isFirst()).isTrue();

		// 현재 페이지의 다음 페이지가 있는지??
		assertThat(bySalary.hasNext()).isTrue();
	}

	@Test
	@DisplayName("Spring Data JPA 페이징 #2 - Slice 사용해보기")
	void testDataJpaPaging2(){
		// 페이지 인덱스가 0 부터 시작한다.
		// 스프링 Data JPA는 페이지를 1부터 시작하는 것이 아니라 0부터 시작한다.
		PageRequest pageRequest =
			PageRequest.of(0, 2, Sort.by(Direction.DESC, "username"));// 0 페이지에서 2개 들고와~

		// PageRequest 의 최상위 부모 클래스는 Pageable 이므로 PageRequest 인스턴스를 전달해주어도 된다.
		// (PageRequest 는 Pageable 의 구현체이다.)
		Slice<Employee> bySalary = empDataRepository.findSliceBySalary(1000D, pageRequest);

		List<Employee> content = bySalary.getContent();

//		Slice 사용시 totalCount 쿼리를 날리지 않기 때문에 count 에 관련된 메서드가 없다.
//		long totalElements = bySalary.getTotalElements();

		content.stream().forEach(e->{
			System.out.println("employee :: " + e);
		});

		assertThat(content.size()).isEqualTo(2);

//		Slice 사용시 totalCount 쿼리를 날리지 않기 때문에 count 에 관련된 메서드가 없다.
//		assertThat(bySalary.getTotalElements()).isEqualTo(6);

		// page 번호를 가져올때는 getNumber() 메서드를 사용한다.
		assertThat(bySalary.getNumber()).isEqualTo(0);

//		Slice 사용시 totalCount 쿼리를 날리지 않기 때문에 count 에 관련된 메서드가 없다.
		// 전체 페이지 갯수는 ? 6/2 = 3
//		assertThat(bySalary.getTotalPages()).isEqualTo(3);

		// 첫번째 페이지가 맞는지?
		assertThat(bySalary.isFirst()).isTrue();

		// 현재 페이지의 다음 페이지가 있는지??
		assertThat(bySalary.hasNext()).isTrue();
	}

	@Test
	@DisplayName("Spring Data JPA 페이징 #3 - Limit 만 걸어보자.")
	void testDataJpaPaging3(){

		// 페이지 인덱스가 0 부터 시작한다.
		// 스프링 Data JPA는 페이지를 1부터 시작하는 것이 아니라 0부터 시작한다.
		PageRequest pageRequest =
			PageRequest.of(0, 2, Sort.by(Direction.DESC, "username"));// 0 페이지에서 2개 들고와~

		List<Employee> bySalary = empDataRepository.findLimitBySalary(1000D, pageRequest);

		bySalary.stream().forEach(e->{
			System.out.println(e);
		});
	}

	@Test
	@DisplayName("Spring Data JPA 페이징 #4 - 조인이 걸려있을 때, 조인한 데이터 모두에 쿼리를 날릴경우는 ? ")
	void testDataJpaPaging4(){

		// 페이지 인덱스가 0 부터 시작한다.
		// 스프링 Data JPA는 페이지를 1부터 시작하는 것이 아니라 0부터 시작한다.
		PageRequest pageRequest =
			PageRequest.of(0, 2, Sort.by(Direction.DESC, "username"));// 0 페이지에서 2개 들고와~

		Page<Employee> bySalary = empDataRepository.findAllCountJoinBySalary(1000D, pageRequest);

		bySalary.stream().forEach(e->{
			System.out.println(e);
		});
	}

	@Test
	@DisplayName("Spring Data JPA 페이징 #5 - 조인이 걸려있을 때, 카운트 쿼리는 필요한 테이블에만 하도록 수정")
	void testDataJpaPaging5(){

		// 페이지 인덱스가 0 부터 시작한다.
		// 스프링 Data JPA는 페이지를 1부터 시작하는 것이 아니라 0부터 시작한다.
		PageRequest pageRequest =
			PageRequest.of(0, 2, Sort.by(Direction.DESC, "username"));// 0 페이지에서 2개 들고와~

		Page<Employee> bySalary = empDataRepository.findSpecificCountJoinBySalary(1000D, pageRequest);

		bySalary.stream().forEach(e->{
			System.out.println(e);
		});
	}

	@Test
	@DisplayName("Spring Data JPA 페이징 #6 - TopN 쿼리")
	void testTopNQuery(){
		List<Employee> top3 = empDataRepository.findTop3BySalary(1000D);

		top3.stream().forEach(e->{
			System.out.println("employee :: " + e);
		});
	}

	@Test
	@DisplayName("Spring Data JPA 페이징 #7 - map으로 Dto 반환하기")
	void testReturnDtoByMapFn(){
		PageRequest pageRequest =
			PageRequest.of(0, 2, Sort.by(Direction.DESC, "username"));// 0 페이지에서 2개 들고와~

		Page<Employee> bySalary = empDataRepository.findSpecificCountJoinBySalary(1000D, pageRequest);

		Page<EmployeeDto> dto = bySalary.map(e -> {
			return new EmployeeDto(e.getUsername(), e.getSalary());
		});
	}

	@Test
	@DisplayName("bulk update salary - 연봉을 20% 인상")
	void testBulkSalaryUpdate(){
		int i = empDataRepository.bulkSalaryUpdate(0.2D);
		System.out.println("======= em.flush(), em.clear() 실행 전 경찰관5 의 데이터 =======");
		Employee emp_before = empDataRepository.findReadOnlyByUsername("경찰관5");
		System.out.println("경찰관 5의 연봉 >>> " + emp_before.getSalary());

		em.flush();
		em.clear();

		System.out.println("======= em.flush(), em.clear() 실행 후 경찰관5 의 데이터 =======");
		Employee emp_after = empDataRepository.findAll().get(5);
		System.out.println("경찰관 5의 연봉 >>> " + emp_after.getSalary());

		assertThat(i).isEqualTo(6);
	}

	@Test
	@DisplayName("Data JPA 에서의 Auditing #1")
	void testDataJpaAuditing1() throws Exception {
		Employee employee = new Employee("경찰관 #99", 1000D, police);
		empDataRepository.save(employee);

		Thread.sleep(1000);
		employee.setUsername("경찰관 #100");

		em.flush();
		em.clear();

		Employee e = empDataRepository.findById(employee.getEmpNo()).get();
		System.out.println("createDate  :: " + e.getCreatedDate());
		System.out.println("updatedDate :: " + e.getLastModifiedDate());
	}

	@Test
	@DisplayName("Data JPA 에서의 Auditing #2")
	void testDataJpaAuditing2() throws Exception {
		Employee employee = new Employee("경찰관 #99", 1000D, police);
		empDataRepository.save(employee);

		Thread.sleep(1000);
		employee.setUsername("경찰관 #100");

		em.flush();
		em.clear();

		Employee e = empDataRepository.findById(employee.getEmpNo()).get();
		System.out.println("createDate  :: " + e.getCreatedDate());
		System.out.println("updatedDate :: " + e.getLastModifiedDate());
		System.out.println("createdBy :: " + e.getCreatedBy());
		System.out.println("updatedBy :: " + e.getLastModifiedBy());
	}
}
