# ElasticSearch 기본개념 (1)

예전에도 한번 정리했었다. 하지만 그때 정리했던 자료들은 inflearn, 블로그의 글들만 보고 정리를 했는데, 매핑 도중에 나타나는 에러들(주로 6.x -> 7.x 이슈)로 인해 시간이 많이 들었다. 해당 교육자료가 많이 나왔을때가 Elasticsearch 6.x 시절이었고, 지금은 무려 7.8.x 버전 대이다.   

그리고, 기본적인 개념 및 용어들도 내식대로 정리할 필요가 있겠다는 필요성을 느꼈다.  

이런 이유로 ElasticSearch 7.x 버전대에서 6.x 버전대의 기본 명령어들을 모두 실행해보고 달라진 점들을 기록해보고자 한다. 정말 길게 늘여서 쓰고 Cmd + F / Ctrl + F 기능으로 개념들을 찾아서 볼 생각이다. 여러가지 개념으로 챕터를 나누어 두는 것보다 이게 더 나중에 찾아보기 쉽지 않을까 하는 생각이다.   

책을 읽으면서 공부를 처음부터 한단계씩 하다보니 6.x 와 7.x버전 사이에서 가장 크게 달라진 점은 6.x 버전에서 7.x버전으로 업데이트 되면서 타입이라는 개념이 Deprecated 된 것이었다.  

> - 6.x 에서는 인덱스 내에 타입을 따로 둘수 있었다.
> - 7.x에서는 타입 관련 연산들이 Deprecated 되어 사용은 가능하지만, 새롭게 바꾸라고 Deprecated 경고문구를 띄우거나, 아예 타입관련해서 없어진 기능 또한 생겨났다.



# 1. 참고 자료들

