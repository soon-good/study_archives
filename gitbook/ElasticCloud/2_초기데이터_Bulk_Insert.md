# 초기 데이터 Bulk Insert

> 한국은행 경제 통계시스템에서 제공하는 KOSPI 데이터를 예제로 해서 ELK 인스턴스에 Bulk Insert 하는 과정이다.  

  

gitbook에 정리하기 위해 이 문서를 만들기 시작했는데, lognomy 프로젝트를 진행하면서 그때 그때 해결했던 내용들과 하나씩 해왔던 내용들을 작업일지로 정리해보려 한다. 

- gitbook url
  - [https://gosgjung.gitbook.io/lognomy/lognomy/elastic-cloud/bulk-insert-kospi](https://gosgjung.gitbook.io/lognomy/lognomy/elastic-cloud/bulk-insert-kospi)

  

# 들어가기 전에

아직 스프링 부트의 데이터 소스와 결합해서 테스트해보지 않았다. 데이터가 있어야 스프링 부트와 연동하는 데이터 소스가 잘 동작하는지 확인 가능할 것 같았다. 이런 이유로 CURL 커맨드를 활용해 초기 데이터를 INSERT 하는 작업을 수행했다. 

일래스틱 클라우드에 인덱스를 생성하고 데이터 자료형을 매핑하는 방식은 [LOGNOMY/Elastic Cloud 세팅작업들/INDEX 생성과 매핑](https://app.gitbook.com/@gosgjung/s/lognomy/lognomy/elastic-cloud/index-kospi) 에 정리해두었다. 

- python의 urllib3 및 기타 로직을 통해 Bulk 데이터 생성
- json 형식의 초기 벌크 데이터 형식을 만드는 과정

에 대해서는  [크롤링작업들/KOSPI Bulk 데이터 생성](https://app.gitbook.com/@gosgjung/s/lognomy/~/drafts/-MLX8QEa8sXF6hTBjrV-/lognomy/python-node.js/kospi-bulk-json) 에 정리해두었다.

  

# 초기 데이터 Bulk Insert

ELK 인스턴스는 아래 명령을 이 페이지에 정리한 이후에 삭제하고 새로 생성했다. ID/PW 가 누출될것 같아서이기도 하고, 굳이 ElasticCloud 를 공개할 필요는 없어서이다.

```bash
curl -X POST -H 'Content-Type: application/json' \
--user elastic:ZMDnxlVzyQkGQwrYDOh9nkl9 \
https://a2580127b5fa47a783245a38d16c6a76.ap-northeast-2.aws.elastic-cloud.com:9243/kospi/_bulk?pretty \
--data-binary @kospi_data_20201107_isoformat.json
```



위의 명령어를 자세히 보면 json 파일을 지정하고 있다. Elastic Cloud에 Curl 커맨드로 데이터 INSERT 작업을 하려면 Elastic Cloud 가 인식할 수 있는 데이터 형식을 JSON으로 만들어서 커맨드로 전달해야만 한다.   

이러한 JSON 파일을 만드는 절차에 대해서는 아래에 정리해두었다

- [LOGNOMY/크롤링작업들/KOSPI BULK 데이터 생성](https://app.gitbook.com/@gosgjung/s/lognomy/lognomy/python-node.js/kospi-bulk-json)

