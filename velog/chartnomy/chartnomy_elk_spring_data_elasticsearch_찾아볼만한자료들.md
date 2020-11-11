# (chartnomy) spring data elasticsearch 관련 참고할 만한 자료들



이름을 참고할 만한 자료들이라고 하니 참 뭔가 이상하다. 나중에 제목을 바꾸던지 할 것 같다. 이번 글에서는 Spring/Spring Boot 기반의 앱을 구현할 때 Elasticsearch 를 연동해야 할때 참고할 만한 자료들을 정리하려고 한다. 참고 자료들을 용도에 따라 분리해서 정리해보면 아래와 같다.



- 스프링 레퍼런스 페이지

  - [spring-data/elasticsearch](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#reference)
  - 스프링 부트 초기 설정시 참고할 만한자료이다.
  - 설정 파일 작성 방식, 타입 매핑, 오퍼레이션, 리포지터리 코드 등 에 대해서 다루고 있다.
  - 나의 경우는 빠르게 초기 설정을 구성해서 테스트 해보는 과정까지는 이 문서가 도움이 많이 되었다. 
  - 추후 더 도움이 될 만한 내용들을 여기서 요약적으로 파악할 수 있지 않을까하는 생각이다.

- Elastic.co 제공 공식 도큐먼트

  - [Java High Level REST Client](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.9/java-rest-high.html)
    - [Document APIs](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.9/java-rest-high-supported-apis.html)
      - Single document APIs
      - Multi document APIs
        - [Bulk API](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.9/java-rest-high-document-bulk.html)
        - [Multi-Get API](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.9/java-rest-high-document-multi-get.html)
        - Reindex API
        - Update By Query API
        - Rethrottle API
        - Multi Term Vectors API
    - [Index APIs](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.9/_index_apis.html)
    - [Search APIs](https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.9/_search_apis.html)
    - ...

  - 스프링 공식문서를 보고 초기 설정을 마쳤다면 Elastic.co 제공 도큐먼트를 보고 필요한 내용을 찾아볼 준비가 되었다고 할 수 있다.
  - 스프링 레퍼런스 페이지만을 보다가... 벌크 INSERT 등에 대해서 찾고 싶은데 아... 뭔가 너무 아쉬웠다. 그래서 예전에 에버노트에 정리한 내용들을 찾아보니 Elastic.co 자료가 있었다.
  - Elastic.co 자료를 초반에 보기에는 굉장히 무리이다. 내가 만들려는 것이 뭔지 까먹게 되고, 답답한 마음만 들게 될 수 있다.



