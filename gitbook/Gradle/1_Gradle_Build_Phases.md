# Gradle Build Phases

  

# 참고자료

[Build Lifecycledocs.gradle.org](https://docs.gradle.org/current/userguide/build_lifecycle.html#sec:build_phases)



# Overview‌

그레이들의 build phase 는 아래와 같이 세가지가 있다고 한다.

- initialization
- configuration
- execution



## 참고) Project 오브젝트

참고로 알아두어야 하는 것이 있다. gradle 에서 사용하는 groovy 또는 kotlin 은 Project 타입의 인스턴스라는 것이 있다. 즉, Project 라는 클래스를 인스턴스화 한 것을 이야기하는 것 인데, 이 Project 클래스는 Gradle 에서 지원하는 내장 라이브러리에서 제공하는 클래스이다.



## Initialization 단계 

> project 인스턴스 생성단계

그레이들은 single 프로젝트, multi 프로젝트 빌드를 지원한다. initialization 단계에서 그레이들은 빌드에 포함시킬 프로젝트 들을 결정하고 각 프로젝트의 인스턴스를 생성한다.



## Configuration 단계

> project 인스턴스 설정 단계

Configuration 단계에서 project 오브젝트가 설정된다.(project objects are configured).



## Execution 단계

> 태스크를 실행하는 단계 

Gradle 은 태스크(task)들의 subset을 결정짓는데,  이 subset 은 configuration 단계에서 생성되고 구성된 task 들이다. subset 은 gradle 명령& 현재 디렉터리로 전달된 태스크(task) 이름 인자에 의해 결정된다. 이후 Gradle은 선택된 태스크들 각각을 실행시킨다. 

  

Execution 단계는 공식문서의 내용이 이해가 잘 안간다. 아직은 직역 수준으로만 정리하고 있는데 조금 더 의역으로 정리할 필요가 있다. 그냥 아직 일단은  `실행시키는 단계` 라고 이해하고 있다.

