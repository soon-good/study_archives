package com.study.qdsl.ch03_basic_sql;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.qdsl.entity.Member;
import com.study.qdsl.entity.QMember;
import com.study.qdsl.entity.Team;
import java.util.List;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * QueryDsl - Ch03-5. 검색조건 활용
 *  : fetch시에 굉장히 다양한 종류 fetchXXX()를 사용하는데 이에 대해 정리
 *  : fetch(), fetchOne(), fetchFirst(), fetchResults(), fetchCount()
 */
@SpringBootTest
@Transactional
public class QdslSelectResult {

	@Autowired
	EntityManager em;

	JPAQueryFactory queryFactory;

	@BeforeEach
	public void before(){
		queryFactory = new JPAQueryFactory(em);

		Team marketingTeam = new Team("Marketing");
		Team analysisTeam = new Team("Analysis");

		em.persist(marketingTeam);
		em.persist(analysisTeam);

		Member john = new Member("John", 23, marketingTeam);
		Member susan = new Member("Becky", 22, marketingTeam);

		Member kyle = new Member("Kyle", 28, analysisTeam);
		Member stacey = new Member("Stacey", 24, analysisTeam);

		Member aladin = new Member("Aladdin", 35, analysisTeam);
		Member genie = new Member("Genie", 41, analysisTeam);

		em.persist(john);
		em.persist(susan);
		em.persist(kyle);
		em.persist(stacey);
		em.persist(aladin);
		em.persist(genie);
	}

	/**
	 * fetch()
	 * 		리스트 조회
	 * 		데이터가 없을 경우 비어있는 리스트를 반환한다.
	 *
	 * fetchOne()
	 * 		단건 조회
	 * 		결과가 없을 경우
	 * 			null
	 * 		결과가 둘 이상일 경우
	 * 			com.querydsl.core.NonUniqueResultException
	 *
	 * fetchFirst()
	 * 		limit(1).fetchOne()
	 *
	 * fetchResults()
	 * 		페이징 정보 포함
	 * 		total count 쿼리 추가 실행
	 *
	 * fetchCount()
	 * 		count 쿼리로 변경해서 count 수 조회
	 */

	/** 리스트 조회 */
	@Test
	public void fetch(){
		final QMember member = QMember.member;
		List<Member> members = queryFactory.selectFrom(member).fetch();
	}

	/** 단건 조회 */
	@Test
	public void fetchOne(){
		final QMember member = QMember.member;
		Member fetchOne = queryFactory.selectFrom(member).fetchOne();
	}

	/**
	 * 바로 다음 테스트 함수인 fetchFirstAlias() 의 SQL 쿼리와 비교해보자
	 */
	@Test
	public void fetchFirst(){
		final QMember member = QMember.member;
		Member fetchFirst = queryFactory.selectFrom(member).fetchFirst();
	}

	@Test
	public void fetchFirstAlias(){
		final QMember member = QMember.member;
		/** fetchFirst() 의 SQL은 limit(1).fetchOne() 한 결과와 같다. */
		Member fetchFirstAlias = queryFactory.selectFrom(member).limit(1).fetchOne();
	}

	/**
	 * fetchResult()
	 * 		페이징을 포함하는 쿼리이다.
	 *
	 * 	fetchResult()의 경우 페이징 쿼리가 복잡해지면,
	 * 	데이터(컨텐츠)를 가져오는 쿼리 와 카운트를 가져오는 쿼리의 값이 다를 때가 있다. (성능 때문에)
	 * 	성능 문제로 인해 카운트를 가져오는 쿼리를 더 단순하게 만드는 경우가 있다.
	 *
	 * 	복잡하고 성능이 중요한 쿼리에서는 fetchResult()로 한번에 가져오기보다는
	 * 	쿼리 두번을 보내는 식으로 하는 편이 낫다.
	 */
	@Test
	public void fetchResults(){
		final QMember member = QMember.member;
		QueryResults<Member> qResult = queryFactory.selectFrom(member)
														.fetchResults();

		long total = qResult.getTotal();
		List<Member> results = qResult.getResults();

		System.out.println("total cnt 	:: " + total);
		System.out.println("results 	:: " + results);
	}

	/**
	 * fetchCount()
	 * 		count() 만 수행한다.
	 */
	public void fetchCount(){
		final QMember member = QMember.member;
		long count = queryFactory.selectFrom(member).fetchCount();
	}
}
