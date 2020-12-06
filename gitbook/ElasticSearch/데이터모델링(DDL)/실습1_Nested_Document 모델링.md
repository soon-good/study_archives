# 실습) Nested Document 모델링

데이터 구조를 Nested Document 로 모델링하는 방법을 정리해보자. 오늘 활용하는 데이터는 [stockplus](https://stockplus.com/m) 에서 가져온 데이터이다.  

# 참고자료 

- [엘라스틱 서치 실무가이드](http://www.yes24.com/Product/Goods/71893929)
- Elastic.co
  - [Nested Query](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-nested-query.html)
    - 중첩 구조의 도큐먼트를 작성했을때 조회를 하려면 Nested Query를 수행해야 한다.
    - https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-nested-query.html
  - [Update API](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-update.html)
    - Update API를 실행하는 다양한 방식을 설명하고 있다.
    - 여기서도 역시 Scripted 기반의 Update 구문을 설명하고 있다.
    - https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-update.html
- elastic search nested document insert (구글검색)
  - [Delete / add Nested Objects in Elastic search](https://stackoverflow.com/questions/39539496/delete-add-nested-objects-in-elastic-search)
    - https://stackoverflow.com/questions/39539496/delete-add-nested-objects-in-elastic-search
  - [How to manage nested objects in elasticsearch documents](https://iridakos.com/programming/2019/05/02/add-update-delete-elasticsearch-nested-objects)
    - 위의 stackoverflow 링크에서 알려준 자료이다.
    - https://iridakos.com/programming/2019/05/02/add-update-delete-elasticsearch-nested-objects



# 초기 모델링‌

처음 생각한 데이터 모델링은 아래와 같았다.

![이미지](./img/실습1_1.png)

다소 관계형 Database 의 형태를 많이 닮아있다. 이때 financial_statistics 인덱스 내에 stockCode, time 필드에 대해서 복합키로 기본키를 걸어줘야 한다. 

오늘 예제에서는 복합키를 지정하는 예제가 아닌, Nested Document 모델을 사용하는 예제를 다뤄보려고 한다.

첫 시작은 json 도큐먼트 기반의 데이터구조를 사용하는 만큼... Nested 모델링을 사용하는 것은 어떨까? 하는 생각에 Nested 데이터 모델링을 직접 찾아보고 공부하게 되었던것 같다.



# Nested 모델링

JSON 형식의 데이터 구조라면 조금은 다르게 구성해야 하지 않을까? 하고 아래처럼 구성했다.  

```bash
{
  "stockName": "",
  "stockSectorName": "",
  "marketCapitalization": -1.00,
  "statistics": [
    {
      "baseDate": "2020-12-01",
      "pbr": 11.00,
      "per": 11.00,
      "roe": 11.00
    },
    {
      "baseDate": "2020-12-02",
      "pbr": 22.01,
      "per": 22.00,
      "roe": 22.00
    },
    // ...
  ]
}
```

이 데이터들은 `stock_financial_quarterly` 라는 인덱스에 색인한다. `stock_financial_quarterly` 인덱스를 생성하면서, 타입을 생성하는 DDL 구문은 아래에 정리해두었다.  

# 인덱스 생성&매핑(DDL)

> [엘라스틱 서치 실무 가이드](http://www.yes24.com/Product/Goods/71893929)

```bash
PUT stock_financial_quarterly
{
  "mappings":{
    "properties":{
      "stockName": {
        "type": "text",
        "analyzer": "standard"
      },
      "stockSectorName": {
        "type": "text"
      },
      "marketCapitalization": { 
        "type": "double"
      },
      "statistics": {
        "type": "nested", 
        "properties": {
          "baseDate": {
            "type": "date",
            "format": "yyyy-MM-dd'T'HH:mm:ss||yyyy-MM-dd||epoch_millis||yyyyMMdd"
          },
          "pbr": {
            "type": "double"
          },
          "per": {
            "type": "double"
          },
          "roe": {
            "type": "double"
          }
        }
      }

    }
  }
}
```



# ‌데이터 색인 (INSERT) (1) - 일반적인 INSERT

> 참고자료 : [Elastic.co - Nested Query](https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-nested-query.html)

초기 데이터 INSERT 는 성공하지만, 도큐먼트 내의 리스트 자료구조에 데이터를 add/delete 할 수없는 예를 먼저 들어야 할 것 같다. 

## 첫 번째 샘플 데이터 INSERT

```bash
POST stock_financial_quarterly/_doc
{
  "stockName": "하이트진로",
  "stockSectorName": "음료",
  "marketCapitalization": 2230200000000,
  "statistics": [
    {
      "baseDate": "2020-12-01",
      "per": "11",
      "pbr": "111",
      "roe": "1111"
    },
    {
      "baseDate": "2020-12-02",
      "per": "22",
      "pbr": "222",
      "roe": "2222"
    }
  ]
}
```



### 데이터 검색

```bash
GET stock_financial_quarterly/_search
```



### 출력결과 

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
    "max_score" : 1.0,
    "hits" : [
      {
        "_index" : "stock_financial_quarterly",
        "_type" : "_doc",
        "_id" : "1A1dM3YBGoIYe4hO3vHU",
        "_score" : 1.0,
        "_source" : {
          "stockName" : "하이트진로",
          "stockSectorName" : "음료",
          "marketCapitalization" : 2230200000000,
          "statistics" : [
            {
              "baseDate" : "2020-12-01",
              "per" : "11",
              "pbr" : "111",
              "roe" : "1111"
            },
            {
              "baseDate" : "2020-12-02",
              "per" : "22",
              "pbr" : "222",
              "roe" : "2222"
            }
          ]
        }
      }
    ]
  }
}
```



## 두 번째 샘플 데이터 INSERT

문제는 지금부터이다. 일단 아래의 명령으로 데이터를 하나 더 추가해보자. 2020/12/03 일자의 데이터를 하나 더 추가하려는 명령어이다.

```bash
POST stock_financial_quarterly/_doc
{
  "stockName": "하이트진로",
  "stockSectorName": "음료",
  "marketCapitalization": 2230200000000,
  "statistics": [
    {
      "baseDate": "2020-12-03",
      "per": "22",
      "pbr": "222",
      "roe": "2222"
    }
  ]
}
```



### 데이터 결과 조회

```bash
GET stock_financial_quarterly/_search
```



### 출력결과 

하이트 진로에 대해서 2020/12/01, 2020/12/02, 2020/12/03 일자의 데이터가 하나의 도큐먼트에 저장되어 있어야 하는데, 하이트 진로라는 회사 하나에 대해 아래와 같이 두가지의 도큐먼트로 저장되게 된다.

- 2020/12/01, 2020/12/02 일자의 도큐먼트
- 2020/12/03 일자의 도큐먼트

실제로 확인해본 출력결과는 아래와 같았다.

```bash
{
  "took" : 111,
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
        "_index" : "stock_financial_quarterly",
        "_type" : "_doc",
        "_id" : "o-gtM3YBYTQk_N3fVP0H",
        "_score" : 1.0,
        "_source" : {
          "stockName" : "하이트진로",
          "stockSectorName" : "음료",
          "marketCapitalization" : 2230200000000,
          "statistics" : [
            {
              "baseDate" : "2020-12-01",
              "per" : "11",
              "pbr" : "111",
              "roe" : "1111"
            },
            {
              "baseDate" : "2020-12-02",
              "per" : "22",
              "pbr" : "222",
              "roe" : "2222"
            }
          ]
        }
      },
      {
        "_index" : "stock_financial_quarterly",
        "_type" : "_doc",
        "_id" : "0Q0uM3YBGoIYe4hOZPED",
        "_score" : 1.0,
        "_source" : {
          "stockName" : "하이트진로",
          "stockSectorName" : "음료",
          "marketCapitalization" : 2230200000000,
          "statistics" : [
            {
              "baseDate" : "2020-12-03",
              "per" : "22",
              "pbr" : "222",
              "roe" : "2222"
            }
          ]
        }
      }
    ]
  }
}
```

이렇게 하이트 진로라는 하나의 회사에 대한 데이터가 여러개이면 하나의 데이터라는 것을 보장할 방법이 충분치 않다. 이런 문제로 찾아본 것이 Scripted Update 이다.



# 데이터 색인 (INSERT) (2) - Scripted Update

> 참고자료 : 
>
> - [StackOverFlow - Delete/add nested objects in Elastic Search](https://stackoverflow.com/questions/39539496/delete-add-nested-objects-in-elastic-search)
> - [How to manage nested objects in elasticsearch documents](https://iridakos.com/programming/2019/05/02/add-update-delete-elasticsearch-nested-objects)

중첩 구조의 객체의 상태를 업데이트하는 것은 script를 실행시키는 것으로 가능하다. 생각해보니 [ElasticSearch 실무 가이드](http://www.yes24.com/Product/Goods/71893929) 에서 script 관련 내용을 보기는 했었다. 하지만 리스트에 데이터를 추가하는 것은 오늘 처음보기는 한다. 이렇게 했을때 reindex 관련 이슈가 있는지 등등에 대해서도 찾아봐야 할 것 같기는 하다.

  

## 기본적인 컨셉

script 에 대한 파라미터를 전달해주는 것으로 실행이 가능하다. source 라는 항목에서 script 를 실행하고 있는 것을 확인 가능하다. 여기서 ctx 라는 변수가 있는데, 이 ctx 라는 것은 컨텍스트(프로그램의 문맥, 흐름, 특정 시점의 핸들값 등등의 의미로 자주 사용되는 편)를 의미하는 변수이다. 

```bash
# Add a new cat
POST iridakos_nested_objects/human/1/_update
{
  "script": {
    "source": "ctx._source.cats.add(params.cat)",
    "params": {
      "cat": {
        "colors": 4,
        "name": "Leon",
        "breed": "Persian"
      }
    }
  }
}
```



## 샘플데이터 INSERT

먼저 데이터를 모두 지우자.  

```bash
DELETE stock_financial_quarterly
```

  

샘플 데이터를 INSERT 해보자. (2020/12/01, 2020/12/02 날짜의 데이터를 insert 한다.)  

```bash
POST stock_financial_quarterly/_doc/1
{
  "stockName": "하이트진로",
  "stockSectorName": "음료",
  "marketCapitalization": 2230200000000,
  "statistics": [
    {
      "baseDate": "2020-12-01",
      "per": "11",
      "pbr": "111",
      "roe": "1111"
    },
    {
      "baseDate": "2020-12-02",
      "per": "22",
      "pbr": "222",
      "roe": "2222"
    }
  ]
}
```

  

INSERT 한 데이터를 점검해보자.

```bash
GET stock_financial_quarterly/_search
```

  

현재 시점의 데이터의 형태는 아래와 같다.

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
    "max_score" : 1.0,
    "hits" : [
      {
        "_index" : "stock_financial_quarterly",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : 1.0,
        "_source" : {
          "stockName" : "하이트진로",
          "stockSectorName" : "음료",
          "marketCapitalization" : 2230200000000,
          "statistics" : [
            {
              "baseDate" : "2020-12-01",
              "per" : "11",
              "pbr" : "111",
              "roe" : "1111"
            },
            {
              "baseDate" : "2020-12-02",
              "per" : "22",
              "pbr" : "222",
              "roe" : "2222"
            }
          ]
        }
      }
    ]
  }
}
```



