package io.study.erd_example.onetomany.entity;

import java.util.ArrayList;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
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
	void testMapping(){
		System.out.println("======= Employee 객체 생성 =======");
		Employee e1 = Employee.builder()
			.username("경찰관#1")
			.salary(1000D)
			.build();

		System.out.println("======= em.persist(e1) =======");
		em.persist(e1);

		System.out.println("======= Department 객체 생성 =======");

		Department d1 = Department.builder()
			.deptName("소하1동경찰서")
			.employees(new ArrayList<>())
			.build();							// DEPARTMENT 테이블에 INSERT 하면 되는 내용이다.

		System.out.println("======= Department.employees.add(e1) =======");
		d1.getEmployees().add(e1);				// DEPARTMENT 테이블에 INSERT 될수 있는 내용은 아니다.
												// DEPARTMENT 테이블이 아니라 EMPLOYEE 테이블을 UPDATE 하게 된다.

		System.out.println("======= em.persist(d1) =======");
		em.persist(d1);
	}

	@AfterEach
	void close(){
		em.close();
	}
}
