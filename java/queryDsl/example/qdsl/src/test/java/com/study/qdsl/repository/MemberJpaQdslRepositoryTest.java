package com.study.qdsl.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.study.qdsl.dto.MemberTeamDto;
import com.study.qdsl.dto.condition.MemberSearchCondition;
import com.study.qdsl.entity.Member;
import com.study.qdsl.entity.Team;
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

	@Test
	public void searchTest(){
		createSampleData();

		/**
		 * 주의 !!)
		 * 동적쿼리 작성시 아래의 조건문에서 goe, loe,teamName 처럼 조건식을 작성하지 않고 객체만 생성하면
		 *
		 * 컴파일타임에는  에러가 나지 않고
		 * 운영에서도 역시 에러가 나지 않는다.
		 *
		 * 하지만 모든 데이터를 가져오므로 치명적이기도 하고, 화면상에 가져와야 하는 데이터와 맞지 않는 버그 또한 생긴다.
		 */
		MemberSearchCondition condition = new MemberSearchCondition();
		condition.setAgeGoe(24);
		condition.setAgeLoe(27);
		condition.setTeamName("Analysis");

		List<MemberTeamDto> result = qdslRepository.searchByBuilder(condition);

		assertThat(result.get(0).getUsername()).isEqualTo("Stacey");
	}

	@Test
	public void searchTestByWhere(){
		createSampleData();

		/**
		 * 주의 !!)
		 * 동적쿼리 작성시 아래의 조건문에서 goe, loe,teamName 처럼 조건식을 작성하지 않고 객체만 생성하면
		 *
		 * 컴파일타임에는  에러가 나지 않고
		 * 운영에서도 역시 에러가 나지 않는다.
		 *
		 * 하지만 모든 데이터를 가져오므로 치명적이기도 하고, 화면상에 가져와야 하는 데이터와 맞지 않는 버그 또한 생긴다.
		 */
		MemberSearchCondition condition = new MemberSearchCondition();
		condition.setAgeGoe(24);
		condition.setAgeLoe(27);
		condition.setTeamName("Analysis");

		List<MemberTeamDto> result = qdslRepository.searchByWhere(condition);

		assertThat(result.get(0).getUsername()).isEqualTo("Stacey");
	}

	private void createSampleData(){
		Team marketingTeam = new Team("Marketing");
		Team analysisTeam = new Team("Analysis");
		Team musicianTeam = new Team("Musician");
		Team nullTeam = new Team("NullTeam");

		entityManager.persist(marketingTeam);
		entityManager.persist(analysisTeam);
		entityManager.persist(musicianTeam);
		entityManager.persist(nullTeam);

		Member john = new Member("John", 23, marketingTeam);
		Member susan = new Member("Becky", 22, marketingTeam);

		Member kyle = new Member("Kyle", 28, analysisTeam);
		Member stacey = new Member("Stacey", 24, analysisTeam);

		Member aladin = new Member("Aladdin", 35, analysisTeam);
		Member genie = new Member("Genie", 41, analysisTeam);

		Member beethoven = new Member("Beethoven", 251, musicianTeam);
		Member chopin = new Member("Chopin", 210, musicianTeam);
		Member genie2 = new Member("Genie", 210, musicianTeam);
		Member nullName = new Member(null, 100, musicianTeam);

		Member ceo = new Member("Jordan", 49, null);

		entityManager.persist(john);
		entityManager.persist(susan);
		entityManager.persist(kyle);
		entityManager.persist(stacey);
		entityManager.persist(aladin);
		entityManager.persist(genie);

		entityManager.persist(beethoven);
		entityManager.persist(chopin);
		entityManager.persist(genie2);
		entityManager.persist(nullName);
		entityManager.persist(ceo);
	}
}