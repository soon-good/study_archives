# 스프링 부트 설정 - JPA, DB

## application.yml

src/main/resources application.properties 파일을 삭제하고 application.yml 파일을 생성한다.  

yml 파일은 들여쓰기가 중요한 데, 들여쓰기를 한단계 더 들어가서 할때마다 공백 2개를 주어서 들여쓰기해야 에러가 나지 않는다.  

application.yml 내용

```yaml
spring:
  datasource:
    url: jdbc:h2:tcp://localhost/~/querydsl
    username: sa
    password:
    driver-class-name: org.h2.Driver

  jpa:
    hibernate:
      ddl-auto: create
      # create : 어플리케이션 로딩 시점에 테이블들을 모두 drop 하고 테이블을 다시 생성
      # create-drop : 어플리케이션 로딩 시점에 테이블들을 모두 drop하고 테이블을 다시 생성, 종료할때 모두 drop
    properties:
      hibernate:
#        show_sql: true
        format_sql: true

logging.level:
 org.hibernate.SQL: debug
# org.hibernate.type: trace
```



- spring.jpa.hibernate.ddl-auto

  - create
    - 어플리케이션 로딩 시점에 테이블을 모두 drop하고 테이블을 다시 생성한다.
  - create-drop
    - 어플리케이션 로딩 시점에 테이블들을 모두 drop하고 테이블을 다시 생성한다. 종료할때는 모든 테이블을 drop 한다.
    - 테스트 코드로 DB결과를 확인하기에는 적합하지 않다. 테스트 코드가 종료된 후에 DB에서 결과를 확인하려고 했는데 모든 결과가 삭제되어 있기 때문이다.

- spring.jpa.properties.hibernate

  - format_sql, show_sql 둘중에 하나만 true로 설정해주고 사용하는게 좋다. 

  - format_sql : true

  - show_sql: true

    > show_sql은 System.out.println을 사용한다. format_sql은 logger를 사용한다. 가급적 logger를 사용하는게 출력 포맷팅 등에서 기능의 다양함이 있기 때문에 format_sql 만 enable 시켜둔다.

- org.hibernate.type: trace

  - trace로 지정하지 않으면 기본값으로 아래처럼 ? 로 파라미터를 표시하게 된다.  

    > insert into hello(id) values(?)

  - trace로 지정하면 파라미터는 보여주지만 조금 찾아보기는 불편하다.
  - 더 자세히 보고싶다면, p6spy 라이브러리를 쓰면 되는데 (build.gradle에 의존성 추가), 이 방식은 운영테스트보다는 개발버전에서만 사용하는 것이 성능에 지장을 덜 준다.

## 테스트 코드

```java
package com.study.qdsl;

import static org.assertj.core.api.Assertions.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.qdsl.entity.Hello;
import com.study.qdsl.entity.QHello;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional	// 테스트에 @Transactional 이 있으면 보통 Rollback을 하게 된다.
@Commit
class QdslApplicationTests {

//	@PersistenceContext  // @Autowired 대신 @PersistenceContext 어노테이션으로 사용해도 무방해다
	@Autowired
	EntityManager em;

	@Test
	void contextLoads() {
		Hello hello = new Hello();
		em.persist(hello);

		JPAQueryFactory query = new JPAQueryFactory(em);
		QHello qHello = new QHello("h");

		Hello result = query
			.selectFrom(qHello) // query 와 관련된 것은 entity가 아니라 QType을 인자로 주어야 한다.
			.fetchOne();

		assertThat(result).isEqualTo(hello);
		assertThat(result.getId()).isEqualTo(hello.getId());
	}

}
```



- @Transactional 
  - @Transactional 이 있으면 기본적으로 Rollback을 하게 된다.
- @Commit
  - 테스트 결과로 insert한 것을 DB에 유지하면서 보기 위해 @Commit 을 추가했다.

## build.gradle 의존성 추가

파라미터 바인딩을 더 깔끔하게 보고 싶은경우 추가하는 라이브러리는 p6spy-spring-boot-starter 이다. p6spy 라이브러리 좋다고 추천을 한다. 

> ```groovy
> implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.5.8'
> ```



의존성을 추가하고 나서 Test를 직접 돌려보면

> insert into hello(id) values(1);

과 같은 형태로 로그에 찍히게 된다.

