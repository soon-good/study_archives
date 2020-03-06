package com.study.qdsl.ch03_basic_sql;

import static org.assertj.core.api.Assertions.*;

import com.querydsl.core.QueryResults;
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
 * QueryDsl - Ch03-7. 페이징
 *  : fetchResults(), limit(), offset() 의 기본 사용법을 알아본다.
 *  : 결과적으로는, 구체적인 SQL을 파악해서 최적화하는 것은 내가 직접 해봐야 할 듯 하다.
 */
@SpringBootTest
@Transactional
public class QdslPagingTest {

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
	 * offset() 은 {0, 1, 2, ... i, ... n} 과 같이 페이지 번호가 매겨진다.
	 */
	@Test
	public void paging1(){
		QMember member = QMember.member;
		List<Member> pagedResult = queryFactory.selectFrom(member)
			.orderBy(member.age.desc())
			.offset(1)    // 2 번째 페이지를 가져온다. offset 은 {0,1,2, ... i, ... n} 과 같이 번호가 매겨진다.
			.limit(2)
			.fetch();

		System.out.println("===== paging1 result =====");
		for(Member m : pagedResult){
			System.out.println("name : "+ m.getUsername());
		}

		assertThat(pagedResult.size()).isEqualTo(2);
	}

	@Test
	public void paging2(){
		QMember member = QMember.member;
		QueryResults<Member> queryResults = queryFactory.selectFrom(member)
			.orderBy(member.age.desc())
			.offset(1)
			.limit(2)
			.fetchResults();
		/**
		 * 실무에서는 위와 같이 코드를 쓰지 않는 경우가 많다고 한다.
		 * 카운트 쿼리를 따로 분리해서 써야하는 경우도 있다.
		 *
		 * 페이징 쿼리가 단순하면 위와 같이 쓸수도 있다.
		 * 컨텐츠 쿼리는 복잡한데, 카운트 쿼리는 굉장히 단순하게 되는 경우가 있다.
		 * 이런 경우 쿼리를 두개로 따로 나누어 작성해야 한다.
		 */


		/** 이렇게 getTotal() 과 getResults() 를 호출하면 SQL이 두번 실행된다.
		 * count() SQL 과 contents를 위한 SELECT SQL이 호출된다.
		 **/
		assertThat(queryResults.getTotal()).isEqualTo(10);
		assertThat(queryResults.getLimit()).isEqualTo(2);
		assertThat(queryResults.getOffset()).isEqualTo(1);
		assertThat(queryResults.getResults().size()).isEqualTo(2);
	}
}
