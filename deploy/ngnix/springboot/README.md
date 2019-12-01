# nginx + Spring Boot

처음 정리하려고 할때 linux 에서 설치하듯이 하려 했었다. 하지만 치명적인 하나의 실수가 있었으니 www-data 계정을 생성해야 한다는 점이었다. 그것도 nologin상태로...

배포환경이 아닌 개발환경에서 맥os위에서 www-data계정을 해킹하듯이 만든후 이렇게 까지 할필요 있나 싶어서 docker로 정리하고자 한다.  


# 참고자료

- 생활코딩  
  https://www.opentutorials.org/module/384/4511  

- spring boot with nginx  
  https://dptablo.tistory.com/235  

- nginx 공식 메뉴얼  
  http://nginx.org/en/docs/install.html

  

# 예제 범위

간단한 spring boot hello world 페이지를 nginx위에서 동작하도록 해본다.



with_docker.md 파일 참조하자.