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

