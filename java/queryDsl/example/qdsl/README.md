# gradle 안될때 여러가지 해결책

## 1) build 꼬일때
이건 나중에 정리하자... ㅠㅜ

## 2) gradle 버전 관련해서 IDE에서 지랄할때 
> Spring Boot plugin requires Gradle 5 (5.6.x only) or Gradle 6 (6.3 or later). The current version is Gradle 6.0.1
해결책은...  
> gradle/wrapper/gradle-wrapper.properties 의 내용에서  
> distributionUrl=https\://services.gradle.org/distributions/gradle-5.6.2-all.zip
로 바꿔준다. 6.0.1.zip -> 5.6.2.zip으로 변경해준다. 아무리 로컬 개발환경의 gradle버전을 맞춰줘도무 IDE내의 gradle버전은 그대로다.  
IDE 에서 빌드하는 도구는 따로 있기때문..

### 참고자료  
[intellij 공식 Q&A](https://intellij-support.jetbrains.com/hc/en-us/community/posts/360000029630-Please-update-Gradle-plugin-for-latest-)

