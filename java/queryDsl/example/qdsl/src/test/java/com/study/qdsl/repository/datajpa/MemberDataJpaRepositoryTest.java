package com.study.qdsl.repository.datajpa;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.study.qdsl.entity.Member;
import com.study.qdsl.entity.Team;
import java.util.List;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
class MemberDataJpaRepositoryTest {

	@Autowired
	EntityManager em;

	@Autowired
	MemberDataJpaRepository repository;

	@Test
	public void sample(){
		Team analysis = new Team("Analysis");
		Member stacey = new Member("Stacey", 30, analysis);

		em.persist(analysis);
		repository.save(stacey);

		Member byId = repository.findById(stacey.getId()).get();
		assertThat(byId.getId()).isEqualTo(stacey.getId());

		List<Member> members = repository.findAll();
		assertThat(members.size()).isEqualTo(1);
		assertThat(members).containsExactly(stacey);

		List<Member> byUsername = repository.findByUsername(stacey.getUsername());
		assertThat(byUsername).containsExactly(stacey);
	}
}