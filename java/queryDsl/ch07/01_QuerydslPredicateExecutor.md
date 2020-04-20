# QuerydslPredicateExecutor

공식 문서 URL

- https://docs.spring.io/spring-data/jpa/docs/2.2.3.RELEASE/reference/html/#core.extensions.querydsl

# 참고

QuerydslPredicateExecutor는 실무에서 도입하기에는 무리가 있는 인터페이스이다. 그럼에도 정리하고 넘어가는 이유는, 실무에서 똥(?)을 밟지 않기 위해서... 이다. 

- 기억해놓고 있다가 누군가 사용하려 한다면 예제를 보여주고, 사용하지 말자고 친절히 이야기 해주어야 한다. 
- 또는 까먹고 있다가 나 자신이 사용할 수도 있기에(??)... 정리!!! 하고 넘어간다.

  

# QuerydslPredicateExecutor 인터페이스

QuerydslPredicateExecutor 는 Querydsl에서 사용자들의 편의를 위해 설계된 인터페이스이다. 대략의 기능들은 아래와 같다.

```java
public interface QuerydslPredicateExecutor<T> {

  Optional<T> findById(Predicate predicate);  

  Iterable<T> findAll(Predicate predicate);   

  long count(Predicate predicate);            

  boolean exists(Predicate predicate);        

  // … more functionality omitted.
}
```



# 실제 사용예

그리고 Repository 에서 사용시에는 간단히 아래와 같이 extends 하면 된다. 여기서는 Spring Data JPA 코드를 예로 든다.  

```java
interface SomeRepository extends JPARepository<SomeEntity, Long>, QuerydslPredicateExecutor<SomeEntity>{
  
}
```

- SomeRepository라고 이름 지은 Spring Data JPA 리포지터리이다.
- QuerydslPredicateExecutor\<SomeEntity\> 를 상속받는다.

사용시에는 아래와 같이 사용한다.

```java
Predicate predicate = someEntity.firstname.equalsIgnoreCase("kyle")
  .and(someEntity.lastname.startsWithIgnoreCase("sgjung"))
  .and(someEntity.age.between(20,50));

someRepository.findAll(predicate);
```

repository에서는 Predicate로 표현해놓은 표현식만을 받아 findAll 하고 있다. 언뜻 보기에는 엄청 편리해보인다. 하지만 문제점이 있다.

# 단점



- 조인이 불편하다는 점
  - 조인 못해서 죽은 귀신이 쓰였나? 할정도로 조인에 집착하는 것처럼 보일 수도 있다...
  - 하지만, 실제 운영상에서 테이블 하나만으로 데이터를 가져오는 경우는 거의 없기 때문에 조인이 가능한지 여부가 필수이다.
  - 위의 QuerydslPredicateExecutor를 사용할 경우 묵시적 조인은 가능하다. 그리고 left join은 불가능하다.
- 서비스 계층에서 Predicate를 만들어 전달해주어야한다.
  - 실제 운영/개발 상에서는 리포지터리 혹은 DAO에서의 역할을 명확히 구분하는 경우가 많다.
  - 각 계층에서의 역할을 명확히 분리해야 각 계층의 독립성이 높아져 유지보수가 편하다.
  - Predicate는 querydsl의 클래스이다.
  - 하지만, 서비스 계층에서 Predicate를 만들어 전달하는 것은 리포지터리에 의존적이다.
  - 예를 들자면, QueryDsl 외의 다른 고 가용성의 라이브러리 도입시 문제가 될수 있는 소지가 있다.





