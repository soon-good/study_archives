package com.study.qdsl.advanced.ch04.projections.item02;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.qdsl.dto.MemberDto;
import com.study.qdsl.dto.UserDto;
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
 * Ch03 - item2
 * DTO 프로젝션
 *  : JPQL 을 사용할 때
 *  	- 생성자 방식을 사용하는데 패키지의 경로까지 모두 지정해주어 new 연산자를 select 절에 주어야 한다.
 *      - 패키지 경로를 모두 지정해주어야 하는 것은 조금 크리티컬 하긴 하다.
 *      - 생성자 방식만 지원한다.
 *
 *  : QueryDsl 을 사용할 때
 *  	- 방법이 3가지 있다.
 *  	 1. 프로퍼티 접근 	( Projections.bean ( Class<? extends T> type, ...) 이용 )
 *  	 2. 필드 직접 접근	( Projections.fields ( Class<? extends T> type, ...) 이용 )
 *  	 3. 생성자 사용	( Projections.constructor ( Class<? extends T> type, ...) 이용 )
 */
@SpringBootTest
@Transactional
public class QdslDtoProjectionTest {

	@Autowired
	EntityManager em;

	JPAQueryFactory queryFactory;

	@BeforeEach
	public void before(){
		queryFactory = new JPAQueryFactory(em);

		Team marketingTeam = new Team("Marketing");
		Team analysisTeam = new Team("Analysis");
		Team musicianTeam = new Team("Musician");
		Team nullTeam = new Team("NullTeam");

		em.persist(marketingTeam);
		em.persist(analysisTeam);
		em.persist(musicianTeam);
		em.persist(nullTeam);

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
		em.persist(ceo);
	}

	/**
	 * JPQL 방식
	 *  : 생성자 방식만 지원
	 *  : 패키지 경로명을 모두 적어주어야 한다.
	 */
	@Test
	public void dtoProjectionByJPQL(){
		List<MemberDto> resultList = em
			.createQuery(
				"select new com.study.qdsl.dto.MemberDto(m.username, m.age) from Member m",
				MemberDto.class
			)
			.getResultList();

		for(MemberDto d : resultList){
			System.out.println("memberDto :: " + d);
		}
	}

	/**
	 * QueryDsl 사용방식 (1)
	 * QueryDSL의 Projections.bean()
	 *
	 * 자바 빈 규약(Getter/Setter)를 활용한 방식
	 * 반드시 기본 생성자(NoArgsConstructor)가 있어야 동작한다.
	 */
	@Test
	public void dtoProjectionBySetter(){
		QMember member = QMember.member;

		List<MemberDto> dtoList = queryFactory
			.select(
				Projections.bean(
					MemberDto.class,
					member.username,
					member.age
				)
			)
			.from(member)
			.fetch();

		for(MemberDto d : dtoList){
			System.out.println("data :: " + d);
		}
	}

	/**
	 * QueryDsl 사용방식 (2)
	 * QueryDSL의 Projections.fields() 이용
	 *
	 * 필드 직접 접근
	 *  : 멤버필드가 private 이어도 접근가능하다. 강력하다. 리플렉션 쓰면 가져올수 있다. 내부적으로 리플렉션이 적용되어 있다.
	 *  : 반드시 기본 생성자(NoArgsConstructor)가 있어야 동작한다.
	 */
	@Test
	public void dtoProjectionByField(){
		QMember member = QMember.member;

		List<MemberDto> dtoList = queryFactory
			.select(
				Projections.fields(
					MemberDto.class,
					member.username,
					member.age
				)
			)
			.from(member)
			.fetch();

		for(MemberDto d : dtoList){
			System.out.println("data :: " + d);
		}
	}

