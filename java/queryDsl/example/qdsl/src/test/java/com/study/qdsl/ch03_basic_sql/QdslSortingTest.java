package com.study.qdsl.ch03_basic_sql;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.qdsl.entity.Member;
import com.study.qdsl.entity.QMember;
import com.study.qdsl.entity.Team;
import java.util.List;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * QueryDsl - Ch03-6. 정렬시 사용하는 메서드들
 *  orderBy() 내에 들어갈 수 있는 조건식 메서드로는
 * 	- asc()  		: QType 엔티티의 멤버필드가 가지는 메서드이다.
 * 	- desc() 		: QType 엔티티의 멤버필드가 가지는 메서드이다.
 * 	- nullsLast()	: QType 엔티티의 멤버필드가 가지는 asc(), desc() 뒤에 옵션으로 붙일 수 있는 메서드이다.(체이닝 활용)
 * 	- nullsFirst()	: QType 엔티티의 멤버필드가 가지는 asc(), desc() 뒤에 옵션으로 붙일 수 있는 메서드이다.(체이닝 활용)
 * 	가 있다.
 */
@SpringBootTest
@Transactional
public class QdslSortingTest {

	@Autowired
	EntityManager em;

	JPAQueryFactory queryFactory;

	@BeforeEach
	public void before(){
		queryFactory = new JPAQueryFactory(em);

		Team marketingTeam = new Team("Marketing");
		Team analysisTeam = new Team("Analysis");
		Team musicianTeam = new Team("Musician");

		em.persist(marketingTeam);
		em.persist(analysisTeam);
		em.persist(musicianTeam);

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

		em.persist(john);
		em.persist(susan);
		em.persist(kyle);
		em.persist(stacey);
		em.persist(aladin);
		em.persist(genie);

		em.persist(beethoven);
		em.persist(chopin);
		em.persist(genie2);
		em.persist(nullName);
	}

	/**
	 * 모든 회원들을 조회하는데
	 * 	- 나이 순으로 내림차순 (DESC)
	 * 	- 이름 순으로 오름차순 (ASC)
	 * 	- 이름 순으로 오름차순 적용시 회원 이름이 없을 경우 마지막에 출력하도록 한다. (nulls last)
	 */
	@Test
	public void sort(){
		final QMember member = QMember.member;

		List<Member> sortResult = queryFactory
			.selectFrom(member)
			.where(member.age.goe(100))
			.orderBy(
				member.age.desc(),
				member.username.asc()
					.nullsLast()
			)
			.fetch();

		/** 예상 결과)
		 * 		베토벤 -> 쇼팽 -> 지니 -> null
		 **/
		Member beethoven = sortResult.get(0);
		Member chopin = sortResult.get(1);
		Member genie = sortResult.get(2);
		Member nullEntity = sortResult.get(3);

		assertThat(beethoven.getUsername()).isEqualTo("Beethoven");
		assertThat(chopin.getUsername()).isEqualTo("Chopin");
		assertThat(genie.getUsername()).isEqualTo("Genie");
		assertThat(nullEntity.getUsername()).isNull();
	}
}
