# ElasticSearch METRIC AGGREGATION

ElasticSearch 의 Aggregation 은 아래와 같이 네가지의 Aggregation을 지원한다.

- Bucketing
- Metric
- Matrix
- Pipeline

오늘 살펴볼 것은 Metric Aggregation 이다.

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

이중 Metric Aggregation은 산술적인 값을 기준으로 group By 하여 SUM, AVG, MIN, AVG 값을 도출해낼 때 사용하는 연산이다.

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



## 예제 데이터 준비

샘플 데이터는 아래와 같다. [허민석 교수님 github](https://github.com/minsuk-heo/BigData/blob/master/ch03/simple_basketball.json)에서 다운로드 가능하다.

```json
{ "index" : { "_index" : "basketball", "_type" : "record", "_id" : "1" } }
{"team" : "Chicago Bulls","name" : "Michael Jordan", "points" : 30,"rebounds" : 3,"assists" : 4, "submit_date" : "1996-10-11"}
{ "index" : { "_index" : "basketball", "_type" : "record", "_id" : "2" } }
{"team" : "Chicago Bulls","name" : "Michael Jordan","points" : 20,"rebounds" : 5,"assists" : 8, "submit_date" : "1996-10-11"}
```



## 예제 데이터 Bulk Insert

```bash
$ curl -XPOST http://localhost:9200/_bulk\?pretty -H 'Content-Type: application/json' --data-binary @simple_basketball.json
...
# 출력결과
{
  "took" : 119,
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
    }
  ]
}
```



# 2. Metric Aggregation

[공식 문서](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations.html#search-aggregation)에 명시된 문구를 인용하면 아래와 같다.

> Aggregations that keep track and compute metrics over a set of documents.

개념적인 부분은 나중에 정리한다고 하고... 일단 쉽게 설명하자면, Metric Aggregation은 산술연산(MIN,MAX,AVG,SUM)을 할때 사용하는 연산이다.

## AVG 연산

AVG를 위한 json 파일의 내용은 아래와 같다. [허교수님 github](https://github.com/minsuk-heo/BigData/blob/master/ch03/avg_points_aggs.json)에서 다운로드 가능하다.

### AVG 연산 json

**avg_points_aggs.json**

```json
{
	"size" : 0,
	"aggs" : {
		"avg_score" : {
			"avg" : {
				"field" : "points"
			}
		}
	}
}
```

- size
  - 결과값 중 보고싶은 것만 보려 할 때 size를 지정한다.
- aggs
  - aggregations 라는 의미이다.
- avg_score
  - aggregation 명을 "avg_score"으로 지정해줬다.
- avg
  - 평균값을 구하겠다는 의미
- "field" : "points"
  - point라는 항목에 대해서 avg를 구하겠다고 명시



### AVG 연산 실행

```bash
$ curl -XGET http://localhost:9200/_search\?pretty -H 'Content-Type: application/json' --data-binary @avg_points_aggs.json

# 출력결과
{
  "took" : 31,
  "timed_out" : false,
  "_shards" : {
    "total" : 2,
    "successful" : 2,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 26,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  },
  "aggregations" : {
    "avg_score" : {
      "value" : 25.0
    }
  }
}
```



## MAX 연산

### MAX 연산 json

**max_points_aggs.json**

```json
{
	"size" : 0,
	"aggs" : {
		"max_score" : {
			"max" : {
				"field" : "points"
			}
		}
	}
}
```

각 필드의 성격은 AVG 연산에 정리한 것과 대동소이하므로 설명은 패스



### MAX 연산 실행

```bash
$ curl -XGET http://localhost:9200/_search\?pretty -H 'Content-Type: application/json' --data-binary @max_points_aggs.json

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
      "value" : 26,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  },
  "aggregations" : {
    "max_score" : {
      "value" : 30.0
    }
  }
}
```



## MIN 연산

### MIN 연산 json

```json
{
	"size" : 0,
	"aggs" : {
		"min_score" : {
			"min" : {
				"field" : "points"
			}
		}
	}
}
```

각 필드의 성격은 AVG 연산에 정리한 것과 대동소이하므로 설명은 패스



### MIN 연산 실행

```bash
$ curl -XGET http://localhost:9200/_search\?pretty -H 'Content-Type: application/json' --data-binary @min_points_aggs.json
# ...
# 출력결과 
{
  "took" : 3,
  "timed_out" : false,
  "_shards" : {
    "total" : 2,
    "successful" : 2,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 26,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  },
  "aggregations" : {
    "min_score" : {
      "value" : 20.0
    }
  }
}
```



## SUM 연산

### SUM 연산 json

**sum_points_aggs.json**

```json
{
	"size" : 0,
	"aggs" : {
		"sum_score" : {
			"sum" : {
				"field" : "points"
			}
		}
	}
}
```

각 필드의 성격은 AVG 연산에 정리한 것과 대동소이하므로 설명은 패스



### SUM 연산 실행

```bash
$ curl -XGET http://localhost:9200/_search\?pretty -H 'Content-Type: application/json' --data-binary @sum_points_aggs.json

# ...
# 출력결과
{
  "took" : 3,
  "timed_out" : false,
  "_shards" : {
    "total" : 2,
    "successful" : 2,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 26,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  },
  "aggregations" : {
    "sum_score" : {
      "value" : 50.0
    }
  }
}
```

## STATS 연산

AVG, MIN, MAX, SUM 연산을 한꺼번에 확인하고 싶을 때는 STATS 연산을 사용하면 된다. 

### STATS 연산 json

```json
{
	"size" : 0,
	"aggs" : {
		"stats_score" : {
			"stats" : {
				"field" : "points"
			}
		}
	}
}
```



### STATS 연산 실행

```bash
$ curl -XGET http://localhost:9200/_search\?pretty -H 'Content-Type: application/json' --data-binary @stats_points_aggs.json

# ...
# 출력결과
{
  "took" : 3,
  "timed_out" : false,
  "_shards" : {
    "total" : 2,
    "successful" : 2,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {
    "total" : {
      "value" : 26,
      "relation" : "eq"
    },
    "max_score" : null,
    "hits" : [ ]
  },
  "aggregations" : {
    "stats_score" : {
      "count" : 2,
      "min" : 20.0,
      "max" : 30.0,
      "avg" : 25.0,
      "sum" : 50.0
    }
  }
}
```





# 참고자료

- [ElasticSearch Reference > Aggregations](https://www.elastic.co/guide/en/elasticsearch/reference/current/search-aggregations.html#search-aggregation)



