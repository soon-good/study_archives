# gradle 안될때 여러가지 해결책

## 1) build 꼬일때
이건 나중에 정리하자... ㅠㅜ

- unlink후 .idea 폴더 삭제후 프로젝트 재오픈
- reimport
- 가끔은 프록시도 문제가 된다. (개발환경 OS에 http_proxy 잠깐 추가했다거나 등등)
- 추후 생각나는데로 계속 추가할 예정



## 2) gradle 버전 관련해서 IDE에서 지랄할때 
> 에러 메시지
>
> - Spring Boot plugin requires Gradle 5 (5.6.x only) or Gradle 6 (6.3 or later). The current version is Gradle 6.0.1
> - 원인은...
>   - Intellij 에서 참조하는 Gradle 버전은 Gradle 6.0.1 이다. 프로젝트 루트에 gradle-wrapper.properties 에 기본으로 명시된 Gradle 버전은 6.0.1 이기 때문이다.  
>     (이건 start.spring.io 에서 제공하는 spring initializer 에서 spring boot 프로젝트를 다운받았을 때 기본으로 지정되어 다운 되는 버전. 사용자가 직접 편집해도 무방하다.)  
>   - gradle-wrapper.properties 에 명시한 gradle-xxxx.zip은 intellij에서 빌드시 사용하는 gradle 버전이다.
>   - 이것을 고치려고 로컬 개발환경(PC)의 환경변수에 있는 Gradle 바이너리 디렉터리를 5.6.x버전으로 아무리 고쳐봐야 Intellij에서 빌드는 실패한다. 그 이유는 intellij에서 빌드시에는 OS에 전역으로 설치한 gradle이 아닌 gradlew를 사용하기 때문으로 보인다.(IDE에서 빌드하는 도구는 따로 있기 때문)
> - 해결책은...  
>   - gradle-wrapper.properties에 명시된 gradle 버전을 맞춰준다.

  

- 인텔리제이에서 Shift 두번을 눌러서 gradle-wrapper.properties 파일 검색하거나
- 프로젝트 루트에서 /gradle/wrapper/gradle-wrapper.properties 파일을 찾는다

예) gradle-wrapper.properties 파일

```properties
#Mon Mar 23 23:14:15 KST 2020
### 이 부분을 5.x 버전으로 고쳐준다. 또는 6.0.3 버전 이후로 맞춰줘도 좋다.
distributionUrl=https\://services.gradle.org/distributions/gradle-5.6.2-all.zip
### 수정 전 
# distributionUrl=https\://services.gradle.org/distributions/gradle-6.0.1-all.zip
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
zipStorePath=wrapper/dists
zipStoreBase=GRADLE_USER_HOME

```

  

> distributionUrl=https\://services.gradle.org/distributions/gradle-6.0.1-all.zip

로 되어 있는 것을  

  

> distributionUrl=https\://services.gradle.org/distributions/gradle-5.6.2-all.zip

와 같이 5.6.x 버전대로 맞춰 주거나, 또는 6.0.3 버전 이후로 맞춰주자.  



버전명에 대한 zip 파일들의 리스트는 아래에서 찾을 수 있다.

- [Gradle Distributions](https://services.gradle.org/distributions/)



### 참고자료  
[intellij 공식 Q&A](https://intellij-support.jetbrains.com/hc/en-us/community/posts/360000029630-Please-update-Gradle-plugin-for-latest-)

