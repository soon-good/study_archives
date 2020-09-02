# ElasticSearch 기본개념 (1)

예전에도 한번 정리했었다. 하지만 그때 정리했던 자료들은 inflearn, 블로그의 글들만 보고 정리를 했는데, 매핑 도중에 나타나는 에러들(주로 6.x -> 7.x 이슈)로 인해 시간이 많이 들었다. 해당 교육자료가 많이 나왔을때가 Elasticsearch 6.x 시절이었고, 지금은 무려 7.8.x 버전 대이다.   

그리고, 기본적인 개념 및 용어들도 내식대로 정리할 필요가 있겠다는 필요성을 느꼈다.  

이런 이유로 ElasticSearch 7.x 버전대에서 6.x 버전대의 기본 명령어들을 모두 실행해보고 달라진 점들을 기록해보고자 한다. 정말 길게 늘여서 쓰고 Cmd + F / Ctrl + F 기능으로 개념들을 찾아서 볼 생각이다. 여러가지 개념으로 챕터를 나누어 두는 것보다 이게 더 나중에 찾아보기 쉽지 않을까 하는 생각이다.   

책을 읽으면서 공부를 처음부터 한단계씩 하다보니 6.x 와 7.x버전 사이에서 가장 크게 달라진 점은 6.x 버전에서 7.x버전으로 업데이트 되면서 타입이라는 개념이 Deprecated 된 것이었다.    

> - 6.x 에서는 인덱스 내에 타입을 따로 둘수 있었다.
> - 7.x에서는 타입 관련 연산들이 Deprecated 되어 사용은 가능하지만, 새롭게 바꾸라고 Deprecated 경고문구를 띄우거나, 아예 타입관련해서 없어진 기능 또한 생겨났다.  

  

## 학습목표

사실 Spring과 Data JPA 를 사용한다면, ElasticSearch의 쿼리를 자세히 공부할 필요는 없을수도 있겠다는 생각이 들었다. 그런데 직접 증권데이터들을 수집해서 ElasticSearch 서버에 저장해두고 조회용도로 쓰고자 하니... 인덱스/타입/도큐먼트/샤드 등등 여러가지의 기본개념들에 대해 이해하고 넘어가야 할 필요성을 느꼈었다.  

여기서는 실제로 ElasticSearch를 통해 데이터를 저장해서 매핑을 직접 해보는 과정들을 정리해보려고 한다. 그 과정에서 부딪혔던 6.x, 7.x 의 차이점들 또한 하나 하나 정리해나갈 예정이다.  



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



# 4. 조회 API

위에서 생성한 데이터를 기반으로 예제를 돌려보자.  

조회시에는  GET 메서드를 사용한다.  

6.x) 이 명령은 7.x 에서는 Deprecated 되었다.  

```
GET /catalog/product/HnMMOnQB0026QqLvzmLm
```

7.x) 

```
GET /catalog/_doc/1
```

출력결과

```
{
  "_index" : "catalog",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 2,
  "_seq_no" : 3,
  "_primary_term" : 1,
  "found" : true,
  "_source" : {
    "sku" : "SP000001",
    "title" : "ElasticSearch for Hadoop",
    "description" : "ElasticSearch for Hadoop",
    "author" : "Vishal Shukla",
    "ISBN" : "1785288997",
    "price" : "39.21"
  }
}
```



# 5. 업데이트 API

## 주요 개념

- 업데이트 API는 기존 도큐먼트를 ID로 업데이트하는 데에 유용하다.
- 일래스틱서치는 내부적으로각 도큐먼트의 버전을 관리한다. 도큐먼트가 변경될 때마다 버전 번호는 증가한다.
- 부분 업데이트(업데이트 API(1) 참고)는 도큐먼트가 미리 존재하는 경우에만 동작한다.
  - 주어진 ID를 가진 도큐먼트가 없는 경우, 일래스틱 서치는 도큐먼트가 없다는 오류를 반환한다.
- 일래스틱 서치에서는 upsert 를 할 수 있다.
- upsert 는 update 또는 insert 를 모두 할 수 있는 기능을 의미한다.
- doc_as_upsert 는 주어진 ID를 가진 도큐먼트가 이미 존재하는지 확인하고, 요청한 도큐먼트를 기존 도큐먼트와 병합한다.

- 주어진 ID를 가진 도큐먼트가 존재하지 않으면 주어진 도큐먼트 내용을 가진 새로운 도큐먼트를 삽입한다.



## 일반 업데이트 API

- 6.x) POST /catalog/product/1/_update
- 7.x) POST /catalog/_update/1



## upsert

- 6.x) POST /catalog/product/1/_update
- 7.x) POST /{index}/_update/1



## 업데이트 API 예제 (1)

### 6.x)  

