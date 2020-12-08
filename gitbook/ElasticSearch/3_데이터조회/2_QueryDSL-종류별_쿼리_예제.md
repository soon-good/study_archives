# QueryDSL - 종류별 쿼리 예제

# 참고자료

- [엘라스틱서치 실무 가이드](http://www.yes24.com/Product/Goods/71893929)
  - 4.3 QueryDSL 의 주요 쿼리 



# QueryDSL 의 기본적인 형식

ElasticSearch에 JSON 구조의 쿼리 파라미터를 통해 질문을 던지는 것을 QueryDSL 형식의 쿼리라고 한다. QueryDSL 형식의 쿼리의 기본적인 형식을 알아보자

```bash
POST stock_financial_quarterly/_search
{
  "query":{
    "match_all": {}
  }
}
```

Elastic Search 의 QueryDSL 쿼리 방식은 위와 같이 `{ "query" : {...} }` 의 형식을 쿼리 파라미터로 하여 Elastic Search에 질의한다.  



# 샘플데이터

## DDL

주의할 점은 actors 항목에 대한 type, 즉 `actors.type` 을 `nested` 로 주었다는 점이다.  

```bash
PUT movie_shop
{
  "mappings":{
    "properties":{
      
      "movieCode": {
        "type": "keyword"
      },

      "movieName": {
        "type": "text",
        "analyzer": "standard"
      },
      
      "movieDescription": {
        "type": "text",
        "analyzer": "standard"
      },

      "price": { 
        "type": "double"
      },
      
      "actors": {
        "type": "nested",
        "properties": {
          "actorName": {
            "type": "text"
          },
          "actorCode": {
            "type": "keyword"
          }
        }
      }

    }
  }
}
```



## 샘플 데이터 INSERT

### 데이터 1)

```bash
POST movie_shop/_doc/1
{
  "movieCode": "A1",
  "movieName": "AAA World",
  "movieDescription": "AAA is a AAA world",
  "price": 1000,
  
  "actors": [
    {
      "actorName": "ACTOR MR.A",
      "actorCode": "ACTOR-A"
    },
    {
      "actorName": "ACTOR MR.AA",
      "actorCode": "ACTOR-AA"
    }
  ]
}
```

  

### 데이터 2)

```bash
POST movie_shop/_doc/2
{
  "movieCode": "B1",
  "movieName": "BBB World",
  "movieDescription": "BBB is a BBB world",
  "price": 1000,
  
  "actors": [
    {
      "actorName": "ACTOR MR.B",
      "actorCode": "ACTOR-B"
    },
    {
      "actorName": "ACTOR MR.BB",
      "actorCode": "ACTOR-BB"
    }
  ]
}
```



# 1. Match All Query

 `{ "query" : {"match_all": {...} }` 의 형태를 띈다. 자세히 살펴보면 `match_all` 파라미터를 "query" 객체 내의 주요 필드이다.  



## 예제

```bash
POST movie_shop/_search
{
  "query":{
    "match_all": {}
  }
}
```

  

## 조회결과 

```bash
{
  "took" : 717,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 2,
      "relation" : "eq"
    },
    "max_score" : 1.0,
    "hits" : [
      {
        "_index" : "movie_shop",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : 1.0,
        "_source" : {
          "movieCode" : "A1",
          "movieName" : "AAA World",
          "movieDescription" : "AAA is a AAA world",
          "price" : 1000,
          "actors" : [
            {
              "actorName" : "ACTOR MR.A",
              "actorCode" : "ACTOR-A"
            },
            {
              "actorName" : "ACTOR MR.AA",
              "actorCode" : "ACTOR-AA"
            }
          ]
        }
      },
      {
        "_index" : "movie_shop",
        "_type" : "_doc",
        "_id" : "2",
        "_score" : 1.0,
        "_source" : {
          "movieCode" : "B1",
          "movieName" : "BBB World",
          "movieDescription" : "BBB is a BBB world",
          "price" : 1000,
          "actors" : [
            {
              "actorName" : "ACTOR MR.B",
              "actorCode" : "ACTOR-B"
            },
            {
              "actorName" : "ACTOR MR.BB",
              "actorCode" : "ACTOR-BB"
            }
          ]
        }
      }
    ]
  }
}
```



# 2. Match Query

- 쿼리를 수행하기 전에 분석기로 텍스트를 분석한 후 검색을 수행한다.
- Match Query 는 텍스트, 숫자, 날짜 등이 포함된 문장을 형태소 분석으로 텀(term)으로 분리하고, 이 텀(term)들을 이용해 검색 질의를 수행한다. 
- 검색어가 분석되어야 할 경우에 사용해야 한다. 검색어를 형태소 분석하여 텀(term)으로 분리하기 때문이다.

‌

## 예제

```bash
POST movie_shop/_search
{
  "query": {
    "match":{
      "movieName": "AAA"
    }
  }
}
```



## 조회결과

```bash
{
  "took" : 0,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1,
      "relation" : "eq"
    },
    "max_score" : 0.6931471,
    "hits" : [
      {
        "_index" : "movie_shop",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : 0.6931471,
        "_source" : {
          "movieCode" : "A1",
          "movieName" : "AAA World",
          "movieDescription" : "AAA is a AAA world",
          "price" : 1000,
          "actors" : [
            {
              "actorName" : "ACTOR MR.A",
              "actorCode" : "ACTOR-A"
            },
            {
              "actorName" : "ACTOR MR.AA",
              "actorCode" : "ACTOR-AA"
            }
          ]
        }
      }
    ]
  }
}
```



# 3. Multi Match Query

- 여러 개의 필드를 대상으로 검색해야 할 때 사용하는 쿼리이다.
- `multi_match` 파라미터를 사용해 질의를 한다.

  

## 예제

```bash
POST movie_shop/_search
{
  "query": {
    "multi_match": {
      "query": "A",
      "fields": ["movieName, movieCode"]
    }
  }
}
```



# 4. Term Query

문자 형태, 즉, 텍스트 형태의 값을 검색할 때 사용한다.  

검색어를 하나의 텀(Term) 으로 처리하기 때문에 필드에 텀(Term)이 정확히 존재하지 않으면 검색되지 않는다.

- Text 데이터 타입
  - 필드에 데이터가 저장되기 전에 데이터가 분석되어 역색인 구조로 저장된다.
- Keyword 데이터 타입
  - 데이터가 분석되지 않고 그대로 필드에 저장된다.

  

## 예제 

```bash
POST movie_shop/_search
{
  "query" : {
    "term": {
      "movieName": "World" 
    }
  }
}
```



# 5. Query String

`query_string` 파라미터를 사용하는 쿼리이다. `query_string` 파라미터를 사용하면 내장 쿼리 분석기를 이용하는 쿼리를 수행하게 된다.  

'AAA' 와 'World' 단어가 AND 조건으로 일치하는 문서를 찾는 예제를 살펴보자.  



## 예제

```bash
#5) Query String
POST movie_shop/_search
{
  "query": {
    "query_string": {
      "default_field": "movieName",
      "query": "(World) OR (AAA)"
    }
  }
}
```



## 출력결과

```bash
{
  "took" : 1,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 2,
      "relation" : "eq"
    },
    "max_score" : 0.8754687,
    "hits" : [
      {
        "_index" : "movie_shop",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : 0.8754687,
        "_source" : {
          "movieCode" : "A1",
          "movieName" : "AAA World",
          "movieDescription" : "AAA is a AAA world",
          "price" : 1000,
          "actors" : [
            {
              "actorName" : "ACTOR MR.A",
              "actorCode" : "ACTOR-A"
            },
            {
              "actorName" : "ACTOR MR.AA",
              "actorCode" : "ACTOR-AA"
            }
          ]
        }
      },
      {
        "_index" : "movie_shop",
        "_type" : "_doc",
        "_id" : "2",
        "_score" : 0.18232156,
        "_source" : {
          "movieCode" : "B1",
          "movieName" : "BBB World",
          "movieDescription" : "BBB is a BBB world",
          "price" : 1000,
          "actors" : [
            {
              "actorName" : "ACTOR MR.B",
              "actorCode" : "ACTOR-B"
            },
            {
              "actorName" : "ACTOR MR.BB",
              "actorCode" : "ACTOR-BB"
            }
          ]
        }
      }
    ]
  }
}
```



# 6. Bool Query

RDB에서 Where 절에 AND, OR 로 여러개의 조건식을 조합해서 쿼리를 날리는데, Bool Query 의 역할이 이와 유사한 역할을 수행하는 것이라고 이해하면 이해가 수월하다.  

엘라스틱 서치는 여러개의 쿼리를 조합해서 스코어를 더 높게 받을 수 있는 쿼리 조건으로 검색하도록 할 수 있따. 이런 유형의 쿼리를 **Compound Query** 라고 한다. 이러한 Compoound Query 를 구현하기 위해 엘라스틱 서치에서는 Bool Query 를 제공한다.  

Bool Query 를 맨 위에  두고 하위에서 다른 쿼리를 구성해서 여러가지 경우의 수에 맞도록 쿼리를 수행할 수 있따.  

- `must` 
  - AND 필드명 = 조건식
- `must_not` 
  - AND 필드명 != 조건식
- `should` 
  - OR 필드명 = 조건식
- `filter` 
  - 필드명 IN (조건식) 



## 예제

```bash
#6) Bool Query
POST movie_shop/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "movieCode": "A1" 
          }
        }
      ],
      "filter": [
        {
          "match": {
            "movieName": "AAA World"
          }
        }
      ]
    }
  }
}
```



## 출력결과

```bash
{
  "took" : 0,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1,
      "relation" : "eq"
    },
    "max_score" : 0.6931471,
    "hits" : [
      {
        "_index" : "movie_shop",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : 0.6931471,
        "_source" : {
          "movieCode" : "A1",
          "movieName" : "AAA World",
          "movieDescription" : "AAA is a AAA world",
          "price" : 1000,
          "actors" : [
            {
              "actorName" : "ACTOR MR.A",
              "actorCode" : "ACTOR-A"
            },
            {
              "actorName" : "ACTOR MR.AA",
              "actorCode" : "ACTOR-AA"
            }
          ]
        }
      }
    ]
  }
}
```

  

# 7. Prefix Query

  

# 8. Exists Query

  

# 9. Nested Query

> SQL의 조인과 유사한 기능을 수행하는 쿼리이다.  

Nested Query 는 문서 내부에 다른 문서가 존재하는 중첩구조의 Nested 데이터 타입ㅇ의 필드를 검색할 때 사용한다. `path` 에 중첩된 필드명을 명시하고, `query` 옵션에 Nested 필드 검색에 사용할 쿼리를 입력한다.  

> 분산시스템에서 SQL의 Join 과 같은 기능을 수행하려면 엄청나게 많은 비용이 소모된다. 수평적으로 샤드가 얼마나 늘어날지 모르는 상황에서 모든 샤드를 검색해야 할 수도 있기 때문이다. 하지만 부모/자식 관계의 형태로 모델링 된 경우가 종종 있기도 하다. 이런 경우에 대해 엘라스틱 서치에서는 Nested Query를 제공하고 있다.

  

## 예제)

movieCode 는 A1 이면서 actors 내에서 actorCode가 "ACTOR-A" 타입인 것만을 추려서 필터링하는 쿼리 예제를 살펴보자.

```bash
# 9) Nested Query
POST movie_shop/_search 
{
  "query":{
    "bool": {
      "must": [
        {
          "term": {
            "movieCode": "A1"
          }
        },
        {
          "nested": {
            "path": "actors",
            "query": {
              "bool": {
                "must": [
                  {
                    "term": {
                      "actors.actorCode": "ACTOR-A"
                    }
                  }
                ]
              }
            }
          }
        }
      ]
    }
  }
}
```



## 출력결과)

```bash
{
  "took" : 2,
  "timed_out" : false,
  "_shards" : {
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 1,
      "relation" : "eq"
    },
    "max_score" : 1.89712,
    "hits" : [
      {
        "_index" : "movie_shop",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : 1.89712,
        "_source" : {
          "movieCode" : "A1",
          "movieName" : "AAA World",
          "movieDescription" : "AAA is a AAA world",
          "price" : 1000,
          "actors" : [
            {
              "actorName" : "ACTOR MR.A",
              "actorCode" : "ACTOR-A"
            },
            {
              "actorName" : "ACTOR MR.AA",
              "actorCode" : "ACTOR-AA"
            }
          ]
        }
      }
    ]
  }
}
```