## Scripted Data Insert

`stock_financial_quarterly.statistics`  는 배열 타입으로 선언되어 있는데 데이터의 실제 모습은 아래와 유사한 형태로 INSERT 되어 있다.  

```bash
{
  // ...
  "hits" : {
    // ...
    "hits" : [
      {
        // ...
        "_source" : {
          "stockName" : "하이트진로",
          "stockSectorName" : "음료",
          "marketCapitalization" : 2230200000000,


          // 이 부분이다.
          "statistics" : [
            {
              "baseDate" : "2020-12-01",
              "per" : "11",
              "pbr" : "111",
              "roe" : "1111"
            },
            {
              "baseDate" : "2020-12-02",
              "per" : "22",
              "pbr" : "222",
              "roe" : "2222"
            }
          ]

        }
      }
    ]
  }
}
```



### 스크립트 데이터 INSERT

> 참고자료
>
> - [How to manage nested objects in Elasticsearch documents](https://iridakos.com/programming/2019/05/02/add-update-delete-elasticsearch-nested-objects)
> - [Update API - Elastic.co](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-update.html)

```bash
POST stock_financial_quarterly/_update/1
{
  "script": {
    "source": "ctx._source.statistics.add(params.stat)",
    "params": {
      "stat": {
        "baseDate": "2020-12-03",
        "per": "33",
        "pbr": "333",
        "roe": "3333"
      }
    }
  }
}
```

출력결과는 아래와 같다.

```bash
{
  "_index" : "stock_financial_quarterly",
  "_type" : "_doc",
  "_id" : "1",
  "_version" : 2,
  "result" : "updated",
  "_shards" : {
    "total" : 2,
    "successful" : 2,
    "failed" : 0
  },
  "_seq_no" : 1,
  "_primary_term" : 1
}
```

실제로 데이터가 INSERT 되었는지 확인해보자. 2020-12-03 일자의 데이터가 INSERT 되었음을 확인할 수 있다.

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
    "max_score" : 1.0,
    "hits" : [
      {
        "_index" : "stock_financial_quarterly",
        "_type" : "_doc",
        "_id" : "1",
        "_score" : 1.0,
        "_source" : {
          "stockName" : "하이트진로",
          "stockSectorName" : "음료",
          "marketCapitalization" : 2230200000000,
          "statistics" : [
            {
              "baseDate" : "2020-12-01",
              "per" : "11",
              "pbr" : "111",
              "roe" : "1111"
            },
            {
              "baseDate" : "2020-12-02",
              "per" : "22",
              "pbr" : "222",
              "roe" : "2222"
            },
            {
              "pbr" : "333",
              "roe" : "3333",
              "baseDate" : "2020-12-03",
              "per" : "33"
            }
          ]
        }
      }
    ]
  }
}
```



# Nested Query