```
POST /catalog/product/1/_update
{
  "doc":{
    "price": "39.39"
  }
}
```



 출력결과) 위의 6.x 대의 명령을 실행하면 아래와 같은 경고 문구가 나타난다.  

```
#! Deprecation: [types removal] Specifying types in document update requests is deprecated, use the endpoint /{index}/_update/{id} instead.
{
  "_index" : "catalog",
  "_type" : "product",
  "_id" : "1",
  "_version" : 3,
  "result" : "updated",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 4,
  "_primary_term" : 1
}
```



### 7.x)

```
POST /catalog/_update/1
{
  "doc":{
    "price": "39.3939"
  }
}
```



출력결과)

```
{
  "_index" : "catalog",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 4,
  "result" : "updated",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 5,
  "_primary_term" : 1
}
```



## 업데이트 API 예제 (2)

업데이트 API는 기존 도큐먼트를 ID로 업데이트하는 데에 유용하다.  
매개변수로 doc_as_upsert 를 이용해 ID가 3인 도큐먼트를 병합하거나, 존재하지 않는 다면 새로운 도큐먼트를 삽입한다.  

### 6.x)

```
POST /catalog/product/3/_update
{
  "doc":{
    "author": "Albert Paro",
    "title": "ElasticSearch 5.0 Cookbook",
    "description": "ElasticSearch 5.0 Cookbook Third Edition",
    "price": "54.99"
  },
  "doc_as_upsert": true
}
```



출력결과) 출력결과를 보면 역시나 DEPRECATED 된 명령어 형식이다.

```
#! Deprecation: [types removal] Specifying types in document update requests is deprecated, use the endpoint /{index}/_update/{id} instead.
{
  "_index" : "catalog",
  "_type" : "product",
  "_id" : "3",
  "_version" : 1,
  "result" : "created",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 6,
  "_primary_term" : 1
}
```



### 7.x)

> POST /{index}/_update/{id}

```
POST /catalog/_update/3
{
  "doc":{
    "author": "Albert Paro",
    "title": "ElasticSearch 5.0 Cookbook",
    "description": "ElasticSearch 5.0 Cookbook Third Edition",
    "price": "54.99"
  },
  "doc_as_upsert": true
}
```



출력결과

```
{
  "_index" : "catalog",
  "_type" : "_doc",
  "_id" : "3",
  "_version" : 1,
  "result" : "noop",
  "_shards" : {
    "total" : 0,
    "successful" : 0,
    "failed" : 0
  },
  "_seq_no" : 6,
  "_primary_term" : 1
}
```



## 업데이트 API 예제 (3)

스크립트를 활용해 특정 제품 가격을 2로 증가시키는 것 역시 가능하다.

### 6.x (DEPRECATED)

> POST /catalog/product/{id}/_update

현재 6.x 명령은 DEPRECATED 되었다.

```
POST /catalog/product/HnMMOnQB0026QqLvzmLm/_update
{
  "script":{
    "inline": "ctx._source.price += params.increment",
    "lang": "painless",
    "params": {
      "increment": 2
    }
  }
}
```

  

출력결과

```
출력결과
#! Deprecation: [types removal] Specifying types in document update requests is deprecated, use the endpoint /{index}/_update/{id} instead.
#! Deprecation: [script][3:15] Deprecated field [inline] used, expected [source] instead
{
  "_index" : "catalog",
  "_type" : "product",
  "_id" : "HnMMOnQB0026QqLvzmLm",
  "_version" : 2,
  "result" : "updated",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 7,
  "_primary_term" : 1
}
```



### 7.x

> POST /catalog/_update/{id}

```
POST /catalog/_update/HnMMOnQB0026QqLvzmLm
{
  "script":{
    "source": "ctx._source.price += params.increment",
    "lang": "painless",
    "params": {
      "increment": 2
    }
  }
}
```

  

출력결과

```
{
  "_index" : "catalog",
  "_type" : "_doc",
  "_id" : "HnMMOnQB0026QqLvzmLm",
  "_version" : 3,
  "result" : "updated",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 8,
  "_primary_term" : 1
}
```



# 삭제 API

## 6.x (DEPRECATED)

> DELETE /catalog/product/{id}

6.x 대의 명령은 현재 DEPRECATED 되었다.

```
#! Deprecation: [types removal] Specifying types in document index requests is deprecated, use the /{index}/_doc/{id} endpoint instead.
{
  "_index" : "catalog",
  "_type" : "product",
  "_id" : "HnMMOnQB0026QqLvzmLm",
  "_version" : 4,
  "result" : "deleted",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 9,
  "_primary_term" : 1
}
```



## 7.x 

> DELETE /{index}/_doc/{id}

```
DELETE /catalog/_doc/3
```

  

출력결과

```
{
  "_index" : "catalog",
  "_type" : "_doc",
  "_id" : "3",
  "_version" : 2,
  "result" : "deleted",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 10,
  "_primary_term" : 1
}
```









