# 편의성 Support 클래스(Web과 Repository)

- QuerydslWebSupport
- QuerydslRepositorySupport



# QuerydslWebSupport

실무에서 사용하기에는 기능이 많이 부족한 클래스이다. 나중에 까먹고 이 클래스를 사용하게 되지는 않을까 하는 노심초사하는 마음에... 정리시작!!  

Query String 형식의 GET 요청을 받을 때 이 queryString 요청을 컨트롤러 계층에서 Querydsl의 Predicate로 변환하여 보여준다. 하지만, 

- leftjoin이 지원되지 않는 점
- 컨트롤러 계층의 코드가 Querydsl 라이브러리에 의존적으로 될수밖에 없는 점
  - 리포지터리 안에서만 Querydsl을 사용하도록 하여 추후 필요에 의해 다른 라이브러리(mybatis, jdbctemplate)을 활용한 방식으로 변경시 웹계층도 모두 바꿔야 하는 불상사가 발생할 수도 있다.

으로 인해 실제로 사용하는 경우는 그리 많지 않다. Querydsl 초창기에 편의성 제공을 위해 querydsl 팀에서 제공을 해주었을 수도 있는데, 단순히 시도에 그치게 된 클래스가 아닐까 싶다.

관련 예제는 https://docs.spring.io/spring-data/jpa/docs/2.2.3.RELEASE/reference/html/#core.web.type-safe 에서 확인 가능하다.  



# QuerydslRepositorySupport

- Sorting 이 제대로 지원되지 않고 (Spring Data의 Sorting 미지원)
- 페이징 적용시 쿼리 튜닝이 되지 않은 기본 페이징만 사용가능

하다는 단점이 있긴 하지만, 초기 프로토타입을 빠르게 구현해야 한다면... 추천한다.

장점으로는 

- Spring Data 의 Pageable 을 그대로 받아서 applyPagination 으로 처리가 가능하다.
- getQueryDsl() 등과 같은 여러 편의성 메서드를 제공한다.
- 의존성 주입 등의 코드가 미리 구비되어 있다. (직접 주입도 가능하다.)



## 기본 Select 구문 예제  

```java
	// 별다른 import 작업 없이 부모 클래스인 QuerydslRepositorySupport 내의 from() 을 사용 가능하다.
	@Override
	public List<MemberTeamDto> search(MemberSearchCondition condition) {
    final QMember member = QMember.member;
    final QTeam team = QTeam team;
    
		List<MemberTeamDto> data = from(member)
			.leftJoin(member.team, team)
			.where(
				MemberExpression.userNameEq(condition),
				MemberExpression.teamNameEq(condition),
				MemberExpression.ageBetween(condition)
			)
			.select(
				new QMemberTeamDto(
					member.id.as("memberId"),
					member.username,
					member.age,
					team.id.as("teamId"),
					team.name.as("teamName")
				)
			)
			.fetch();

		return null;
	}
```



## 페이징 적용 Select 구문 예제

```java
	// Sorting이 불편한 점이 있긴 하지만, 페이징을 간편하게 사용할 수 있도록 지원해주는 점은 장점이다.
	// 쿼리 튜닝 가능 여부는 찾아봐야 알 듯 하다.
	@Override
	public Page<MemberTeamDto> searchPagingSimple(MemberSearchCondition condition, Pageable pageable) {
		JPQLQuery<MemberTeamDto> jpaQuery = from(member)
			.leftJoin(member.team, team)
			.where(
				MemberExpression.userNameEq(condition),
				MemberExpression.teamNameEq(condition),
				MemberExpression.ageBetween(condition)
			)
			.select(new QMemberTeamDto(
				member.id.as("memberId"),
				member.username,
				member.age,
				team.id.as("teamId"),
				team.name.as("teamName")
			));
//			.offset(pageable.getOffset())
//			.limit(pageable.getPageSize())
//			.fetchResults();

		jpaQuery = getQuerydsl().applyPagination(pageable, jpaQuery);

		QueryResults<MemberTeamDto> fetchResult = jpaQuery.fetchResults();
		List<MemberTeamDto> contents = fetchResult.getResults();
		long count = fetchResult.getTotal();
		return new PageImpl<MemberTeamDto>(contents, pageable, count);
	}
```



# 참고 - MemberExpression

- userNameEq
- teamNameEq
- ageGoe
- ageLoe
- ageBetween

등의 함수는 Member 엔티티에 대한 공통적인 표현식이다. 관련 표현식을 공통화 할 수 있다. 여기저기서 userNameEq 등의 표현식을 재정의해 사용하기 귀찮아서 작성해봤다.  

**MemberExpression.java**

```java
package com.study.qdsl.dto.exression;

import static com.study.qdsl.entity.QMember.member;
import static com.study.qdsl.entity.QTeam.team;
import static org.springframework.util.StringUtils.hasText;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.study.qdsl.dto.condition.MemberSearchCondition;

public class MemberExpression {

	public static BooleanExpression userNameEq(MemberSearchCondition condition){
		return hasText(condition.getUsername()) ? member.username.eq(condition.getUsername()) : null;
	}

	public static BooleanExpression teamNameEq(MemberSearchCondition condition){
		return hasText(condition.getTeamName()) ? team.name.eq(condition.getTeamName()) : null;
	}

	public static BooleanExpression ageGoe(MemberSearchCondition condition){
		return member.age.goe(condition.getAgeGoe());
	}

	public static BooleanExpression ageLoe(MemberSearchCondition condition){
		return member.age.loe(condition.getAgeLoe());
	}

	public static BooleanExpression ageBetween(MemberSearchCondition condition){
		return member.age.between(condition.getAgeGoe(), condition.getAgeLoe());
	}
}
```







