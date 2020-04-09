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

Spring Data JPA Repository를 이용한 코드에 QueryDsl을 접목시켜 보자.  

- MemberJpaCustom 이라는 이름의 interface를 생성한다.
- MemberJpaCustom을 implements 하는 MemberJpaCustomImpl 을 만든다.
  - MemberJpaCustomImpl 에서는 QueryDsl을 이용한 검색/조회 쿼리들이 위치한다. 
    - search(MemberSearchCondition condition)
- MemberDataJpaRepository 
  - 스프링 Data JPA를 사용하는 인터페이스이다.
  - 방금 전, 바로 위의 예제에서 작성한 Interface이다.
  - JpaRepository\<Member, Long\> 을 implements 하고 있다.
  - 여기에 추가로 MemberJpaCustom을 implements 하도록 구현한다.

  

## MemberJpaCustom.java

QueryDsl을 사용하는 코드들을 추상화한 interface이다.  

**MemberJpaCustom.java**

```java
package com.study.qdsl.repository.custom;

import com.study.qdsl.dto.MemberTeamDto;
import com.study.qdsl.dto.condition.MemberSearchCondition;
import com.study.qdsl.entity.Member;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * ch06-item2-QueryDsl 지원 커스텀 리포지터리 만들기
 */
public interface MemberJpaCustom {
	public List<MemberTeamDto> search(MemberSearchCondition condition);
  // ... 
}

```

  

## MemberJpaCustomImpl.java

QueryDsl을 실제로 사용하는 구현체의 코드들을 이곳에서 구현한다.  

**MemberJpaCustomImpl.java**

```java
package com.study.qdsl.repository.custom;

import static com.study.qdsl.entity.QMember.*;
import static com.study.qdsl.entity.QTeam.*;
import static org.springframework.util.StringUtils.*;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.qdsl.dto.MemberTeamDto;
import com.study.qdsl.dto.QMemberTeamDto;
import com.study.qdsl.dto.condition.MemberSearchCondition;
import com.study.qdsl.entity.Member;
import com.study.qdsl.entity.QMember;
import com.study.qdsl.entity.QTeam;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

/**
 * ch06-item2-QueryDsl 지원 커스텀 리포지터리 만들기
 */
public class MemberJpaCustomImpl implements MemberJpaCustom {

	private final EntityManager em;
	private final JPAQueryFactory queryFactory;

	public MemberJpaCustomImpl(EntityManager em){
		this.em = em;
		queryFactory = new JPAQueryFactory(em);
	}

	@Override
	public List<MemberTeamDto> search(MemberSearchCondition condition) {
		return
			queryFactory.select(new QMemberTeamDto(
				member.id.as("memberId"),
				member.username,
				member.age,
				member.team.id.as("teamId"),
				member.team.name.as("teamName")
			))
			.from(member)
			.where(
				userNameEq(condition),
				teamNameEq(condition),
				ageGoe(condition),
				ageLoe(condition)
			)
			.leftJoin(member.team, team)
			.fetch();
	}

	private BooleanExpression userNameEq(MemberSearchCondition condition){
		return hasText(condition.getUsername()) ? member.username.eq(condition.getUsername()) : null;
	}

	private BooleanExpression teamNameEq(MemberSearchCondition condition){
		return hasText(condition.getTeamName()) ? team.name.eq(condition.getTeamName()) : null;
	}

	private BooleanExpression ageGoe(MemberSearchCondition condition){
		return condition.getAgeGoe() == null ? null : member.age.goe(condition.getAgeGoe());
	}

	private BooleanExpression ageLoe(MemberSearchCondition condition){
		return condition.getAgeLoe() == null ? null : member.age.loe(condition.getAgeLoe());
	}

	private BooleanExpression ageBetween(MemberSearchCondition condition){
		return member.age.between(condition.getAgeGoe(), condition.getAgeLoe());
	}
  
}
```



## MemberDataJpaRepository.java

Spring Data JPA 를 사용하도록 되어있는 JPA 코드에서 QueryDsl도 사용할 수 있도록 해준다. QueryDsl의 동작을 **가질수 있도록** MemberJpaCustom 도 implements 하게끔 한다.  

```java
package com.study.qdsl.repository.datajpa;

import com.study.qdsl.entity.Member;
import com.study.qdsl.repository.custom.MemberJpaCustom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberDataJpaRepository extends JpaRepository<Member, Long> , MemberJpaCustom {

	/** 아래와 같이 작성하면 스프링 데이터 JPA 가 메서드 이름으로 자동으로 JPQL 을 만들어내는 전략을 취한다.
	 * ex) select m from Member m where m.username = :username */
	List<Member> findByUsername(String username);
}
```



이렇게 하면 Spring Data JPA 코드 내에 QueryDsl 코드 역시 포함되게 된다.



# 개인적인 생각... 

Spring Data Jpa 코드에는 웬만하면 QueryDsl을 억지로 섞을 필요까지는 없을 것 같다고 생각된다. QueryDsl을 제공하는 @Repository를 따로 생성하여 사용하는 것이 기능과 목적을 모두 보장하는 것 같다는 생각이다.  

패키지 구조만 모델에 맞게끔 잘 분류하면, 하나의 제품 로직 내의 특정화면에 대한  repository 패키지 내에서 querydsl과 data jpa를 분류해서 상호 취합적인 관계로 사용할 수 있고, 그게 더 깔끔할 것 같다.  

물론, 강의에서 진행하는 코드들은 모두 data jpa와 querydsl을 혼용하여 사용하는 예제로 진행된다. 이 부분에 대해서는 따로 예제를 만들어보던가 해봐야겠다.  

