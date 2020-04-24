# OS 환경변수를 application yml 파일에 적용하기

외부 API 연동을 위해 API 키가 필요하거나 그런 경우가 있다. 이 경우, application.yml 또는 application.properties 파일에 모두 입력해놓고 사용하는 것도 가능하다. 일반적으로 생각할 수 있는 보편적인 방법이긴 하다. 하지만 일단, application.properties, application.yml 에 포함시키는 방법은 추천되지 않는 편이다. 공개 github 리포지터리 같은 곳에 API Key 가 있으면, 비트코인 채굴 등에 활용될 수 있는 문제가 있기 때문이다.  

물론, 실제 현업에서 DB에 접근하는 앱을 공개 리포지터리를 활용하는 경우가 있는 경우는 많지 않지만, 가끔 필요한 경우도 있긴 하다. 외부 API 연동 앱 같은 것들을 협업으로 하는 경우도 있을수 있으니깐...

  

하지만, OS에 환경변수로 URL이나, API Key, Database 비밀번호 등을 입력해놓고 사용하는 경우도 있다. 조금 삼천포 이긴 하지만, django 기반의 웹 프레임워크에서는 통상적으로 권장되는 방식으로 OS 환경변수에 key/value 들을 두고 이것을 바인딩해줄 수 있도록 해준다. Spring 역시 이런 방법이 가능한지 찾아보니... 있다.    



물론 다른 방법도 있다. properties 파일을 중첩해서 변수처럼 활용하고 비밀번호나 시크릿 등에 포함된 properties 파일은 .gitignore에 추가해놓는 방식..도 있다. 이건 나중에 다뤄보기로 하자.



서론이 길었다. 

# SPRING_APPLICATION_JSON

https://www.latera.kr/reference/java/2019-09-29-spring-boot-config-externalize/



```bash
SPRING_APPLICATION_JSON='{"acme": {"name": "test"}}'
```







