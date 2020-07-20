# Elastic Search 자료 모음

# 1. docker container elasticsearch

- [docker hub - elasticsearch](https://hub.docker.com/_/elasticsearch)
- [elasticsearch 공식문서](https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html)
  - volume 마운트 관련해서 찾아보다가 공식문서를 접했다...
  - docker hub 내의 명령어들은 자세하지 않다. 기본적인 명령어들만 구비되어 있다. elasticsearch 공식문서를 참고하자.
- [docker network rm](https://docs.docker.com/engine/reference/commandline/network_rm/)



# 2. kubernetes elasticsearch

- [ElasticSearch on Kubernetes](https://sematext.com/blog/kubernetes-elasticsearch/)



# 3. github

- [허민석 교수님  github](https://github.com/minsuk-heo/BigData)



# 4. books

- [일래스틱 스택 6 입문](http://www.yes24.com/Product/Goods/61155479?scode=032&OzSrank=2)
  - 480쪽 (조금 분량이 많은 책이다.)
  - [파라나브 슈클라](http://www.yes24.com/SearchCorner/Result?domain=ALL&author_yn=Y&query=&auth_no=218631), [샤랏 쿠마](http://www.yes24.com/SearchCorner/Result?domain=ALL&author_yn=Y&query=&auth_no=218632) 저/[장준호](http://www.yes24.com/SearchCorner/Result?domain=ALL&author_yn=Y&query=&auth_no=206578) 역 *|* [에이콘출판사](javascript:void(0);) *|* 2018년 05월 31일 
  - 이 책의 경우에는 실습시 안되는 부분들이 많아서 독자들이 블로그에 해당 내용들을 해결한 부분들을 코멘트?해놓고 있다. 
  - http://www.yes24.com/Product/Goods/69049660?scode=032&OzSrank=3
- [일래스틱서치 쿡북 3/e](http://www.yes24.com/Product/Goods/69049660?scode=032&OzSrank=3)
  - 868쪽 (분량이 굉장히 많은 책이다. )
  - [알베르토 파로](http://www.yes24.com/SearchCorner/Result?domain=ALL&author_yn=Y&query=&auth_no=247082) 저/[이재익](http://www.yes24.com/SearchCorner/Result?domain=ALL&author_yn=Y&query=&auth_no=191778), [최중연](http://www.yes24.com/SearchCorner/Result?domain=ALL&author_yn=Y&query=&auth_no=191779) 역 *|* [에이콘출판사](javascript:void(0);) *|* 2019년 01월 31일 *|*
  - 리뷰가 없는 책이다. 양이 많아서 다들 안읽어본듯...하다.
- [일래스틱서치 모니터링](http://www.yes24.com/Product/Goods/38950544)
  - 200쪽
  - [댄 노블](http://www.yes24.com/SearchCorner/Result?domain=ALL&author_yn=Y&query=&auth_no=248418) 저/[이재익](http://www.yes24.com/SearchCorner/Result?domain=ALL&author_yn=Y&query=&auth_no=191778) 역 *|* [에이콘출판사](javascript:void(0);) *|* 2017년 04월 27일 *|* 원서 : [Monitoring Elasticsearch](javascript:void(0);)



# 5. 트러블 슈팅

- [ElasticSearch REST 요청시 application/x-www-form-urlencoded is not supported 에러가 날때 해결법](https://abc2080.tistory.com/entry/%EC%97%90%EB%9F%AC-ContentType-header-applicationxwwwformurlencoded-is-not-supported)
- [mapping 에러시 해결방법 - inflearn](https://www.inflearn.com/questions/12385)
  - elasticsearch 7.x 부터는 curl 리퀘스트시 헤더를 명확히 설정해주어야 한다.  
    (Content-Type: application/json)
  - include_type_name을 true로 설정해주어야 한다.
  - type 들중 string으로 되어 있는 부분들은 type으로 설정해주어야 한다. (7.x 버전사용할 경우)
    - [관련 링크 - stackvoerflow](https://stackoverflow.com/questions/47452770/no-handler-for-type-string-declared-on-field-name)



