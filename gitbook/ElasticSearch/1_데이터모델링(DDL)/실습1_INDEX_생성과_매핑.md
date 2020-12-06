# INDEX 생성과 매핑

> KOSPI 데이터를 예제로 해서 INDEX 를 생성하고 매핑해보자.

  

gitbook에 정리하기 위해 이 문서를 만들기 시작했는데, lognomy 프로젝트를 진행하면서 그때 그때 해결했던 내용들과 하나씩 해왔던 내용들을 작업일지로 정리해보려 한다. 

- gitbook url
  - [https://gosgjung.gitbook.io/lognomy/lognomy/elastic-cloud/index-kospi](https://gosgjung.gitbook.io/lognomy/lognomy/elastic-cloud/index-kospi)



# INDEX 삭제 (KOSPI 데이터)

이미 KOSPI 데이터가 생성되어 있는 경우 삭제하고 다시 만들어야 하는 경우 키바나 콘솔 > Dev Tools 에서 아래의 명령을 주어서 삭제해주면 된다.

```bash
DELETE /kospi
```



# INDEX 생성과 매핑 (KOSPI 데이터)

INDEX 를 생성하면서 동시에 데이터의 타입을 매핑해주는 절차이다. INDEX 를 생성하고 나서 타입을 매핑하는 과정에 대해서는 자세히 설명된 곳이 없어서 포기했었다.

Elastic Cloud 에 접속해서 키바나 콘솔을 열고 아래와 같이 입력하자.

```bash
PUT /kospi
{
    "mappings": {
        "properties": {
            "UNIT_NAME": {
                "type": "text"
            },
            "STAT_NAME": {
                "type": "text"
            },
            "ITEM_CODE1": {
                "type": "text"
            },
            "STAT_CODE": {
                "type": "text"
            },
            "ITEM_CODE2": {
                "type": "text"
            },
            "ITEM_CODE3": {
                "type": "text"
            },
            "ITEM_NAME1": {
                "type": "text"
            },
            "ITEM_NAME2": {
                "type": "text"
            },
            "DATA_VALUE": {
                "type": "double"
            },
            "ITEM_NAME3": {
                "type": "text"
            },
            "TIME": {
                "type": "date",
                "format": "yyyy-MM-dd'T'HH:mm:ss||yyyy-MM-dd||epoch_millis||yyyyMMdd"
            }
        }
    }
}
```

