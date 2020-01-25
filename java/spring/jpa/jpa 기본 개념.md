# spring data jpa - 기본 개념

JPA는 인터페이스로서 자바 표준 명세서이다. JPA를 사용하기 위해서는 인터페이스를 구현한 구현체가 필요하다. 

구현체로 대표적인 것들은

- Hibernate
- Eclipse Link

가 있다. Spring에서 JPA를 사용해 무언가를 만들때 직접 hibernate, Eclipse Link를 직접 사용하지는 않는다.

Hibernate, Eclipse Link 를 추상화한 Spring Data JPA 모듈을 사용한다.

(slf4j 가 Log4j와 Logback을 추상화 시킨것을 떠올리면 될듯하다.)

Hibernate, Eclipse Link, Spring Data JPA, JPA의 관계를 살펴보면 아래와 같다.

- JPA <- Hibernate <- Spring Data JPA

Hibernate는 JPA를 구현했고, Spring Data JPA 모듈은 Hibernate를 Wrapping 하고 있다.

이렇게 한단계 더 Wrapping한 Spring Data JPA를 제공하는 이유는

- 구현체 교체의 용이성
- 저장소 교체의 용이성

을 위해서 필요한 것이라 한다.

**구현체 교체의 용이성**

이렇게 해놓은 이유는 Hibernate보다 성능이 좋은 다른 구현체가 나타날 수 있을때 개발자들이 이를 선택해서 교체하기 쉽도록 대안을 제시하는 것이라 한다.

실제로 자바의 Redis 클라이언트가 Jedis에서 Lettuce로 대세가 넘어갈 때 Spring Data Redis를 쓰던 개발자들은 아주 쉽게 교체를 했다고 한다.

**저장소 교체의 용이성**

관계형 데이터베이스 외의 다른 저장소로 쉽게 교체하기 위해서이다. 예를 들면,

서비스 초기에는 관계형 데이터베이스로 모든 기능의 처리가 가능하지만 점점 트래필이 많아져 관계형 데이터베이스로는 도저히 감당이 안될 때가 올때가 있다. 이럴 경우 MongoDB로 교체가 필요하다면 개발자는 

- Spring Data JPA에서 Spring Data MongoDB로

의존성만 교체하면 된다.

Spring Data 의 하위 프로젝트 들은 기본적인 CRUD의 인터페이스가 같기 때문이다. 즉, Spring Data JPA, Spring Data Redis, Spring Data Mongo DB 등의 Spring Data 하위 프로젝트들은 save(), findAll(), findOne() 등을 인터페이스로 갖고 있다. 따라서 저장소가 교체되어도 기본적인 기능은 변경할 것이 없다. 

이런 장점들로 인해 Hibernate를 직접 쓰기보다 Spring 팀에서 Spring Data 프로젝트를 권장하고 있다.