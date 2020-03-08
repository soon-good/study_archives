package com.study.qdsl.repository;

import static com.study.qdsl.entity.QMember.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.qdsl.entity.Member;
import com.study.qdsl.entity.QMember;
import java.util.List;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 참고)
 * 방법 2) 에 비해서 방법 1)이 QueryDsl 테스트 코드 작성시 조금 더 편리하다.
 * 방법 2) 의 경우는 테스트코드 작성시 외부에서 의존성 라이브러리를 주입해야 하므로 조금 불편
 *
 * 참고) EntityManager 는 스프링 컨테이너에서 오직 하나인 인스턴스 객체인데, 동시성 문제가 있지 않을까요??
 * 		JPAQueryFactory 또한 EntityManager 인스턴스를 주입받아 사용하고 있습니다. 동시성 문제가 없나요??
 *
 * 		EntityManager 는 싱글턴으로 스프링 컨테이너에서 주입받은 객체이다.
 * 		JPAQueryFactory 의 동작 역시 EntityManager 인스턴스에 의존하고 있다.
 * 		EntityManager 를 스프링에서 사용시 동시성 문제가 없이 트랜잭션 단위로 모두 분리되도록 처리해주도록 설계되어 있다.
 * 		EntityManager 에는 실제 동작을 책임지는 역할을 하는 객체가 아닌 Proxy 하는 객체, 즉, 대리로 해주는 객체(가짜객체)를 주입해준다.
 * 		주입받은 프록시 역할을 하는 객체가 각각 다른 곳에 바인딩 되도록 라우팅을 해준다.
 *
 * 		더 자세한 내용은 김영한 님 JPA 책 13.1 트랜잭션 범위의 영속성 컨텍스트를 참고하면 된다.
 */
@Repository
//@RequiredArgsConstructor
public class MemberJpaQdslRepository {

	private final EntityManager em;
	private final JPAQueryFactory queryFactory;

	/** 방법 1)
	 * JPAQueryFactory 의 인스턴스를 내부에서 생성한다.
	 **/
	public MemberJpaQdslRepository(EntityManager em) {
		this.em = em;
		this.queryFactory = new JPAQueryFactory(em);
	}

	/** 방법 2)
	 * QueryDslConfig.java 에 선언했듯이 @Bean 으로 JPAQueryFactory 인스턴스를 생성하는 빈을 등록한다.
	 * EntityManager, JPAQueryFactory를 인자로 받아 의존성 주입하는 생성자를 작성한다.
	 * 		또는
	 * @RequiredArgsConstructor 를 선언하고 의존성 주입하는 생성자를 따로 두지 않는다.
	 * final 선언된 멤버 필드들을 모두 생성자에 의존성 주입해준다.
	 */
//	public MemberJpaQdslRepository(EntityManager em, JPAQueryFactory queryFactory) {
//		this.em = em;
//		this.queryFactory = queryFactory;
//	}

	public List<Member> findAll() {
		return queryFactory.selectFrom(member)
			.fetch();
	}

	public List<Member> findByUsername(String username){
		return queryFactory.selectFrom(member)
			.where(member.username.eq(username))
			.fetch();
	}
}
