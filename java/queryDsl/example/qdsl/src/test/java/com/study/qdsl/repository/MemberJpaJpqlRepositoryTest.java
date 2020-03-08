package com.study.qdsl.repository;

import static org.assertj.core.api.Assertions.*;

import com.study.qdsl.entity.Member;
import java.util.List;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ch05 - item1 :: 순수 JPA 리포지터리와 QueryDsl
 *  : JPQL 을 이용해 JpaRepository 를 구현했을 때
 */
@SpringBootTest
@Transactional
class MemberJpaJpqlRepositoryTest {

	@Autowired
	EntityManager em;

	@Autowired
	MemberJpaJpqlRepository repository;

	@Test
	public void basicTest(){
		Member member = new Member("Stacey", 24);
		repository.save(member);

		Member foundMember = repository.findById(member.getId()).get();
		assertThat(foundMember.getUsername()).isEqualTo("Stacey");
		assertThat(foundMember).isEqualTo(member);

		List<Member> resultList1 = repository.findAll();
		assertThat(resultList1).containsExactly(member);

		List<Member> resultList2 = repository.findByUsername("Stacey");
		assertThat(resultList2).containsExactly(member);
	}
}