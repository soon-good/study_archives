
참고)
DEPRECATED 된 항목이 하나 있다.
PUT /<index>/<type>/<id> 와 같은 형식으로 INSERT 하는 것은 DEPRECATED 되었다고 한다.
자세한 내용은 아래를 참고하자. 
type 이라는 개념자체가 사라진 것 같다는 느낌이다. 
#! Deprecation: [types removal] Specifying types in document index requests is deprecated, use the typeless endpoints instead (/{index}/_doc/{id}, /{index}/_doc, or /{index}/_create/{id}).

7.x 에서는 타입(type)이라는 개념이 없어졌다. 신기하게도 _doc라는 개념을 사용한다.
하나의 index 에 생성한 모든 데이터들은 _doc 라는 type 을 ㅗ관리된다.
인덱스 안에 타입을 여러개 두는 것이 불가능해졌다.
테이블 같은 개념은 이제 인덱스가 모두 관리하는것이 되었고, 인덱스는 스키마의 역할을 하는 것은 아닌듯해보인다.

인덱싱, 색인 연산 (Iexing Operation)
- 인덱스 내에서 타입에 도큐먼트를 추가하거나, 생성하는 작업

동적 매핑
- 인덱스와 도큐먼트를 한번에 INSERT시 도큐먼트가 아직 존재하지 않을 경우 필드의 데이터 타입을 추론한다. 이것을 동적 매핑이라고 한다.
- 

# 인덱스 추가
인덱스 내에서 타입에 도큐먼트를 추가하거나, 생성하는 작업을 색인연산(Indexing Operation)이라고 부른다.
- ID를 지정해 도큐먼트 색인하기
  - 요청형식 : PUT /<index>/<type>/<id>

- ID를 제공하지 않고 도큐먼트 색인하기
  - 도큐먼트의 ID 생성을 제어하지 않으려면 POST 메서드를 사용하면 된다.
  - 요청형식 : POST /<index>/<type>

---
ID를 지정해 도큐먼트  색인하기
# 1. 예제 데이터 (1) insert
아이디를 지정해 도큐먼트 색인하기 (Document Indexing)
PUT /catalog/product/1
{
  "sku": "SP000001",
  "title": "ElasticSearch for Hadoop",
  "description": "ElasticSearch for Hadoop",
  "author": "Vishal Shukla",
  "ISBN": "1785288997",
  "price": 26.99
}

```bash
curl -XPUT http://localhost:9200/catalog/product/1 -H 'Content-Type: application/json' -d '{"sku": "SP000001", "title": "ElasticSearch for Hadoop", "description": "Elasticsearch for Hadoop" "author": "Vishal Shukla", "ISBN": "1785288997", "price": 26.99}'
```

# 2. 예제 데이터 (2) insert
PUT /catalog/product/2
{
  "sku": "SP000002",
  "title": "Google Pixel Phone 32GB - 5 inch display",
  "description": "Google Pixel Phone 32GB - 5 inch display (Factory Unlocked US Version)",
  "price": 26.99,
  "resolution": "1440 x 2560 pixels",
  "os": "Android 7.1"
}

# 3. (82p) 인덱스 생성시 샤드 및 복제본 갯수를 지정하기 
PUT /catalog
{
  "settings":{
    "index":{
      "number_of_shards": 5,
      "number_of_replicas": 2
    }
  }
}

---
# ID를 제공하지 않고 도큐먼트 색인하기
- 도큐먼트의 ID 생성을 제어하지 않으려면 POST 메서드를 사용하면 된다.
- 요청형식 : POST /<index>/<type>

POST /catalog/product
{
  "sku": "SP000003",
  "title": "Mastering ElasticSearch",
  "description": "Mastering ElasticSearch",
  "author": "Bharvi Dixit",
  "price": 54.99
}

출력결과
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

---
조회 API 
GET /catalog/product/HnMMOnQB0026QqLvzmLm

역시 위의 명령은 DEPRECATED 되었다.
```
#! Deprecation: [types removal] Specifying types in document get requests is deprecated, use the /{index}/_doc/{id} endpoint instead.
```
7.x 에서는 아래처럼 적용한다.
> /{index}/_doc/{id}

