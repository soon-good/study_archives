# docker 기반 개발용 Logstash 설치

# 0. 참고자료

- [docker hub repository - logstash](https://hub.docker.com/_/logstash)
- [Running Logstash on Docker](https://www.elastic.co/guide/en/logstash/current/docker.html)
- [Configuring Logstash for Docker](https://www.elastic.co/guide/en/logstash/current/docker-config.html)
  - 가장 자세한 자료.. ㅠㅜ
- [www.docker.elastic.co](https://www.docker.elastic.co/)



# 1. LOGSTASH란?

ELK 스택에서 LOGSTASH 는 INPUT을 담당한다.

![이미자](./img/LOGSTASH_OVERVIEW_1.png)

LOGSTASH로 받은 INPUT은 변환되어 elasticsearch로 들어가고, Kibana는 elasticsearch의 데이터를 조회해서 시각화한다.



# 2. docker network

## docker network 생성

net-es-chartnomy 라는 이름의 docker network를 생성한다.

```bash
$ docker network create net-es-chartnomy
```

## docker network 삭제

```bash
$ docker network rm net-es-chartnomy
```



# 3. docker 기반 logstash container 구동

## directory 생성

```bash
# logstash 볼륨 연결을 위한 디렉터리 생성
$ mkdir -p ~/logstash/chartnomy/pipeline
$ mkdir -p ~/logstash/chartnomy/config

# 주요 설정 파일들 생성
$ touch ~/logstash/chartnomy/config/logstash.yml
$ touch ~/logstash/chartnomy/config/logstash-sample.conf
```



## logstash container 구동

```bash
$ docker container run --rm -it --name logstash-chartnomy -d -v ~/logstash/chartnomy/pipeline:/usr/share/logstash/pipeline/ -v ~/logstash/chartnomy/config/logstash.yml:/usr/share/logstash/config/logstash.yml -v ~/logstash/chartnomy/config/logstash-sample.conf:/usr/share/config/logstash-sample.conf docker.elastic.co/logstash/logstash:7.8.0

# 출력결과 
af87d38750cbcbd7a6ede19cb42d4d3c8377bd15b925d6230fbbd3fd8bb55f85
```



## logstash container 조회

```bash
$ docker container ls --filter name=logstash-chartnomy
```



## logstash container 삭제

```bash
$ docker container stop logstash-chartnomy
```



# 4. 환경설정 파일들

## logstash.yml




## logstash-sample.conf

```bash
$ vim ~/logstash/chartnomy/pipeline/logstash-simple.conf

input { 
	stdin { } 
}
output {
	stdout { }
}	

:wq
```







