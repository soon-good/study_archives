# ElasticSearch, Kibana, Logstash 자료 모음

# 1. docker container elasticsearch

- [docker hub - elasticsearch](https://hub.docker.com/_/elasticsearch)
- [elasticsearch 공식문서](https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html)
  - volume 마운트 관련해서 찾아보다가 공식문서를 접했다...
  - docker hub 내의 명령어들은 자세하지 않다. 기본적인 명령어들만 구비되어 있다. elasticsearch 공식문서를 참고하자.
- [docker network rm](https://docs.docker.com/engine/reference/commandline/network_rm/)



# 2. docker kubernetes elasticsearch

- [Elastic.co - Beats 및 Elasticsearch를 이용한 Docker & Kubernetes 로그 수집 및 모니터링](https://www.elastic.co/kr/webinars/elasticsearch-log-collection-with-kubernetes-docker-and-containers)
- [데브원영 - ElasticSearch 와 Kibana & filebeat를 활용한 쿠버네티스 로깅 아키텍처](https://blog.voidmainvoid.net/153)
- [ElasticSearch on Kubernetes - helm 을 이용한 개발버전](https://sematext.com/blog/kubernetes-elasticsearch/)



# 3. docker 명령어 및 개념설명

- [가장 빨리 만나는 Docker - 목차](http://pyrasis.com/book/DockerForTheReallyImpatient/Chapter20/28)
  - 이미 책으로도 나와있기도 하지만, 블로그에는 요약적인 설명으로 굉장히 간편하게 되어 있다.
- [가장 빨리 만나는 Docker - run 명령어 및 옵션 목록들](http://pyrasis.com/book/DockerForTheReallyImpatient/Chapter20/28)
  - run 명령어들 중 --link 등과 같은 특수한 경우도 많은데, 이러한 옵션들에 대해 하나 하나 한땀 한땀 정리해두셨다.



# 4. docker-compose elasticsearch

## 공식자료

- [Install Elasticsearch with Docker](https://www.elastic.co/guide/en/elasticsearch/reference/current/docker.html) (다른 자료에 비해 굉장히 자세하다)
- Learn how to [configure Elasticsearch](https://www.elastic.co/guide/en/elasticsearch/reference/current/settings.html).
- Configure [important Elasticsearch settings](https://www.elastic.co/guide/en/elasticsearch/reference/current/important-settings.html).
- Configure [important system settings](https://www.elastic.co/guide/en/elasticsearch/reference/current/system-config.html).

## 블로그

- [ELK Docker 설치 방법](https://judo0179.tistory.com/60)
- [ELK 셋팅부터 알람까지 - 우아한형제들](https://woowabros.github.io/experience/2020/01/16/set-elk-with-alarm.html)



# 5. ElasticSearch 세부 설정들

- [2.3.2 elasticsearch.yml - Elastic 가이드북](https://esbook.kimjmin.net/02-install/2.3-elasticsearch/2.3.2-elasticsearch.yml)
- [ES-ETC #1 elasticsearch.yml 에서 설정하는 것들 - velog](https://velog.io/@jakeseo_me/ES-ETC-1-elasticsearch.yml%EC%97%90%EC%84%9C-%EC%84%A4%EC%A0%95%ED%95%98%EB%8A%94-%EA%B2%83%EB%93%A4)



# 6. ELK Stack, Kafka 연동

- [ELK Stack - Logstash를 이용한 로그 수집](https://coding-start.tistory.com/189)



# 7. 운영환경에서 ELK 환경 설치

- [ELKR (ElasticSearch + Logstash + Kibana + Redis) 를 이용한 로그분석 환경 구축하기](https://medium.com/chequer/elkr-elasticsearch-logstash-kibana-redis-%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-%EB%A1%9C%EA%B7%B8%EB%B6%84%EC%84%9D-%ED%99%98%EA%B2%BD-%EA%B5%AC%EC%B6%95%ED%95%98%EA%B8%B0-f3dd9dfae622)



# 8. ElasticSearch를 SpringBoot 에서 연동하기

- [Spring data ElasticSearch 사용해보기, 탁구치는 개발자](https://lng1982.tistory.com/284)
- [Spring Boot ElasticSearch 연결 및 간단 CRUD, wedul](https://wedul.site/576)



# 9. ELK Overview

- ElasticSearch
- Logstash
  - https://osc131.tistory.com/106
  - [[Elasticsearch] Logstash 설치와 기본개념](http://asuraiv.blogspot.com/2015/07/elasticsearch-logstash.html)
  - [LogStash 설치 on Docker!](https://pcconsoleoraksil.tistory.com/252)
  - [[Docker] Install Logstash](https://1226choi.tistory.com/20)
- Kibana



# 10. github

- [허민석 교수님  github](https://github.com/minsuk-heo/BigData)
- [github/deviantony/docker-elk](https://github.com/deviantony/docker-elk)



# 11. books

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



# 12. 트러블 슈팅

- [ElasticSearch REST 요청시 application/x-www-form-urlencoded is not supported 에러가 날때 해결법](https://abc2080.tistory.com/entry/%EC%97%90%EB%9F%AC-ContentType-header-applicationxwwwformurlencoded-is-not-supported)
- [mapping 에러시 해결방법 - inflearn](https://www.inflearn.com/questions/12385)
  - elasticsearch 7.x 부터는 curl 리퀘스트시 헤더를 명확히 설정해주어야 한다.  
    (Content-Type: application/json)
  - include_type_name을 true로 설정해주어야 한다.
  - type 들중 string으로 되어 있는 부분들은 type으로 설정해주어야 한다. (7.x 버전사용할 경우)
    - [관련 링크 - stackvoerflow](https://stackoverflow.com/questions/47452770/no-handler-for-type-string-declared-on-field-name)



