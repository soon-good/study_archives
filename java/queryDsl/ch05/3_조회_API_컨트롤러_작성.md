# 조회 API 컨트롤러 개발

조회 API 컨트롤러에서는 local 이라는 운영 프로필 환경에 한해 샘플 데이터를 넣어준 후 샘플 데이터를 기반으로 동작하도록 할 예정이다.  

# 샘플 데이터 로컬 개발환경 구성

## application.yml - 프로파일 등록

```yaml
spring:
  profiles:
    active: local
  datasource:
    url: jdbc:h2:tcp://localhost/~/querydsl
    username: sa
    password:
    driver-class-name: org.h2.Driver

  jpa:
    hibernate:
      ddl-auto: create
    properties:
      hibernate:
#        show_sql: true     # system.out 으로 콘솔에 출력된다. (추천하지 않는 옵션이다.)
                            # logging.level.org.hibernate.SQL을 debug로 두면 로거를 사용해 SQL이 출력된다.
                            # system.out 대신 logger를 쓰고 싶다면 spring.jpa.hibernate.properties.show_sql 은 사용하지 말자.
        format_sql: true
        use_sql_comments: true

logging.level:
 org.hibernate.SQL: debug
# org.hibernate.type: trace
```

  

만약 test와 local 개발 환경 profile 을 분리하지 않았다면 아래의 링크를 참고해 분리하자~  

[프로파일 분리 test/local](https://github.com/soongujung/study_archives/blob/master/java/queryDsl/general/프로파일_분리_test_local.md)

## 샘플 데이터 생성 Component

초기 데이터를 생성하는 구문 자체를 static으로 구성하고 Component로 등록했다. 그리고 @PostConstructor를 이용해 servlet이 초기화 되는 시점에 샘플데이터 insert 구문을 실행하도록 구성한다.  

@PostConstructor와 @Transactional을 함께 사용할 수 없기 때문에 @PostConstructor, @Transactional 에서의 동작을 분리했다.  

**ex) SampleDataComponent.java**

```java
package com.study.qdsl.config.local;

import com.study.qdsl.entity.Member;
import com.study.qdsl.entity.Team;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
	Ch05 - item4 :: 조회 API 컨트롤러 개발
	 : profile 이 local, test 일 경우에 대해 환경 분리
	   (application.yml 따로 위치시킴)
	 : local 프로필 일 경우에 대해 샘플 데이터 넣도록 설정 추가
	 : 컨트롤러와 repository 연동
 */
@Profile("local")
@Component
@RequiredArgsConstructor
public class SampleDataComponent {

	private final InitSampleData initSampleData;

	@PostConstruct
	public void init(){
		initSampleData.init();
	}

	@Component
	static class InitSampleData{

//		@PersistenceContext
		@Autowired
		EntityManager entityManager;

		@Transactional
		public void init(){
			Team marketingTeam = new Team("Marketing");
			Team analysisTeam = new Team("Analysis");
			Team musicianTeam = new Team("Musician");
			Team nullTeam = new Team("NullTeam");

			entityManager.persist(marketingTeam);
			entityManager.persist(analysisTeam);
			entityManager.persist(musicianTeam);
			entityManager.persist(nullTeam);

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
			entityManager.persist(ceo);
		}
	}
}
```



# 컨트롤러 작성

```java
package com.study.qdsl.web.member;

import com.study.qdsl.dto.MemberTeamDto;
import com.study.qdsl.dto.condition.MemberSearchCondition;
import com.study.qdsl.repository.MemberJpaQdslRepository;
import com.study.qdsl.repository.datajpa.MemberDataJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {
	private final MemberJpaQdslRepository repository;
	private final MemberDataJpaRepository dataRepository;

	@GetMapping("/v1/members")
	public List<MemberTeamDto> getAllMembers(MemberSearchCondition condition){
		return repository.searchByWhere(condition);
	}
}

```

