# INDEX 매핑 개념

> Elastic Search 의 모든 내용을 정리하지는 않을 예정이다. 단지 lognomy 프로젝트에 필요한 Elastic Search의 개념들 만을 기록하는 것이 목표이다. 막상 개념 정리를 시작 할 때는 "정말 길다..." 하는 생각이 들었는데... 막상 정리하고 나니 내용이 너무 짧아서 만족은 하지만... 참... 허탈하기도 하다.  
>
> ElasticSearch 의 기본 개념들을 한동안 무시하고 공부하지 않았었다. (무시했다고 보는게 맞다. 반성하고 있다.) 공부를 하면서 느낀 점은 역시 한순간에 이루어지는 성취는 없다는 점이다. 뭘 하든 깊게 공부하자는 생각이 들었다.



이 글은 [gosgjung.gitbook.io - INDEX 매핑 개념](https://gosgjung.gitbook.io/lognomy/lognomy/elastic-cloud/undefined/index-mapping-overview) 에 정리했던 내용을 github 에 백업과 버전 관리 및 원본 문서 증명용도(응?)로 정리한 문서입니다. 



# 참고자료

[엘라스틱 서치 실무가이드](http://www.yes24.com/Product/Goods/71893929?OzSrank=1)



# 매핑의 개념

데이터가 인덱스에 데이터가 추가될 때 데이터의 타입을 구체적으로 정의하는 과정을 매핑이라고 한다. 흔히 데이터베이스에서 테이블의 각 컬럼들, 컬럼의 타입, 컬럼명, 컬럼의 제약조건을 정의하는 과정을 DDL이라고 한다. 인덱스 매핑 역시 이 DDL과 유사한 연산을 한다. 즉, 인덱스 생성시 인덱스가 가지게 될 필드의 타입을 지정하는 역할을 수행한다. 단점으로는 인덱스는 생성된 매핑의 타입을 변경할 수 없다는 점이다. 타입을 변경하려면 인덱스를 삭제하고 재생성하거나 매핑을 재정의해야 한다.

그리고 인덱스에는 document 라고 불리는 하나의 데이터 단위가 존재한다. 데이터베이스에서 흔히 'row, 로우' 라고 불리는 개념과 유사하다. document (문서)에 존재하는 필드의 속성(properties)들을 정의할 때 각 필드의 속성(properties)에는 데이터 타입, 메타데이터가 포함된다. 

그리고 이 데이터 타입과 메타데이터를 이용해 document가 어떻게 역색인(Inverted Index)으로 변환되게 할지 상세하게 정의할 수 있다. (메타 데이터는 예를 들면 `_id`  , `_index` 등을 예로 들수 있다.) 



# 동적매핑 vs 명시적인 매핑

Elastic Search 의 매핑방식은 아래와 같이 두가지 방식이 있다.

- 동적 매핑
- 명시적 매핑

## 동적매핑

동적 매핑의 개념부터 알아보자. Elastic Search 는 기본적으로 스키마리스이다. 스키마가 없어도 매핑정보 없이 내부적으로 묵시적인 데이터 타입 매핑이 일어난다. 이렇게 묵시적으로 내부에서 자동으로 매핑해주는 동적 매핑을 운영환경에 적용하면 추후 굉장히 관리가 힘들어지게 된다. 

데이터의 타입에 따라 다른 연산을 하게끔 정의 해야 하는데, 해당 필드가 어떤 타입인지 파악하려면 `GET /인덱스명/_mapping` 과 같은 쿼리를 통해 데이터 타입을 확인해서 코드 상에서 변환을 해주어야 한다. 

## 명시적 매핑

관계형 데이터베이스를 사용할 때 데이터베이스에 대해 DDL을 통해 테이블, 컬럼명, 컬럼의 데이터 타입, 참조관계 등을 지정할 수 있다. 이렇게 DDL 과 같이 데이터의 타입, 필드 명, 프로퍼티 설정 등의 작업을 거치는 과정을 Elastic Search 에서는 '**매핑**' 이라고 부른다.

그리고 DDL은 보통 테이블을 생성할 때 수행하는데, 매핑 역시 인덱스 생성과 동시에 매핑을 지정하는 편이다.

[참고 - 인덱스 생성과 동시에 매핑(한국경제통계시스템 데이터 인덱스 구성)](https://gosgjung.gitbook.io/lognomy/lognomy/elastic-cloud/undefined/index-kospi)

# 명시적 매핑(DDL) 실습

## 매핑 상태 확인



```
GET kospi/_mapping
```

## 명시적 매핑 (DDL)

> 참고 자료 : 
>
> - [keyword 타입에 대해 standard analyzer 를 사용하려 할 때 에러가 나는 이유 - discuss.elastic.co](https://discuss.elastic.co/t/mapping-definition-for-fields-has-unsupported-parameters-analyzer-not-standard/204782)

예를 들어 아래와 같은 형식의 매핑구문을 통해 인덱스 생성과 동시에 매핑을 적용했다고 해보자.



```
PUT financial_yearly 
{
  "mappings":{
    "properties":{
      "stockCode": {
        "type": "keyword"
      },
      "stockName": {
        "type": "text",
        "analyzer": "standard"
      },
      "fundamental": {
        "properties": {
          "baseDate": {
            "type": "date",
            "format": "yyyy-MM-dd'T'HH:mm:ss||yyyy-MM-dd||epoch_millis||yyyyMMdd"
          },
          "pbr":{
            "type": "double"
          },
          "per":{
            "type": "double"
          }
        }
      }
    }
  }
}
```

출력결과

```
{
  "acknowledged" : true,
  "shards_acknowledged" : true,
  "index" : "financial_yearly"
}
```



## 매핑상태 확인



```
GET financial_yearly/_mapping
```

출력결과



```
{
  "financial_yearly" : {
    "mappings" : {
      "properties" : {
        "fundamental" : {
          "properties" : {
            "baseDate" : {
              "type" : "date",
              "format" : "yyyy-MM-dd'T'HH:mm:ss||yyyy-MM-dd||epoch_millis||yyyyMMdd"
            },
            "pbr" : {
              "type" : "double"
            },
            "per" : {
              "type" : "double"
            }
          }
        },
        "stockCode" : {
          "type" : "keyword"
        },
        "stockName" : {
          "type" : "text",
          "analyzer" : "standard"
        }
      }
    }
  }
}
```

## 인덱스 삭제

인덱스를 생성했는데, 초기 개발 단계여서 삭제하고 싶을 경우가 있다. 이 경우 아래의 명령어를 내려주면 된다.



```
DELETE financial_yearly
```

## 정리하면서...

매핑구문(DDL)에서 아래와 같이 "analyzer" : "standard" 와 같은 파라미터가 추가로 전달된 것을 볼 수있다. 이렇게 매핑 구문 내에서 부가적으로 전달하는 키/값 쌍의 파라미터를 매핑 파라미터라고 한다. 여기에 대해서는 바로 다음 문서에서 정리할 예정이다.



```
PUT financial_yearly 
{
  "mappings":{
    // ...
      "stockName": {
        "type": "text",
        "analyzer": "standard"
      },
    // ...
    }
  }
}
```