	/**
	 * QueryDsl 사용방식 (3)
	 * QueryDSL의 Projections.constructor() 이용
	 *
	 * : 반드시 기본 생성자(NoArgsConstructor)가 있어야 동작한다.
	 */
	@Test
	public void dtoProjectionByConstructor(){
		QMember member = QMember.member;

		List<MemberDto> dtoList = queryFactory
			.select(
				Projections.constructor(
					MemberDto.class,
					member.username,
					member.age
				)
			)
			.from(member)
			.fetch();

		for(MemberDto d : dtoList){
			System.out.println("data :: " + d);
		}
	}

	/**
	 * 컬럼명에 AS를 주는 경우
	 *  : Dto 바인딩시 필드 명을 다르게 화면에 전달해주고 싶을 경우가 있다. 이에 대한 예제이다.
	 *
	 * 필드명이 다른 Dto 를 가져올때 JPA 에서 쿼리 내에 지정한 Dto 에 이름이 맞는 필드가 없을 경우 null 로 값을 대입해준다.
	 * 	: MemberDto 와 필드명이 다른 UserDto 의 예를 들어보자.
	 * 	: UserDto 는 username 필드 대신 name 필드가 있다.
	 */
	@Test
	public void dtoProjectionAliasBasic(){
		QMember member = QMember.member;

		List<UserDto> dtoList = queryFactory
			.select(
				Projections.fields(
					UserDto.class,
					member.username,
					member.age
				)
			)
			.from(member)
			.fetch();

		/** 결과를 확인해보면 에러는 나지 않는데, name 필드에 null 값이 들어간다. */
		System.out.println("===== Non Alias Result =====");
		for(UserDto d : dtoList){
			System.out.println("data :: " + d);
		}

		/** 해결방법 */
		/** ex) member.username.as("name") */
		List<UserDto> aliasResult = queryFactory.select(
			Projections.fields(
				UserDto.class,
				member.username.as("name"),		// ExpressionUtils.as(member.username, "as") 와 같은 표현이다.
				member.age
			)
		).from(member).fetch();

		/** 결과를 확인해보면 제대로 값이 들어와 있다.. */
		System.out.println("===== Alias Result =====");
		for(UserDto d : aliasResult){
			System.out.println("data :: " + d);
		}
	}

	/**
	 * 서브쿼리 사용시
	 * 	ExpressionUtils.as( JPAExpressions.select(...).from(...), "별칭" ) 을 사용한다.
	 * 	각 컬럼에 Max 값을 찍어서 준다. (리포팅 프로젝트 같은 데에서 흔히 쓰이는 방식...)
	 */
	@Test
	public void dtoProjectionAliasSubquery(){
		QMember member = QMember.member;
		QMember subMember = new QMember("subMember");

		List<UserDto> aliasResult = queryFactory.select(
			Projections.fields(
				UserDto.class,
				member.username.as("name"),

				ExpressionUtils.as(
					JPAExpressions.select(
						subMember.age.max()
					)
					.from(subMember),
					"age"
				)
			)
		).from(member).fetch();

		System.out.println("===== Alias Result =====");
		for(UserDto d : aliasResult){
			System.out.println("data :: " + d);
		}
	}

	/**
	 * Alias 적용시 생성자를 사용한 프로젝션의 경우에는 (Projections.constructor( Class <? extends T>, ...))
	 * 	: 타입만 맞으면 제대로 들어가기 때문에 크게 문제가 되지는 않는다.
	 * 	: 하지만 생성자 오버로딩의 경우, 생성자 매개변수의 갯수는 같지만 순서가 다른 경우도 있기 때문에 조금은 제약이 있다.
	 */
	@Test
	public void dtoProjectionAliasConstructor(){
		QMember member = QMember.member;

		List<UserDto> aliasResult = queryFactory.select(
			Projections.constructor(
				UserDto.class,
				member.username,
				member.age
			)
		).from(member).fetch();

		System.out.println("===== Alias Result =====");
		for(UserDto d : aliasResult){
			System.out.println("data :: " + d);
		}
	}
}
