# Spring 과 ElasticSearch 7.x 연동하기

> Spring 에서 Elastic Search 서버 인스턴스를 연결해 Spring Data Elasticsearch 를 통해 Database 연산을 테스트 코드로 실행해보는 간단한 예제를 정리해봤다.

오늘 정리하는 예제는 [velog - spring data elasticsearch](https://velog.io/@gosgjung/chartnomy-ElasticSearch-내의-KOSPI-경제지표-조회-feat.-Spring-Data-ElasticSearch) 에 정리했던 내용이다. [velog](https://velog.io/@gosgjung)를 이제 당분간은 사용하지 않을 생각이라서.. gitbook 으로 문서를 옮기는 중이다. 그런데 내용을 조금 더 다듬을 필요가 있을것 같기도 하고, 한동안 이 개념 정리하는 것을 까먹고 있기도 했따. 이런 이유로 문서 정리 작업이 ... 지연되고 있었다. 



# 구현목표 

오늘 정리할 예제의 구현 목표는 이렇다. 

- 의존성 추가
  - Spring Boot 환경에 `spring-boot-starter-data-elasticsearch` 의존성 추가
- High Level Client 설정 
  - Elastic Search 의 접속정보 연결
- JPA를 활용한 쿼리실행 
  - JUnit 테스트 코드 기반으로 간단한 selectAll 구문 호출하는 테스트 코드 작성

처음 계획은 이렇게 간단했지만, 막상 정리하다보니 내용이 길어졌다...



# 참고자료‌

**접속정보 설정시 참고한 자료**   ‌

- [Stack Over Flow - Java 애플리케이션이 Elastic Search 서버 인스턴스에 접속하지 못하는 문제](https://stackoverflow.com/questions/62552911/unable-to-connect-my-java-application-to-the-elastic-stack-instance)  

**High Level Rest Client 관련 참고한 자료들**  

- [docs.spring.io - elasticsearch.clients.rest](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.clients.rest)
- [High Level REST Client - docs.spring.io](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.clients.rest)
- [Elastic Search Client Configuration 에 대한 세부적인 설명](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.clients.configuration) 

**Unknown Host Exception 에러 발생 관련해서 참고한 자료**  

- 검색어 : `search.core.ElasticsearchRestTemplate.translateException`
- [unknown host exception using java client](https://discuss.elastic.co/t/java-net-unknownhostexception-using-java-rest-client-5-6-3/106329)
- 이외의 자료들
- https://discuss.elastic.co/t/java-net-unknownhostexception-using-java-rest-client-5-6-3/106329/17
- https://discuss.elastic.co/t/java-net-unknownhostexception-using-java-rest-client-5-6-3/106329/18
- https://discuss.kotlinlang.org/t/how-to-call-string-getbytes/2152
- 참고)
  - [Cluster State API - cluster name 을 얻어오는 방식에 대한 설명 역시 확인 가능하다.](https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-state.html)  

**Entity**  

- [doc.spring.io - elasticsearch.mapping.meta-model.annotations](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.mapping.meta-model.annotations)

**ISO Format**  

- [mapping data format](https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-date-format.html)

‌

# Elastic Cloud 접속정보 설정‌

connection.properties 라는 파일을 생성해서 이곳에 모든 외부 서버의 접속정보들을 모아두었다. 이 connection.properties 파일은 .gitignore 에 등록해두었다. 소스 상의 위치는 아래와 같다. 문자열로 접속정보들을 직접 입력해주어도 되지만, 나중에 재사용성이 너무 떨어져서... connection.properties 파일에 모아두었다.  



![이미지](./img/1.png)

‌

## (Optional) connection.properties‌

### 참고자료‌

- [Stack Over Flow - Java 애플리케이션이 Elastic Search 서버 인스턴스에 접속하지 못하는 문제](https://stackoverflow.com/questions/62552911/unable-to-connect-my-java-application-to-the-elastic-stack-instance)  

아래의 connection.properties 파일의 내용에서 보듯이 conn.elasticsearch.host 를 세팅할 때 `http://` 또는 `https://` 를 제거해주어야 한다. 예를 들면 아래와 같은 형식이다.

> conn.elasticsearch.host=f765633ef8c84c4a9fb0b0f8f24ad84d.ap-northeast-2.aws.elastic-cloud.com

  

```properties
# rds 접속 정보 등등…

# TODO 정리 필요 https:// 또는 http:// 를 제거해야 한다.
# https://stackoverflow.com/questions/62552911/unable-to-connect-my-java-application-to-the-elastic-stack-instance

# 이건 잘못된 주소 형식이다. https:// 를 제거하자
# conn.elasticsearch.host=https://f765633ef8c84c4a9fb0b0f8f24ad84d.ap-northeast-2.aws.elastic-cloud.com
# http:// 를 제거한 문자열을 java config 시 사용해야 한다. (ElasticSearch 라이브러리 내부에서 통용되는 형식이라 조금 어쩔수 없는 부분)
conn.elasticsearch.host=f765633ef8c84c4a9fb0b0f8f24ad84d.ap-northeast-2.aws.elastic-cloud.com

conn.elasticsearch.port=9243
conn.elasticsearch.cluster_name=a2580127b5fa47a783245a38d16c6a76
conn.elasticsearch.username=elastic
conn.elasticsearch.password=ZMDnxlVzyQkGQwrYDOh9nkl9


spring.main.allow-bean-definition-overriding=true
```



위의 connection.properties 의 내용들중에 elastic search에 관련된 항목들만을 정리해보면 아래와 같다.

- conn.elasticsearch.host
- conn.elasticsearch.port
- conn.elasticsearch.cluster_name
- conn.elasticsearch.username
- conn.elasticsearch.password

이 항목들은 반드시 위와 같은 이름으로 properties에 지정할 필요는 없다. 텍스트로 소스상에 지정해도 되고, 원하는 다른 이름의 key 값으로 지정해주어도 된다.  



## (Optional) PropertySourceConfig

스프링 로딩시 `src/main/resources` 밑의 connection.properties 파일을 읽어들이도록 아래와 같이 명시적으로 설정해두었다.

```kotlin
package io.chart.lognomy.config


import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource


@Configuration
@PropertySource("classpath:/connection.properties")
class PropertySourceConfig {
}
```



# 의존성 추가

의존성으로는 `spring-boot-starter-data-elasticsearch` 를 추가해주었다. 일반적인 spring data jpa 에서 지원하는 @Id, @Table 과는 다른 개념의 매핑(ex. @Document, @Field ) 이 존재해서인지 `spring-boot-starter-data-elasticsearch` 라는 라이브러리가 따로 존재한다.

```groovy
implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch:2.3.4.RELEASE")
```



# High Level Client 설정

## 참고자료 

- [docs.spring.io - elasticsearch.clients.rest](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.clients.rest)
- [Elastic Search Client Configuration 에 대한 세부적인 설명](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.clients.configuration) 
- Unknown Host Exception 에러 발생 관련해서 참고한 자료
  - 검색어 : `search.core.ElasticsearchRestTemplate.translateException`
  - [unknown host exception using java client](https://discuss.elastic.co/t/java-net-unknownhostexception-using-java-rest-client-5-6-3/106329)
  - 이외의 자료들
  - https://discuss.elastic.co/t/java-net-unknownhostexception-using-java-rest-client-5-6-3/106329/17
  - https://discuss.elastic.co/t/java-net-unknownhostexception-using-java-rest-client-5-6-3/106329/18
  - https://discuss.kotlinlang.org/t/how-to-call-string-getbytes/2152
- 참고)
  - [Cluster State API - cluster name 을 얻어오는 방식에 대한 설명 역시 확인 가능하다.](https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-state.html)



## ElasticsearchClientConfiguration.kt

Elastic Search 를 설정하는 파일은 아래와 같다. 아직은 단순 설정만 적용된 상태이긴 하지만, 추후 Reactive Programming 방식으로 변경 예정이다.

```kotlin
package io.chart.lognomy.config


import org.apache.http.Header
import org.apache.http.message.BasicHeader
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.beans.factory.annotation.Value


import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.RestClients
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories
import org.springframework.http.HttpHeaders
import java.time.Duration
import java.util.*


/*
    TODO 정리 필요
    cluster name 을 얻어오는 방식은 https://www.elastic.co/guide/en/elasticsearch/reference/current/cluster-state.html 에 자세히 설명되어 있다.
    이 중 curl -X GET "localhost:9200/_cluster/state/blocks?pretty" 을 선택했다.
 */


//@EnableElasticsearchRepositories
@Configuration
class ElasticsearchClientConfiguration (
        @Value("\${conn.elasticsearch.host}") val host: String,
        @Value("\${conn.elasticsearch.port}") val port: String,
        @Value("\${conn.elasticsearch.cluster_name}") val clusterName: String,
        @Value("\${conn.elasticsearch.username}") val username: String,
        @Value("\${conn.elasticsearch.password}") val password: String
) : AbstractElasticsearchConfiguration() {


    // AbstractElasticsearchConfiguration 클래스 내에 abstract 메서드에서, 반드시 implements 해야 한다.
    // 이런점은 확실히 마음에 든다.
    @Bean
    override fun elasticsearchClient(): RestHighLevelClient {
        val hostAndPort = "$host:$port"


        // java.net.UnknownHostException elasticsearch 검색내용
        // https://discuss.elastic.co/t/java-net-unknownhostexception-using-java-rest-client-5-6-3/106329/17
        // https://discuss.elastic.co/t/java-net-unknownhostexception-using-java-rest-client-5-6-3/106329/18
        // https://discuss.kotlinlang.org/t/how-to-call-string-getbytes/2152
        val encodedBytes : String = Base64.getEncoder().encodeToString("$username:$password".toByteArray())
//        val headers = mutableListOf<Header>()
//        headers.add(BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"))
//        headers.add(BasicHeader("Authorization", "Basic $encodedBytes"))


        val httpHeaders: HttpHeaders = HttpHeaders()
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Authorization", "Basic $encodedBytes")


        val clientConfiguration : ClientConfiguration = ClientConfiguration.builder()
                .connectedTo(hostAndPort)
                .usingSsl()
                .withConnectTimeout(Duration.ofSeconds(5))
                .withSocketTimeout(Duration.ofSeconds(5))
                .withDefaultHeaders(httpHeaders)
//                .withBasicAuth(username, password)
//                .usingSsl()
//                .withProxy("asdf")
//                등등 굉장히 많은 설정이 있다.
//                자세한 내용은 아래 링크 참고
//                https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.clients.configuration
                .build()


        return RestClients.create(clientConfiguration).rest()
    }

}
```

  

# Entity

> 참고자료
>
> - [doc.spring.io - elasticsearch.mapping.meta-model.annotations](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.mapping.meta-model.annotations)
> - [ISO Format 관련 자료 - mapping data format](https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-date-format.html)



## 엔티티 매핑‌

@Field 안에서 format, pattern 이 어떤 Java의 어떤 데이터 타입인지 공식문서에 나와있지 않아서 잠깐 막막했었다. 그래서 @Field 어노테이션 내부구현을 참고해본 결과 아래와 같은 요소들을 사용해서 구현하게 되었다.‌

- format() : DateFormat
  - org.springframework.data.elasticsearch.annotations.DateFormat
  - 이 DateFormat 은 enum 타입이다.
- pattern() : String‌

날짜 format, pattern 을 지정해주는 @Field 내부 구현을 살펴보자

![이미지](./img/2.png)

  

![이미지](./img/3.png)

  

## 샘플 데이터 엔티티 매핑

샘플 데이터 엔티티 매핑은 한국은행 경제 통계시스템에서 제공해주는 코스피 데이터를 기준으로 했다.

> 참고자료 : 
>
> - [High Level REST Client - docs.spring.io](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.clients.rest)

  

**Kospi.kt**

```kotlin
package io.chart.lognomy.indicators.kospi

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.DateFormat
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.LocalDateTime


// 참고자료
// https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#elasticsearch.clients.rest


@Document(indexName = "kospi", createIndex = false)
data class Kospi(
        @Id @Field(name = "TIME", type = FieldType.Date, pattern = "yyyy-MM-dd'T'hh:mm:ss", format = DateFormat.date_hour_minute_second)
        val time : LocalDateTime,


        @Field(name = "DATA_VALUE", type = FieldType.Text) val value: String,
        @Field(name = "ITEM_CODE1", type = FieldType.Text) val itemCodeOne : String,
        @Field(name = "ITEM_CODE2", type = FieldType.Text) val itemCodeTwo : String,
        @Field(name = "ITEM_CODE3", type = FieldType.Text) val itemCodeThree : String,
        @Field(name = "ITEM_NAME1", type = FieldType.Text) val itemNameOne : String,
        @Field(name = "ITEM_NAME2", type = FieldType.Text) val itemNameTwo : String,
        @Field(name = "ITEM_NAME3", type = FieldType.Text) val itemNameThree : String,
        @Field(name = "STAT_NAME", type = FieldType.Text) val statName : String,
        @Field(name = "STAT_CODE", type = FieldType.Text) val statCode : String,
        @Field(name = "UNIT_NAME", type = FieldType.Text) val unitName : String
)
```



# Repository, Service

Repository, Service의 내용은 Elastic Search의 내용을 참고할 내용이 많지 않기 때문에 크게 어렵거나 막히는 내용은 없을듯 하다.



## Repository‌

**KospiRepository.kt**

```kotlin
package io.chart.lognomy.indicators.kospi

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.data.repository.Repository

import java.time.LocalDateTime

// 일부 인터넷 자료를 보면 아래 주석친 부분과 같은 내용들이 나오는데, 현재 시점의 최신버전인 ElasticSearch 7.x 에서는 통하지 않는다.
// Jackson 등과 같은 라이브러리 지원 등을 제거하면서 대폭 바뀌었기 때문이다.
//@Repository("kospiRepository")
//interface KospiRepository : ElasticsearchRepository<Kospi, LocalDateTime>{

// 이게 정상적으로 동작하는 코드이다. 빌트인(??)으로 제공되는 spring data jpa 를 가져다 쓰면 된다.
interface KospiRepository : Repository<Kospi, LocalDateTime> {
    fun findAllBy(): List<Kospi>
}
```

‌

## Service

**KospiService.kt**

```kotlin
package io.chart.lognomy.indicators.kospi

import org.springframework.stereotype.Service

@Service
class KospiService (
        val kospiRepository: KospiRepository
){
    public fun findAll(): List<Kospi> {
        return kospiRepository.findAllBy().toList()
    }
}
```



# 테스트 코드

## KospiTest.kt

```kotlin
package io.chart.lognomy.indicators.kospi


import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class KospiTest {


    lateinit var kospiService: KospiService


    @Qualifier("kospiRepository")
    @Autowired
    lateinit var kospiRepository: KospiRepository


    @BeforeEach
    fun setup() : Unit{
        kospiService = KospiService(kospiRepository)
    }


    @Test
    @DisplayName("findAll")
    fun testFindAll() : Unit{
        val findAll = kospiService.findAll()
        println(findAll)
    }
}
```



## 출력결과

잘 된다~ 😅

![이미지](./img/4.png)



# 마치면서 💾

아직 ELK를 실무에서 사용해본 경험은 없다. 전 회사에서 다뤄본 에너지 모니터링 시스템의 데이터를 빠르게 처리하는 것을 더 빠르게 처리할 수 있는 시스템을 만들어보는게 나의 호기심영역이다. 이런 이유로... 실제 운영되는 에너지 데이터는 일반인의 신분으로 구할 수는 없기 때문에 에너지 데이터처럼 시간축을 기반으로 하는 경제지표 데이터를 구하게 되었다. 경제지표 데이터 (미국 기준금리, 한국 기준금리, 원/달러 환율, 위안화/달러 환율)에 대해 여러가지 시도를 chartnomy 프로젝트를 진행해보면서 여러가지 기술들을 접해보면서 내가 가진 기술력을 스케일업해나갈 생각이다.  

이런 생각으로 제일 처음으로 관심을 가지게 된 기술은 Elastic Search 였다. Index를 생성하고 관리하는 것부터 덤프 Data 를 밀어넣는 것, Spring 에서 조회하는 것까지... 여러가지로 공부할 게 많지만, 뭔가 게임을 해나가는 과정과 같은 기분이어서 재미있기는 하다. 뭔가 퀘스트가 있는 게임을 하는 기분이다. 그런데... 가끔은 등산도 하고 컴퓨터를 끄고 나가서 땀을 흘리는 시간도 가져야 할것 같다. 등산을 너무 하고 싶다.   