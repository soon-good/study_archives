package com.study.qdsl.repository.datajpa;

import com.study.qdsl.entity.Member;
import com.study.qdsl.repository.custom.MemberJpaCustom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ch06 - item01
 * 순수 JPA 리포지터리에 비해 스프링 데이터 JPA 에서는 기본으로 제공되는 메서드들이 많다.
 *
 * 스프링 데이터 JPA 에서 기본으로 제공되는 메서드들 (GeneratedValue, All, 등등등 정말 기본적인 보일러플레이트는 제공하고 있다.)
 * 	: save(), findById(), findAll()
 *
 * 특정 컬럼에 대한 메서드는 제공하지 않는다.
 *  : ex) findByUsername()
 *
 * findByUsername(), findByAge() 등등 세부 컬럼에 대한 메서드를 만들었을 때
 * 스프링 데이터 JPA 는 메서드의 이름에 주어진 이름을 통해 자동으로 컬럼명을 인식해 JPQL 을 만들어내는 전략을 취한다.
 *
 * ch06-item2-QueryDsl 지원 커스텀 리포지터리 만들기 >> MemberJpaCustom 상속하도록 변경
 */
public interface MemberDataJpaRepository extends JpaRepository<Member, Long> , MemberJpaCustom {

	/** 아래와 같이 작성하면 스프링 데이터 JPA 가 메서드 이름으로 자동으로 JPQL 을 만들어내는 전략을 취한다.
	 * ex) select m from Member m where m.username = :username */
	List<Member> findByUsername(String username);
}
