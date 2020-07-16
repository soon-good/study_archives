# Elastic Search + Spring Boot 

주식에서 쓰이는 차트와 유사한 형태를 만들고 있는 중인데, 한번에 가져오는 데이터의 양이 많거나, 조인이 비교적 많은 데이터에 사용하게 될 경우에 관계형 DB로는 무리가 될 수도 있다.  

데이터의 성격이 기록된 데이터가 미래에 변경되지 않을 성격인 주식가격 등에 대한 데이터이기 때문에 데이터의 수정이 필요가 없다. 데이터의 수정이 많을 경우 트리 구조의 데이터를 사용하는 경우 불리해지기 때문에 그런 경우는 elastic search 등을 사용하는 것을 재고할 필요가 있다.  

이런 이유로 Elastic Search를 Spring Boot 에서 적용하는 예제를 남겨보고자 한다. 초반에는 json 파일 기반으로 예제를 수행할 예정이다.

## 참고자료

- [Spring data ElasticSearch 사용해보기, 탁구치는 개발자](https://lng1982.tistory.com/284)
- [Spring Boot ElasticSearch 연결 및 간단 CRUD, wedul](https://wedul.site/576)



# 1. Overview

일단 Elastic Search를 로컬에 설치해야 한다. 이 부분에 대해서는 docker로 설치할 예정이다. Spring Boot와 Elastic Search를 연동하는 것은 Spring Data JPA를 이용해 연결 가능하다. 쿼리 자체가 필요없어지기도 해서 어느 정도는 간편해지는 면이 있는 것 같다.  



# 2. docker를 이용한 Elastic Search 설정



# 3. Elastic Search Spring 설정





# 4. Spring Data JPA 연동









