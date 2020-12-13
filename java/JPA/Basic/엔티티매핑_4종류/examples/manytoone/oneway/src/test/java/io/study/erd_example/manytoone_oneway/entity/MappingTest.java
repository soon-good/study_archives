package io.study.erd_example.manytoone_oneway.entity;

import javax.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Commit
public class MappingTest {

	@Autowired
	private EntityManager em;

	@Test
	@DisplayName("N:1(다대일) 단방향 매핑 테스트 >>> ")
	void testMapping(){
		System.out.println("======= Department 객체 생성 =======");
		Department d1 = Department.builder()
			.deptName("소방서#1")
			.build();

		System.out.println("======= em.persist(department) =======");
		em.persist(d1);

		System.out.println("======= Employee 객체 생성 =======");
		Employee e1 = Employee.builder()
			.salary(2000D)
			.username("소방관#1")
			.department(d1)
			.build();

		System.out.println("======= em.persist(employee) =======");
		em.persist(e1);
	}

}