ex)
GET /catalog/_doc/1

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
# 업데이트 API 
- 업데이트 API는 기존 도큐먼트를 ID로 업데이트하는 데에 유용하다. 
- 일래스틱서치는 내부적으로각 도큐먼트의 버전을 관리한다. 도큐먼트가 변경될 때마다 버전 번호는 증가한다. 
- 부분 업데이트(업데이트 API(1) 참고)는 도큐먼트가 미리 존재하는 경우에만 동작한다.
  - 주어진 ID를 가진 도큐먼트가 없는 경우, 일래스틱 서치는 도큐먼트가 없다는 오류를 반환한다.

- 일래스틱서치에서는 upsert 를 할 수 있다.
- upsert 는 update 또는 insert 를 모두 할 수 있는 기능을 의미한다.

- doc_as_upsert 는 주어진 ID를 가진 도큐먼트가 이미 존재하는지 확인하고, 요청한 도큐먼트를 기존 도큐먼트와 병합한다. 
- 주어진 ID를 가진 도큐먼트가 존재하지 않으면 주어진 도큐먼트 내용을 가진 새로운 도큐먼트를 삽입한다.
 
 일반 업데이트 API 
 - 6.x) POST /catalog/product/1/_update
 - 7.x) POST /catalog/_update/1

 upsert 
 - 6.x) POST /catalog/product/3/_update
 - 7.x) POST /{idnex}/_update/{id}

# 업데이트 API (1)
업데이트 API는 기존 도큐먼트를 ID로 업데이트하는 데에 유용하다. 

6.x 대의 명령은 아래와 같다.
POST /catalog/product/1/_update
{
  "doc":{
    "price": "39.39"
  }
}

출력결과 (역시 DEPRECATED 문구가 나타난다.)
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

7.x 대에서 변경된 명령은 아래와 같다.
POST /catalog/_update/1
{
  "doc":{
    "price": "39.3939"
  }
}

출력결과
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


# 업데이트 API (2)
업데이트 API는 기존 도큐먼트를 ID로 업데이트하는 데에 유용하다. 
매개변수로 doc_as_upsert 를 이용해 ID가 3인 도큐먼트를 병합하거나, 존재하지 않는 다면 새로운 도큐먼트를 삽입한다.

6.x 에서 사용하던 명령은 아래와 같다.
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

출력결과를 보면 역시나 DEPRECATED 된 명령어 형식이다.
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

7.x 에서는 아래의 형식을 사용한다.
> /{index}/_update/{id} 

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

출력결과
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



# 업데이트 API (3)
스크립트를 활용해 특정 제품 가격을 2로 증가시키는 것 역시 가능하다.

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

역시 6.x 대의 명령은 DEPRECATED 되어있다.


7.x >>>
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

출력결과
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


# 삭제 API 
6.x
DELETE /catalog/product/HnMMOnQB0026QqLvzmLm

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


역시 DEPRECATED 되어있다. 
7.x 명령은 아래와 같다.
DELETE /{index}/_doc/{id}


DELETE /catalog/_doc/3

출력결과
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

# 인덱스 생성 및 매핑 관리
(81p)
매핑을 생성하는 방식은 크게 세 가지의 방법이 있다.
- 동적 매핑
  - 인덱스와 도큐먼트를 한번에 INSERT시 도큐먼트가 아직 존재하지 않을 경우 필드의 데이터 타입을 추론한다. 이것을 동적 매핑이라고 한다.
  - 인덱스에 첫 번째 도큐먼트를 색인하면 새로운 인덱스를 생성하면서 매핑 타입을 자동으로 생성한다.
  - 하지만 매핑 타입이 이렇게 자동으로 생성되는 것 보다는 직접 제어되는 것이 더 좋다.
- 인덱스 생성시 
  - 인덱스 생성시 타입에 대한 매핑을 지정할 수 있다.
    - 6.x 버전의 명령어는 더이상 지원되지 않고 에러를 뿜는다.
    - 7.x 부터는 타입이 없다. 어떻게 지정하는지 확인해봐야 할 듯 하다.
