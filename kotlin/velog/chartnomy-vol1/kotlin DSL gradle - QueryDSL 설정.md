# kotlin DSL gradle - QueryDSL 설정

[(velog](https://velog.io/@gosgjung/kotlin-DSL-gradle-QueryDSL-%EC%84%A4%EC%A0%95) 에 정리했던 내용을 github에 백업 및 버전관리하기 위한 용도)   

> 아직 QueryDsl 의존성을 코틀린 DSL 기반 Gradle에서 추가하는 예제가 있는 Spring Boot 서적들이 없는 것 같았다. (물론 내가 못 찾는 거일수도 있다는거). 인터넷을 찾아가면서 kotlin 기반 gradle 파일에서 querydsl 을 설정한 내용들을 정리해보려고 한다.  
>
>   
>
> 사실 예전에는 김영한님 인프런 강의를 봤었고 그대로 build.gradle.kts 로 옮기려 했는데 srcDir 지정하는 부분에서 크게 막혔고, kapt 설정하는 부분 역시 무슨 역할인지 처음에는 잘 몰라서 헤매긴 했다.  
>
>   
>
> 지금도 정리중이긴 하지만, gradle, gradle wrapper, gradle kotlin dsl, kapt 등의 개념에 대해 따로 정리해둘 예정이다...휴우...  
>
>   
>
> 현재 진행중인 사이드 프로젝트를 위한 데이터는 만들었는데 이걸 얼른 빨리 Elastic Cloud에 데이터 올려서 DataJPA로 연동하고 싶은데 하는 마음만 앞서고 있다. 릴뤡스~ 해야 하는데 말이다...  

  

# 참고자료

- 가장 도움이 되었던 자료  
[Kotlin 으로 Spring Boot JPA 프로젝트에 Querydsl 적용하기](https://deep-dive-dev.tistory.com/38)  

  


# github repository
- [lognomy github 리포지터리](https://github.com/soongujung/lognomy/tree/master)  

  

# build.gradle.kts

## plugins
```kotlin
plugins {
    // ...
    kotlin("kapt") version "1.4.10"
}  
```
## dependencies

```kotlin
dependencies {
    // ...
    implementation("com.querydsl:querydsl-jpa:4.2.1")
    implementation("org.mariadb.jdbc:mariadb-java-client:2.7.0")
    kapt("com.querydsl:querydsl-apt:4.2.2:jpa")
    
    // ...
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor(group = "com.querydsl", name = "querydsl-apt", classifier = "jpa")
    // ...
}
```

## srcDir 지정
```kotlin
sourceSets["main"].withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class){
    kotlin.srcDir("$buildDir/generated/source/kapt/main")
}
```

## build.gradle.kts
부분만 봐서는 이해가 안되고 build.gradle.kts의 전체 내용을 올려야지 의심되는 부분들이 이해가 될때가 있다. 그래서 글이 길어지더라도... 투머치이긴 하지만 전체 내용을 적어본다.
```kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.3.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("jvm") version "1.3.72"
    kotlin("plugin.spring") version "1.3.72"
    kotlin("plugin.jpa") version "1.3.72"
    kotlin("kapt") version "1.4.10"
//    kotlin("plugin.kapt") version "1.4.10"
}

group = "io.chart"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("com.querydsl:querydsl-jpa:4.2.1")
    implementation("org.mariadb.jdbc:mariadb-java-client:2.7.0")
    kapt("com.querydsl:querydsl-apt:4.2.2:jpa")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("mysql:mysql-connector-java")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor(group = "com.querydsl", name = "querydsl-apt", classifier = "jpa")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.projectreactor:reactor-test")
}

sourceSets["main"].withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class){
    kotlin.srcDir("$buildDir/generated/source/kapt/main")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}
```

  

# QueryDsl Configuration

java 에 비해 생소할 수도 있다. 코틀린에서는 클래스 생성시 주 생성자(primary constructor)를 사용해 필드(코틀린에서는 프로퍼티라고 부른다.)를 선언할 수 있다.  

더 깊은 내용은 아직 나도 공부해나가는 과정이라... 차차 공부해나갈 예정이다~ 코틀린의 생성자에 대해서는 공식문서를 한번 정리할 예정인데 언제 시간이 날지는 잘 모르겠다...
```kotlin
package io.chart.lognomy.config

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Configuration
class QueryDslConfiguration (
        @PersistenceContext
        val entityManager: EntityManager ){
    @Bean
    fun jpaQueryFactory () = JPAQueryFactory(entityManager)
}
```

  

# 샘플 Entity, Repository, Service 작성

테스트 용도로 일단 뭔가 DB에 때려넣고 테스트를 해봐야 QueryDsl의 동작을 확인해볼수 있을것 같아 시작했다. 여기서 테스트용 DB는 h2 대신 docker를 이용한 mariadb를 사용했다.  

docker mariadb 를 사용하는 Datasource의 profile은 testdocker 로 이름지었다.  

가장 만만한 예제는 역시 학교에서 고전처럼 배우는 Employee 테이블이다ㅋㅋ 단순 테스트를 위해 그리 복잡하게 짜지 않았다.  

## Entity 작성 
kotlin 에는 data 라는 예약어가 있다. 해당 내용에 대해서는 [코틀린 data 클래스](https://velog.io/@gosgjung/kotlin-data-%ED%81%B4%EB%9E%98%EC%8A%A4) 에 정리해두었다. 
```kotlin
package io.chart.lognomy.temporary

import javax.persistence.*

@Entity
@Table(name = "Employee")
data class Employee(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMP_NO")
    val id: Long,

    @Column(name = "EMP_NAME", nullable = false)
    var name: String
)
//{
//    constructor(name: String){
//        this.name = name
//    }
//}
```
  

## 샘플 Repository, QRepository 작성하기

사람마다 QueryDsl을 작성하는 데에 차이가 있겠지만 나는 DataJPA 기능을 모아놓은 리포지터리는 그대로 두고 QueryDsl 기반 리포지터리는 따로 생성해놓는 편이다.  

이렇게 하면 굳이 상속에 대해 깊게 관여하지 않아도 되고, 앞에 Q가 붙어있는것이 QueryDsl 리포지터리임을 알수 있고, Query가 복잡한 용도의 리포지터리 메서드들을 모아놓는 용도를 구분할 수 있다고 생각해서이다.  

  

### EmpRepository

> Data JPA 기반의 리포지터리를 만들어보자~ 별 내용없다. 

```kotlin
package io.chart.lognomy.temporary

import org.springframework.data.jpa.repository.JpaRepository

interface EmpRepository : JpaRepository<Employee, Long>{
}
```

  

### QEmpRepository

> QueryDsl 기반의 리포지터리이다.  

```kotlin
package io.chart.lognomy.temporary

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import javax.persistence.EntityManager

@Repository
class QEmpRepository(
        val entityManager: EntityManager,
        val jpaQueryFactory: JPAQueryFactory
) {
    fun selectAllEmployees(): List<Employee> {

        val empList : List<Employee> = jpaQueryFactory.selectFrom(QEmployee.employee)
                .fetch();

        return empList
    }
}
```

  

## Service

Service 까지 작성할 필요는 굳이 없지만, 초기 세팅을 테스트한다고 하면, Service에서 Repository를 제대로 불러오는지를 확인해보는것도 괜찮지 않을까 싶어 기왕 하는 김에 Service 코드 역시 작성했다.

  

### EmpService

> Service의 동작을 추상화한 인터페이스이다.  

```kotlin
package io.chart.lognomy.temporary

interface EmpService {
    fun selectAllEmployees(): List<Employee>
}
```

  

### EmpServiceImpl

> Service의 동작을 구현했다.  

```kotlin
package io.chart.lognomy.temporary

import org.springframework.stereotype.Service

@Service
class EmpServiceImpl (val qEmpRepository: QEmpRepository) : EmpService{
    override fun selectAllEmployees(): List<Employee> {
        return qEmpRepository.selectAllEmployees()
    }
}
```

  

# 테스트 코드 작성하기

테스트를 돌려보자~ 잘 된다~ 야호~
```kotlin
package io.chart.lognomy.temporary

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import javax.persistence.EntityManager

@ActiveProfiles("testdocker")
@Transactional
@SpringBootTest
class EmployeeTest {

    @Autowired
    private lateinit var empService: EmpService

    @Autowired
    private lateinit var entityManager: EntityManager

    @Autowired
    private lateinit var empRepository: EmpRepository

    @Autowired
    private lateinit var qEmpRepository: QEmpRepository

    @BeforeEach
    fun setup(){
        val jordan = Employee(1, "Jordan")
        empRepository.save(jordan)

        val police1 = Employee(2, "경찰관#1")
        empRepository.save(police1)

        val firefighter1 = Employee(3, "소방관#1")
        empRepository.save(firefighter1)
    }

    @Test
    @DisplayName("QueryDsl 설정 제대로 잡혔나 확인하는 테스트 코드 #1")
    fun testQuerydslConfigurationRepository(){
        val employees = qEmpRepository.selectAllEmployees()
        assertThat(employees.size).isEqualTo(3)
    }

    @Test
    @DisplayName("QueryDsl 설정 제대로 잡혔나 확인하는 테스트 코드 #2")
    fun testQuerydslConfigurationService(){
        val employees = empService.selectAllEmployees()
        assertThat(employees.size).isEqualTo(3)
    }

}
```

  

# application.yml

설마 설마이지만... 누군가가 내 블로그에 와서 application.yml 을 어떻게 작성했나요? 하고 물어볼꺼라는 생각에 그냥 남겨본다... 설마 정말 application.yml 을 짜는 방법을 몰라서 블로그에 물어볼 정도인 사람이 있을까? 하는 생각이 들지만 ...  

개인적인 취향이지만 로컬에서는 테스트 코드이든, WAS 코드이든 무조건 testdocker 프로필을 사용하는 편이다.  

prod 프로필의 경우 aws의 ip 주소, rds url 등이 있는 관계로 프로젝트 최상단에 아래의 내용을 추가해주었고, PropertySource Configuration 코드를 추가해 스프링 구동시 로드되도록 설정해놓은 상태이다..  

(프로퍼티를 분리한 방식은 추후 정리할 예정이다. 내용이 그리 많은 편은 아니지만..)  

- conn.rds.url
- conn.rds.port
- conn.rds.user
- conn.rds.jdbcurl
- conn.rds.password
- conn.rds.driver-class-name

  

**application.yml**  

```yaml
spring:
  profiles:
    active: testdocker
#    active: prod
---
spring:
  profiles: local
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
        # show_sql: true     # system.out 으로 콘솔에 출력된다. (추천하지 않는 옵션이다.)
        # logging.level.org.hibernate.SQL을 debug로 두면 로거를 사용해 SQL이 출력된다.
        # system.out 대신 logger를 쓰고 싶다면 spring.jpa.hibernate.properties.show_sql 은 사용하지 말자.
        format_sql: true
        use_sql_comments: true

logging.level:
  org.hibernate.SQL: debug
# org.hibernate.type: trace
---
spring:
  profiles: testdocker
  datasource:
    url: jdbc:mariadb://localhost:23307/lognomy
    username: root
    password: 1111

    hikari:
      auto-commit: true
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
logging.level:
  org.hibernate.SQL: debug
---
spring:
  profiles: prod
  #  prod 의 datasource 는 프로젝트 최상단 connection-info.properties 에 지정했고, .gitignore 에 등록되어 있다.
  #  datasource:
  #    url: ${conn.rds.jdbcurl}
  #    username: ${conn.rds.username}
  #    password: ${conn.rds.password}
  #    driver-class-name: ${conn.rds.driver-class-name}
  jpa:
    hibernate:
      ddl-auto: none
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

# docker mariadb 쉘스크립트
그냥 추가로 docker mariadb 쉘스크립트를 추가해보았다. 추후 윈도우 기반 개발PC를 사용하게 된다면 bat 파일을 만들어야 할듯 하다.  

지금은 개발용 PC가 맥인 관계로... 쉘스크립트로 추가했다.    

github에 원본소스가 있다. 구동시킬때 ^M 뭐시기 에러문구 나오면 Line Separator 문제이다. CRLF로 되어있다면 LF로 지정후 실행해줘야 한다.  

누가 질문하면 지워버릴까 생각중이다.  
## docker-mariadb-start.sh
```bash
#!/bin/zsh

name_lognomy_mysql='lognomy-mariadb'
cnt_lognomy_mysql=`docker container ls --filter name=lognomy-mariadb | wc -l`
cnt_lognomy_mysql=$(($cnt_lognomy_mysql -1))

if [ $cnt_lognomy_mysql -eq 0 ]
then
    echo "'$name_lognomy_mysql' 컨테이너를 구동시킵니다.\n"

    # 디렉터리 존재 여부 체크 후 없으면 새로 생성
    DIRECTORY=~$USER/env/docker/lognomy/volumes/lognomy-mariadb
    test -f $DIRECTORY && echo "볼륨 디렉터리가 존재하지 않으므로 새로 생성합니다.\n"

    if [ $? -lt 1 ]; then
      mkdir -p ~$USER/env/docker/lognomy/volumes/lognomy-mariadb
    fi

    # mariadb 컨테이너 구동 & 볼륨 마운트
    docker container run --rm -d -p 23307:3306 --name lognomy-mariadb \
                -v ~/env/docker/lognomy/volumes/lognomy-mariadb:/var/lib/mysql \
                -e MYSQL_ROOT_PASSWORD=1111 \
                -e MYSQL_DATABASE=lognomy \
                -e MYSQL_USER=testuser \
                -e MYSQL_PASSWORD=1111 \
                -d mariadb:latest \
                --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci

else
    echo "'$name_lognomy_mysql' 컨테이너가 존재합니다. 기존 컨테이너를 중지하고 삭제합니다."
    # 컨테이너 중지 & 삭제
    docker container stop lognomy-mariadb

    # 컨테이너 볼륨 삭제
    rm -rf ~/env/docker/lognomy/volumes/lognomy-mariadb/*
    echo "\n'$name_lognomy_mysql' 컨테이너 삭제를 완료했습니다.\n"

    # 디렉터리 존재 여부 체크 후 없으면 새로 생성
    DIRECTORY=~$USER/env/docker/lognomy/volumes/lognomy-mariadb
    test -f $DIRECTORY && echo "볼륨 디렉터리가 존재하지 않으므로 새로 생성합니다.\n"

    if [ $? -lt 1 ]; then
      mkdir -p ~$USER/env/docker/lognomy/volumes/lognomy-mariadb
    fi

    # mariadb 컨테이너 구동 & 볼륨 마운트
    echo "'$name_lognomy_mysql' 컨테이너를 구동시킵니다."
    docker container run --rm -d -p 23307:3306 --name lognomy-mariadb \
                -v ~/env/docker/lognomy/volumes/lognomy-mariadb:/var/lib/mysql \
                -e MYSQL_ROOT_PASSWORD=1111 \
                -e MYSQL_DATABASE=lognomy \
                -e MYSQL_USER=testuser \
                -e MYSQL_PASSWORD=1111 \
                -d mariadb:latest \
                --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
fi
```
  

## docker-mariadb-ls.sh

```bash
#!/bin/zsh

docker container ls --filter name=lognomy-mariadb
```
  

## docker-mariadb-stop.sh

```bash
#!/bin/zsh

name_lognomy_mysql='lognomy-mariadb'

cnt_lognomy_mysql=`docker container ls --filter name=lognomy-mariadb | wc -l`
cnt_lognomy_mysql=$(($cnt_lognomy_mysql -1))

if [ $cnt_lognomy_mysql -eq 0 ]
then
    echo "'$name_lognomy_mysql' 컨테이너가 없습니다. 삭제를 진행하지 않습니다."

else
    echo "'$name_lognomy_mysql' 컨테이너가 존재합니다. 기존 컨테이너를 중지하고 삭제합니다."
    docker container stop lognomy-mariadb
    rm -rf ~/env/docker/lognomy/volumes/lognomy-mariadb/*
    echo "\n'$name_lognomy_mysql' 컨테이너 삭제를 완료했습니다.\n"
fi
```