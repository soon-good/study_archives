# ElasticSearch BUCKET AGGREGATION

ElasticSearch 의 Aggregation 은 아래와 같이 네가지의 Aggregation을 지원한다.

- Bucketing
- Metric
- Matrix
- Pipeline

오늘 살펴볼 것은 Bucket Aggregation 이다.

# 1. AGGREGATION 이란?

[공식 가이드 문서](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations.html#search-aggregations)의 설명을 인용해보면 아래와 같다.

> The aggregations framework helps provide aggregated data based on a search query. It is based on simple building blocks called aggregations, that can be composed in order to build complex summaries of the data.  
>
> There are many different types of aggregations, each with its own purpose and output. To better understand these types, it is often easier to break them into four main families:  
>
> - [*Bucketing*](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-bucket.html)  
> - [*Metric*](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-metrics.html)
> - [*Matrix*](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-matrix.html)  
> - [*Pipeline*](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations-pipeline.html)

여기서부터는 뇌피셜이다. 위의 많은 글들을 읽어보기에는 아직은 시간이 많지 않다. 일단 내 식대로 이해한 뇌피셜을 정리해보면 아래와 같다. 순전히 뇌피셜로만 조미료처럼 이해해보기위해 순전히 주관적인 추측만을 이용해 쉽게 정리해보면 아래와 같다.

Aggregation은 group by등을 통해서 SUM, AVG, MIN, MAX 등의 값을 도출해낸다. 보통의 DB들도 보통은 이러한 연산을 통해 값을 도출해내는 편이다.  

이중 Bucket Aggregation은 다큐먼트의 bucket을 기준으로 group By 하여 SUM, AVG, MIN, AVG 값을 도출해낼 때 사용하는 연산이다. 예를 들자면 농구팀별 최대score, 최소 score, 평균 score 등을 구하는 경우를 예로 들 수 있다.    

Aggregation 의 포맷은 일반적으로 아래와 같다고 한다.  

```json
"aggregations": {
  "<aggregation_name>": {
    "<aggregation_type>": {
      <aggregation_body>
    }
    [, "meta": {[<meta_data_body]}] ?
  	[, "aggregations": {[<sub_aggregation>]+}]?
  }
	[, "<aggregation_name_2>": {...} ]*
}
```



# 2. 예제 데이터 준비

## Basketball index 생성

```bash
$ curl -XPUT http://localhost:9200/basketball\?pretty -H 'Content-Type: application/json'
{
  "acknowledged" : true,
  "shards_acknowledged" : true,
  "index" : "basketball"
}
```



## Basketball mapping 생성

### 샘플 mapping 데이터

샘플 데이터는 아래와 같다. 예제 데이터는 [허교수님 github](https://github.com/minsuk-heo/BigData/blob/master/ch04/basketball_mapping.json) 에서 다운로드 할 수 있다. 예제 mapping 파일내의 string 타입은  7.8.x 버전에서 에러를 낸다. 이런 이유로 vi 에디터의 치환기능으로 string -> text로 타입을 변경해주었다. (ex. :%s/string/text/g)

```json
{
	"record" : {
		"properties" : {
			"team" : {
				"type" : "string",
				"fielddata" : true
			},
			"name" : {
				"type" : "string",
				"fielddata" : true
			},
			"points" : {
				"type" : "long"
			},
			"rebounds" : {
				"type" : "long"
			},
			"assists" : {
				"type" : "long"
			},
			"blocks" : {
				"type" : "long"
			},
			"submit_date" : {
				"type" : "date",
				"format" : "yyyy-MM-dd"
			}
		}
	}
}
```



### 매핑 생성

```bash
$ curl -XPUT http://localhost:9200/basketball/record/_mapping\?include_type_name\=true\&pretty -H 'Content-Type: application/json' -d @basketball_mapping.json

# 출력결과 ...
{
  "acknowledged" : true
}
```



## 예제 Document 데이터 생성

### 예제 데이터 다운로드

예제 데이터는 [허교수님 github](https://github.com/minsuk-heo/BigData/blob/master/ch04/twoteam_basketball.json) 에서 다운로드 가능하다.

**twoteam_basketball.json**

```json
{ "index" : { "_index" : "basketball", "_type" : "record", "_id" : "1" } }
{"team" : "Chicago","name" : "Michael Jordan", "points" : 30,"rebounds" : 3,"assists" : 4, "blocks" : 3, "submit_date" : "1996-10-11"}
{ "index" : { "_index" : "basketball", "_type" : "record", "_id" : "2" } }
{"team" : "Chicago","name" : "Michael Jordan","points" : 20,"rebounds" : 5,"assists" : 8, "blocks" : 4, "submit_date" : "1996-10-13"}
{ "index" : { "_index" : "basketball", "_type" : "record", "_id" : "3" } }
{"team" : "LA","name" : "Kobe Bryant","points" : 30,"rebounds" : 2,"assists" : 8, "blocks" : 5, "submit_date" : "2014-10-13"}
{ "index" : { "_index" : "basketball", "_type" : "record", "_id" : "4" } }
{"team" : "LA","name" : "Kobe Bryant","points" : 40,"rebounds" : 4,"assists" : 8, "blocks" : 6, "submit_date" : "2014-11-13"}
```



### 예제 데이터 BULK INSERT

```bash
$ curl -XPOST http://localhost:9200/_bulk\?pretty -H 'Content-Type: application/json' --data-binary @twoteam_basketball.json
{
  "took" : 8,
  "errors" : false,
  "items" : [
    {
      "index" : {
        "_index" : "basketball",
        "_type" : "record",
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
    {
      "index" : {
        "_index" : "basketball",
        "_type" : "record",
        "_id" : "2",
        "_version" : 1,
        "result" : "created",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 1,
        "_primary_term" : 1,
        "status" : 201
      }
    },
    {
      "index" : {
        "_index" : "basketball",
        "_type" : "record",
        "_id" : "3",
        "_version" : 1,
        "result" : "created",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 2,
        "_primary_term" : 1,
        "status" : 201
      }
    },
    {
      "index" : {
        "_index" : "basketball",
        "_type" : "record",
        "_id" : "4",
        "_version" : 1,
        "result" : "created",
        "_shards" : {
          "total" : 2,
          "successful" : 1,
          "failed" : 0
        },
        "_seq_no" : 3,
        "_primary_term" : 1,
        "status" : 201
      }
    }
  ]
}
```



# 3. Group By Team (Bucket Aggregation)

## Aggregation json 생성

Aggregation 예제 데이터는 [허교수님 github](https://raw.githubusercontent.com/minsuk-heo/BigData/master/ch04/terms_aggs.json) 에서 다운로드 가능하다.

**terms_aggs.json**

```json
{
	"size" : 0,
	"aggs" : {
		"players" : {
			"terms" : {
				"field" : "team"
			}
		}
	}
}
```

- size : 0 
  - 여러개의 정보가 도출되도록 하지 않도록 하고, 원하는 aggregation 만 확인하기 위한 옵션
- "aggs"
  - "aggregations" 라고 써도 된다.
  - aggregation에 대한 옵션을 json 형태로 기술한다.
- aggregation 옵션들
  - aggregation 명은 players 로 지정했다.
  - 사용할 aggregation의 종류는 "term" 이다.
  - "team" 별로 document를 group by 하기 위해 group by 의 기준을 팀(team)으로 지정해주었다.

## Term Aggregation 실행

데이터를 확인해보자.

```bash
$ curl -XGET http://localhost:9200/_search\?pretty -H 'Content-Type: application/json' --data-binary @terms_aggs.json

# 출력결과 
{
  "took" : 51,
  "timed_out" : false,
  "_shards" : {
    "total" : 2,
    "successful" : 2,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 28,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  },
  "aggregations" : {
    "players" : {
      "doc_count_error_upper_bound" : 0,
      "sum_other_doc_count" : 0,
      "buckets" : [
        {
          "key" : "chicago",
          "doc_count" : 2
        },
        {
          "key" : "la",
          "doc_count" : 2
        }
      ]
    }
  }
}
```

데이터를 확인해보면 

- 시카고는 document가 2개 있고, 
  - "doc_count": 2
- LA는 document가 2개 있다고
  - "doc_count": 2

출력해주는 것을 확인 가능하다.



# 4. Stats Aggregation 과 Bucket Aggregation 을 조합해보기

## Aggregation json 생성

**stats_by_team.json** 

```json
{
	"size" : 0,
	"aggs" : {
		"team_stats" : {
			"terms" : {
				"field" : "team"
			},
			"aggs" : {
				"stats_score" : {
					"stats" : {
						"field" : "points"
					}
				}
			}
		}
	}
}
```

위의 json 파일의 연산을 정리해보면 아래와 같다.

- 팀별로 도큐먼트를 group by (Bucket Aggregation)한 후
- 각 팀별로 Stats 연산(Metric Aggregation)을 수행

## Aggregation 실행

```bash
$ curl -XGET http://localhost:9200/_search\?pretty -H 'Content-Type: application/json' --data-binary @stats_by_team.json
# ...
# 출력결과
{
  "took" : 6,
  "timed_out" : false,
  "_shards" : {
    "total" : 2,
    "successful" : 2,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 28,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  },
  "aggregations" : {
    "team_stats" : {
      "doc_count_error_upper_bound" : 0,
      "sum_other_doc_count" : 0,
      "buckets" : [
        {
          "key" : "chicago",
          "doc_count" : 2,
          "stats_score" : {
            "count" : 2,
            "min" : 20.0,
            "max" : 30.0,
            "avg" : 25.0,
            "sum" : 50.0
          }
        },
        {
          "key" : "la",
          "doc_count" : 2,
          "stats_score" : {
            "count" : 2,
            "min" : 30.0,
            "max" : 40.0,
            "avg" : 35.0,
            "sum" : 70.0
          }
        }
      ]
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







