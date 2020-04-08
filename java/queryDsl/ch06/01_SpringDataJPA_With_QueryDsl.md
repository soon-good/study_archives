# Spring Data JPA With QueryDsl

Spring Data JPA 기반으로 Repository 계층을 구현하면 기본키와 연관된 항목은 모두 JPA에서 기본으로 제공되게 된다. 예를 들어 Member를 Repository로 구현했을 경우 

- save(...)
- findById(Long id)
- findAll()

은 기본으로 제공된다. 

기본키로 지정되지 않은 필드는 사용자가 직접 구현해야 한다. 예를 들면 username 과 같은 항목이다.

ex) 

- findByUsername(String username)

# 목차

- Spring Data JPA Repository

  querydsl을 사용하지 않은 순수한 Spring Data JPA Repository 의 예제코드를 정리한다.

  - repository 생성

    샘플 리포지터리 예제이다.

  - repository 동작 테스트 코드 작성

    단순 동작이 잘 되는지를 테스트하기 위한 테스트 코드이다.

- Spring Data JPA Repository With QueryDsl

# Spring Data JPA Repository

QueryDsl 코드를 배제한 순수 Spring Data JPA Repository 코드이다.

## repository 생성

먼저 적당한 디렉터리에 datajpa라는 패키지를 생성하고 그 아래에 아래와 같이 MemberDataJPARepository 라는 이름의 interface를 생성했다. 코드를 보자

```java
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
 * ch06-item2-QueryDsl 지원 커스텀 리포지터리 만들기 >> MemberJpaCustom 상속하도록 변경
 */
public interface MemberDataJpaRepository extends JpaRepository<Member, Long> {

	/** 아래와 같이 작성하면 스프링 데이터 JPA 가 메서드 이름으로 자동으로 JPQL 을 만들어내는 전략을 취한다.
	 * ex) select m from Member m where m.username = :username */
	List<Member> findByUsername(String username);
}
```

  

- MemberDataJpaRepository는 JPARepository\<Member, Long\> 을 상속하는 interface이다. 
- Spring Data JPA 에서는 따로 쿼리 구현을 작성하지 않아도 interface 내에 findByUsername(String username) 을 생성해두면 내부적으로 jpql을 생성해 조회구문을 만들어낸다.
- 생성되는 쿼리의 예) findByUsername
  - select m from Member m where m.username = ?

## Repository 테스트 코드 작성

작성한 Repository가 동작하는지 단순 동작을 테스트 하는 테스트 코드를 작성하자.  

```java
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
```



# Spring Data JPA Repository With QueryDsl

개인적으로 스프링 개발팀에 원하는 바는 .... Spring Data JPA Repository 라는 용어의 이름을 좀 줄여줬으면 좋겠다..  



