package io.study.erd_example.onetoone_twoway_master_fk.entity;

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
	@DisplayName("1:1 (1대1) 양방항 >>> 주인테이블에 FK를 둘때 ")
	public void testMapping(){

		System.out.println("======= IDCard 객체 생성 =======");
		IDCard idCard = IDCard.builder()
			.manufacturer("삼성")
			.price(1000D)
			.build();

		System.out.println("======= em.persist(idCard) =======");
		em.persist(idCard);

		System.out.println("======= Employee 객체 생성 =======");
		Employee e1 = Employee.builder()
			.idCard(idCard)
			.salary(2000D)
			.username("소방관#1")
			.build();

		System.out.println("======= em.persist(e1) =======");
		em.persist(e1);

	}
}
