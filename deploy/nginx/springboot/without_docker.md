# nginx + Spring Boot

 여기서는 apt-get 또는 yum과 같은 패키지 매니저로 nginx를 다운로드 받는 것보다는 직접 특정 버전의 nginx를 다운받아서 compile한 후에 사용하는 방식을 다뤄보고자 한다.   
추후 Dockerfile로 정리해봐야 겠다. 지금은 일단 Docker없이 직접 설정하는 과정을 다룬다.



# 참고자료

- 생활코딩  
  https://www.opentutorials.org/module/384/4511  
- spring boot with nginx  
  https://dptablo.tistory.com/235  
- nginx 공식 메뉴얼  
  http://nginx.org/en/docs/install.html

  

# 예제 범위

간단한 spring boot hello world 페이지를 nginx위에서 동작하도록 해본다.



# 필요 의존성 설치

nginx를 컴파일하기 위해서는 필수 의존성이 필요한데 해당 의존성들을 설치한다.

- PCRE
  Perl5 내의 정규표현식 라이브러리  
- openssl  
  https 모듈인 HttpSslModule 을 사용하기 위해 openssl 이 필요하다.
- zlib  
  ngx_http_gzip_module 이라는 이름의 모듈은 zlib 라이브러리가 있어야 한다고 한다.
- gcc, g++



## 의존성 파일 설치 디렉터리 설정

의존성들은 모두 dependency 디렉터리에 담아두고 컴파일시 참조하도록 할 예정이다.

```bash
$ mkdir dependency
```

  

## PCRE 설치

perl5의 정규표현식 라이브러리이다. 2019년도 라이브러리중 아무거나 선택했다. google에서 pcre ftp를 검색해 적당한 페이지를 찾아서 필요 소스의 링크주소를 복사하면 아래 명령어의 https://.... 의 링크가 된다.  

```bash
$ cd dependency
$ wget https://ftp.pcre.org/pub/pcre/pcre2-10.34.tar.gz
$ tar xvzf pcre2-10.34.tar.gz
```



## openssl 설치

2019년도에 대한 자료를 다운로드 받았다.  
[공식 페이지 - http://www/openssl.org/source](http://www/openssl.org/source)  

[1.0.2 버전](https://www.openssl.org/source/openssl-1.0.2t.tar.gz)  

```bash
$ cd dependency
$ wget https://www.openssl.org/source/openssl-1.0.2t.tar.gz
$ tar xvzf openssl-1.0.2t.tar.gz
```



## zlib 설치



```bash
$ cd dependency
$ wget http://zlib.net/zlib-1.2.11.tar.gz
$ tar xvzf zlib-1.2.11.tar.gz
```