- [ElasticSearch 7.x 매핑](https://www.elastic.co/guide/en/elasticsearch/reference/7.x/mapping.html)
- [지극히 개인적인 연구소(텍스트 기반으로 잘 정리되어 있다. 괜춘한 자료)](https://m.blog.naver.com/PostView.nhn?blogId=slykid&logNo=221559872279&proxyReferer=https:%2F%2Fwww.google.com%2F)

계속 추가 예정



# 2. 기본 용어 및 개념 정리

## 7.x 에서 DEPRECATED 된 연산

### PUT /index/type/id

type을 지정해서 INSERT하는 방식은 DEPRECATED 되었다고 한다. 7.x 에서는 type이라는 개념 자체가 없어진 듯 해보인다.

```
#! Deprecation: [types removal] Specifying types in document index requests is deprecated, use the typeless endpoints instead (/{index}/_doc/{id}, /{index}/_doc, or /{index}/_create/{id}).
```



## 7.x 에서 타입(type)/매핑의 개념의 변화

- 7.x 에서는 타입(type)이라는 개념이 없어졌다. 신기하게도 _doc라는 개념을 사용한다.
- 하나의 index 에 생성한 모든 데이터들은 _doc 라는 type 으로관리된다.
- 인덱스 안에 타입을 여러개 두는 것이 불가능해졌다.
- 테이블 같은 개념은 이제 인덱스가 모두 관리하는것이 되었고, 인덱스는 스키마의 역할을 하는 것은 아닌듯해보인다.  
- RDB랑 비교해서 개념파악하기에는 더 어려워진듯 하다.  



## 인덱스/타입/도큐먼트

- 인덱스
  - 관계형 DB의 스키마와 같은 역할을 한다.
  - 6.x 버전까지는 관계형 DB의 스키마와 같은 역할을 했지만 
  - 7.x 버전부터는 인덱스가 테이블의 역할을 하는 듯한 느낌이다.
- 타입
  - 6.x 까지만 지원되는 개념이다.
  - 현존 상용 서비스들은 6.x 기반 대에서 개발된 것들이 많을 것이기 때문에 6.x의 명령어도 동시에 알고 있는 것이 도움이 된다.
  - 현업에서는 아마도, Elastic 사의 지원을 받아 마이그레이션 작업을 하지 않을까 하는 생각이 든다.
- 도큐먼트
  - 실제 insert 되는 데이터들을 의미한다.
  - 관계형 DB의 개념으로 보면 row 의 개념이다.

  

## 인덱싱/색인 연산 (Indexing Operation)

> 인덱스 내에서 **도큐먼트를 추가**하거나, **생성**하는 작업  
>
> 7.x 부터는 타입이라는 개념이 없어졌기 때문에 도큐먼트는 인덱스 내에 추가하게 된다.   
>
> 6.x 에서는 인덱스 내에 타입을 생성해서 도큐먼트를 추가했었다.  

즉, **데이터를 Insert/Create 하는 작업**이라고 이해하면 한방에 정리될 것 같다.

  

## 동적 매핑

> 인덱스와 도큐먼트를 한번에 INSERT시 도큐먼트가 아직 존재하지 않을 경우 필드의 데이터 타입을 추론한다. 이것을 동적 매핑이라고 한다.  

  

# 3. 인덱스 추가 연산

> 인덱스 내에서 **도큐먼트를 추가하거나 생성하는 작업**을 **인덱싱/색인 연산**(Indexing Operation)이라고 부른다.

## 인덱스를 추가하는 다양한 방법들

- ID를 지정해 도큐먼트 색인하기
  - 요청형식 : PUT /\<index\>/\<type\>/\<id\>
- 인덱스 생성시 샤드 및 복제본 갯수를 지정하기 (82p)
- ID를 제공하지 않고 도큐먼트 색인하기
  - 도큐먼트의 ID 생성을 제어하지 않으려면 POST 메서드를 사용하면 된다.
  - 요청형식 : POST /\<index\>/\<type\>



## 3.1. ID를 지정해 도큐먼트 색인하기

### 1) 예제 데이터 insert (1)

아이디를 지정해 도큐먼트 색인하기 (Document Indexing)

6.x 명령어 (키바나 콘솔)

```
PUT /catalog/product/1
{
  "sku": "SP000001",
  "title": "ElasticSearch for Hadoop",
  "description": "ElasticSearch for Hadoop",
  "author": "Vishal Shukla",
  "ISBN": "1785288997",
  "price": 26.99
}
```



7.x 명령어  

```
PUT /catalog/1
{
  "sku": "SP000001",
  "title": "ElasticSearch for Hadoop",
  "description": "ElasticSearch for Hadoop",
  "author": "Vishal Shukla",
  "ISBN": "1785288997",
  "price": 26.99
}
```



curl  

```bash
curl -XPUT http://localhost:9200/catalog/product/1 -H 'Content-Type: application/json' -d '{"sku": "SP000001", "title": "ElasticSearch for Hadoop", "description": "Elasticsearch for Hadoop" "author": "Vishal Shukla", "ISBN": "1785288997", "price": 26.99}'
```



### 2) 예제 데이터 insert (2)

6.x

```
PUT /catalog/product/2
{
  "sku": "SP000002",
  "title": "Google Pixel Phone 32GB - 5 inch display",
  "description": "Google Pixel Phone 32GB - 5 inch display (Factory Unlocked US Version)",
  "price": 26.99,
  "resolution": "1440 x 2560 pixels",
  "os": "Android 7.1"
}
```



7.x

```
PUT /catalog/2
{
  "sku": "SP000002",
  "title": "Google Pixel Phone 32GB - 5 inch display",
  "description": "Google Pixel Phone 32GB - 5 inch display (Factory Unlocked US Version)",
  "price": 26.99,
  "resolution": "1440 x 2560 pixels",
  "os": "Android 7.1"
}
```



## 3.2. 인덱스 생성시 샤드 및 복제본 갯수를 지정하기 (82p)

```
PUT /catalog
{
  "settings":{
    "index":{
      "number_of_shards": 5,
      "number_of_replicas": 2
    }
  }
}
```



## 3.3 ID를 제공하지 않고 도큐먼트 색인하기

- 도큐먼트의 ID 생성을 제어하지 않으려면 POST 메서드를 통해 데이터를 INSERT하면 된다.

- 요청형식 : POST/[index]/[type]

**6.x)**  

```
POST /catalog/product
{
  "sku": "SP000003",
  "title": "Mastering ElasticSearch",
  "description": "Mastering ElasticSearch",
  "author": "Bharvi Dixit",
  "price": 54.99
}
```



출력결과)

```
#! Deprecation: [types removal] Specifying types in document index requests is deprecated, use the typeless endpoints instead (/{index}/_doc/{id}, /{index}/_doc, or /{index}/_create/{id}).
{
  "_index" : "catalog",
  "_type" : "product",
  "_id" : "HnMMOnQB0026QqLvzmLm",
  "_version" : 1,
  "result" : "created",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 2,
  "_primary_term" : 1
}
```



7.x 에서의 명령

```bash
POST /catalog
{
  "sku": "SP000003",
  "title": "Mastering ElasticSearch",
  "description": "Mastering ElasticSearch",
  "author": "Bharvi Dixit",
  "price": 54.99
}
```



