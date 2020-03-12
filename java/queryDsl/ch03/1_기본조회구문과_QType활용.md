# 기본 조회구문과 QType 활용

예를 들어 아래의 데이터들이 들어있다고 하자.

```text
 member :: Member(id=4, username=John, age=23)
 member :: Member(id=5, username=Becky, age=22)
 member :: Member(id=6, username=Kyle, age=28)
 member :: Member(id=7, username=Stacey, age=24)
 member :: Member(id=8, username=Aladdin, age=35)
 member :: Member(id=9, username=Genie, age=41)
 member :: Member(id=10, username=Beethoven, age=251)
 member :: Member(id=11, username=Chopin, age=210)
 member :: Member(id=12, username=Genie, age=210)
 member :: Member(id=13, username=null, age=100)
```



우리는 이 데이터를 활용해 기본적인 Select 문을 QueryDsl 방식으로 작성할 것이다.  

Member와 Team 도메인의 연관관계는 바로 이전에서 작성했었다.  

Member와 Team을 QueryDsl에서 인식할 수 있는 타입인 QMember, QTeam으로 먼저 변환하자.  

Gradle > Task> other > compileQueryDsl을 통해 가능하다.



## 기본적인 SELECT 문

```java
package com.study.qdsl.remind.ch03;

import static com.study.qdsl.entity.QMember.*;

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

@SpringBootTest
@Transactional
public class BasicQTypesTest {

	@Autowired
	EntityManager entityManager;

	JPAQueryFactory queryFactory;

	@BeforeEach
	public void before(){
		queryFactory = new JPAQueryFactory(entityManager);

		Team marketingTeam = new Team("Marketing");
		Team analysisTeam = new Team("Analysis");
		Team musicianTeam = new Team("Musician");

		entityManager.persist(marketingTeam);
		entityManager.persist(analysisTeam);
		entityManager.persist(musicianTeam);

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

	}

	@Test
	public void selectAll(){
		List<Member> members = queryFactory.select(member)
			.from(member)
			.fetch();

		for(Member m : members){
			System.out.println( " member :: " + m);
		}
	}
}
```





















