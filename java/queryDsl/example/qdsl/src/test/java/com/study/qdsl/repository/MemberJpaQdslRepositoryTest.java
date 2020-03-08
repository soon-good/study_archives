package com.study.qdsl.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.study.qdsl.entity.Member;
import java.util.List;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class MemberJpaQdslRepositoryTest {

	@Autowired
	EntityManager entityManager;

	@Autowired
	MemberJpaQdslRepository qdslRepository;

	@Autowired
	MemberJpaJpqlRepository jpqlRepository;

	@Test
	public void basicTest(){
		Member member = new Member("Stacey", 24);
		jpqlRepository.save(member);

		Member foundMember = jpqlRepository.findById(member.getId()).get();
		assertThat(foundMember.getUsername()).isEqualTo("Stacey");
		assertThat(foundMember).isEqualTo(member);

		List<Member> resultList1 = qdslRepository.findAll();
		assertThat(resultList1).containsExactly(member);

		List<Member> resultList2 = qdslRepository.findByUsername("Stacey");
		assertThat(resultList2).containsExactly(member);
	}
}