- 기존 인덱스에 타입 매핑 생성
- 매핑 업데이트

## 인덱스 생성시 매핑 추가
6.x 방식
7.x 에서는 실행되지 않는다.
PUT /my_index
{
  "settings": {
    "index":{
      "number_of_shards": 5,
      "number_of_replicas": 2
    }
  },
  "mappings": {
    "my_type":{
      "properties":{
        "f1":{
          "type": "text"
        },
        "f2":{
          "type": "keyword"
        }
      }
    }
  }
}

출력결과
에러이므로 따로 메모하지 않겠음
이 경우는 에러문구에서 힌트를 주지도 않는다.

7.x 방식
PUT /my_index
{
  "settings": {
    "index":{
      "number_of_shards": 5,
      "number_of_replicas": 2
    }
  },
  "mappings": {
    "my_type":{
      "properties":{
        "f1":{
          "type": "text"
        },
        "f2":{
          "type": "keyword"
        }
      }
    }
  }
}

출력결과
{
  "acknowledged" : true,
  "shards_acknowledged" : true,
  "index" : "my_index"
}

## 기존 인덱스에 타입 매핑 생성
인덱스를 생성한 후에 타입을 추가할 수 있다. (7.x 에서는 타입이 없어졌다. 이부분은 6.x에만 해당된다.)

6.x 명령어
PUT /catalog/_mapping/my_type2
{
  "properties":{
    "name":{
      "type": "text"
    }
  }
}

출력결과
 에러난다

7.x 명령어
PUT /catalog/_mapping
{
  "properties":{
    "name":{
      "type": "text"
    }
  }
}

출력결과
{
  "acknowledged" : true
}

추가 정리 필요한 부분 83p~84p

## 매핑 업데이트
새로운 필드에 대한 매핑은 타입 생성후 추가할 수 있다. (물론 이것은 6.x에만 해당하는 이야기이다. 7.x에서는 타입 자체가 없다.)
catalog 라는 인덱스에 code라는 필드를 추가해보자. 이렇게 하면 해당 필드는 분석되지 않는다.

6.x 버전
참고로 6.x 버전의 아래 명령어는 7.x 에서는 실행되지 않는다.
PUT /catalog/_mapping/catalog
{
  "properties":{
    "code":{
      "type": "keyword"
    }
  }
}

7.x 버전
PUT /catalog/_mapping
{
  "properties":{
    "code":{
      "type": "keyword"
    }
  }
}

출력결과
{
  "acknowledged" : true
}


병합후의 매핑을 확인해보자
GET /catalog/_mapping

{
  "catalog" : {
    "mappings" : {
      "properties" : {
        "ISBN" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "author" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "code" : {
          "type" : "keyword"
        },
        "description" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "name" : {
          "type" : "text"
        },
        "os" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "price" : {
          "type" : "float"
        },
        "resolution" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "sku" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        },
        "title" : {
          "type" : "text",
          "fields" : {
            "keyword" : {
              "type" : "keyword",
              "ignore_above" : 256
            }
          }
        }
      }
    }
  }
}


데이터를 INSERT해보자. code 라는 필드가 제대로 출력되는지, 매핑은 제대로 되었는지를 확인해봐야해서이다.
6.x
POST /catalog/category
{
  "name": "sports",
  "code": "C004",
  "description": "Sports equipment"
}

7.x
POST /catalog/_doc
{
  "name": "sports",
  "code": "C004",
  "description": "Sports equipment"
}

출력결과
{
  "_index" : "catalog",
  "_type" : "_doc",
  "_id" : "lHOQOnQB0026QqLvtm6Q",
  "_version" : 1,
  "result" : "created",
  "_shards" : {
    "total" : 2,
    "successful" : 1,
    "failed" : 0
  },
  "_seq_no" : 11,
  "_primary_term" : 1
}

87페이지부터 다시~




HnMMOnQB0026QqLvzmLm

