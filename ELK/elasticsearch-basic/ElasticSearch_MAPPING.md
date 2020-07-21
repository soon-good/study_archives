# ElasticSearch Mapping

Mapping은 관계형 데이터베이스의 Schema와 동일한 개념이다.

일을 할 때 보통 매핑 없이 데이터를 넣는 것은 상당히 위험한 일이다. (지금까지의 예제에서는 매핑없이 간단한 예제였다.)

예를 들면 날짜 데이터를 Date 타입이 아닌 문자열로 insert하거나, 숫자 타입의 데이터를 문자형으로 insert해놓으면, 검색시 색인성능 뿐만 아니라, 데이터 조회의 모호함이 발생할 수 있다.

# 1. INDEX 생성

## INDEX 생성

데이터 insert 전, index를 생성해야 한다. index의 이름은 'classes'이다.

```bash
$ curl -XPUT http://localhost:9200/classes\?pretty

# 출력결과
{
  "acknowledged" : true,
  "shards_acknowledged" : true,
  "index" : "classes"
}
```

index 생성후 결과 데이터를 확인해보자.

## data 확인

```bash
$ curl -XGET http://localhost:9200/classes\?pretty

# 출력결과
{
  "classes" : {
    "aliases" : { },
    "mappings" : { },
    "settings" : {
      "index" : {
        "creation_date" : "1595079341822",
        "number_of_shards" : "1",
        "number_of_replicas" : "1",
        "uuid" : "AfZgDelwTby9x5kodOGaRg",
        "version" : {
          "created" : "7080099"
        },
        "provided_name" : "classes"
      }
    }
  }
}
```



# 2. 매핑

매핑 파일은 [허민석교수님 github](https://github.com/minsuk-heo/BigData/blob/master/ch01/classesRating_mapping.json) 에서 매핑 파일을 다운로드 받았다. elasticsearch 7.x 버전에서 호환되지 않는 이슈로 인해 별도로 수정을 했다.  

**매핑파일 적용하기**

```bash
$ curl -XPUT http://localhost:9200/classes/class/_mapping\?include_type_name\=true\&pretty -H 'Content-Type: application/json' -d @classesRating_mapping.json

# 출력결과
{
  "acknowledged" : true
}
```

**주의사항**

> elasticsearch 7.x 부터는 mapping 생성시 include_type_name을 true로 설정해주어야한다. 

  

**데이터 확인하기**

```bash
$ curl -XGET http://localhost:9200/classes\?pretty
... 
# 출력결과
{
  "classes" : {
    "aliases" : { },
    "mappings" : {
      "properties" : {
        "major" : {
          "type" : "text"
        },
        "professor" : {
          "type" : "text"
        },
        "rating" : {
          "type" : "integer"
        },
        "school_location" : {
          "type" : "geo_point"
        },
        "semester" : {
          "type" : "text"
        },
        "student_count" : {
          "type" : "integer"
        },
        "submit_date" : {
          "type" : "date",
          "format" : "yyyy-MM-dd"
        },
        "title" : {
          "type" : "text"
        },
        "unit" : {
          "type" : "integer"
        }
      }
    },
    "settings" : {
      "index" : {
        "creation_date" : "1595079341822",
        "number_of_shards" : "1",
        "number_of_replicas" : "1",
        "uuid" : "AfZgDelwTby9x5kodOGaRg",
        "version" : {
          "created" : "7080099"
        },
        "provided_name" : "classes"
      }
    }
  }
}
```




## 매핑 파일

### classesRating_mapping.json 

[허민석교수님 github](https://github.com/minsuk-heo/BigData/blob/master/ch01/classesRating_mapping.json) 에서 다운로드 가능하다. 원본 자료에서 string -> text로 변경해주어야 한다. vi 에디터의 문자열 치환 기능(ex. %s/string/text/g)으로 string -> text 로 변경한 json 파일의 내용은 아래와 같다.  

```json
{
	"class" : {
		"properties" : {
			"title" : {
				"type" : "text"
			},
			"professor" : {
				"type" : "text"
			},
			"major" : {
				"type" : "text"
			},
			"semester" : {
				"type" : "text"
			},
			"student_count" : {
				"type" : "integer"
			},
			"unit" : {
				"type" : "integer"
			},
			"rating" : {
				"type" : "integer"
			},
			"submit_date" : {
				"type" : "date",
				"format" : "yyyy-MM-dd"
			},
			"school_location" : {
				"type" : "geo_point"
			}
		}
	}
}
```



# 3. BULK INSERT

이제 샘플 데이터들을 bulk insert 해보자

## BULK insert

```bash
$ curl -XPOST http://localhost:9200/_bulk\?pretty -H 'Content-Type: application/json' --data-binary @classes.json

... 
# 출력결과
{
  "took" : 55,
  "errors" : false,
  "items" : [
    {
      "index" : {
        "_index" : "classes",
        "_type" : "class",
        "_id" : "1",
        "_version" : 1,
        "result" : "created",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 0,
        "_primary_term" : 1,
        "status" : 201
      }
    },
    // ... (중략) ...
    {
      "index" : {
        "_index" : "classes",
        "_type" : "class",
        "_id" : "25",
        "_version" : 1,
        "result" : "created",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 23,
        "_primary_term" : 1,
        "status" : 201
      }
    }
  ]
}
```



## 데이터 확인

데이터 타입에 맞게끔 INSERT 된것을 확인 가능하다.

```bash
$ curl -XGET http://localhost:9200/classes/class/1\?pretty

# 출력결과
{
  "_index" : "classes",
  "_type" : "class",
  "_id" : "1",
  "_version" : 1,
  "_seq_no" : 0,
  "_primary_term" : 1,
  "found" : true,
  "_source" : {
    "title" : "Machine Learning",
    "Professor" : "Minsuk Heo",
    "major" : "Computer Science",
    "semester" : [
      "spring",
      "fall"
    ],
    "student_count" : 100,
    "unit" : 3,
    "rating" : 5,
    "submit_date" : "2016-01-02",
    "school_location" : {
      "lat" : 36.0,
      "lon" : -120.0
    }
  }
}
```



# 트러블 슈팅

[mapping 에러시 해결방법 - inflearn](https://www.inflearn.com/questions/12385)

- elasticsearch 7.x 부터는 curl 리퀘스트시 헤더를 명확히 설정해주어야 한다.  
  (Content-Type: application/json)

- elasticsearch 7.x 부터는 include_type_name을 true로 설정해주어야 한다.

  - ex)

  - ```bash
    curl -H 'Content-Type:application/json' -XPUT 'http://localhost:9200/classes/class/_mapping?include_type_name=true&pretty' -d @classesRating_mapping.json
    ```

- type 들중 string으로 되어 있는 부분들은 type으로 설정해주어야 한다. (7.x 버전사용할 경우)

  - [관련 링크 - stackvoerflow](https://stackoverflow.com/questions/47452770/no-handler-for-type-string-declared-on-field-name)

