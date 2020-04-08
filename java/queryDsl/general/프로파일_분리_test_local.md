# 프로파일(profile) 분리

자세한 설명은 생략하고 과정만을 정리한다으아으아~  

- src/main/resources/application.yml 내의 spring.profiles.active 를 

  - dev

  - local

  - production 

    등 원하는 내용으로 채워준다.

- src/test/ 아래에 resources 디렉터리를 생성한다.

- src/test/resources 디렉터리에 application.yml을 생성한다.  

  (또는 src/main/resources에 생성한 application.yml 을 복사해준다.)  

- src/test/resources/application.yml 에 필요한 설정들을 추가해주자.

  - 이때, spring.profiles.active 는 'test'로 지정해준다.

**예) src/test/resources/application.yml**

```yaml
spring:
  profiles:
    active: test
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
#        show_sql: true
        format_sql: true
        use_sql_comments: true

logging.level:
 org.hibernate.SQL: debug
# org.hibernate.type: trace
```



