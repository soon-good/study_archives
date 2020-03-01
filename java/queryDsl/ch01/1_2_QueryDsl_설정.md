# QueryDsl 설정

QueryDsl은 gradle 빌드 과정 속에서 QType이라는 타입을 뽑아내야 한다고 한다. 그래서 환경설정이 따로 어렵다고 하는군.  

build.gradle 내의 plugins에 아래와 같이 

## plugins 설정

> id "com.ewerk.gradle.plugin.querydsl" version "1.0.10"

을 추가해준다.

```groovy
plugins {
	id 'org.springframework.boot' version '2.3.0.BUILD-SNAPSHOT'
	id 'io.spring.dependency-management' version '1.0.9.RELEASE'
	// queryDsl 추가
	id "com.ewerk.gradle.plugin.querydsl" version "1.0.10"
	id 'java'
}
```



## dependency 설정

build.gradle 내에 아래와 같이 의존성을 추가한다.

> implementation 'com.querydsl:querydsl-jpa'

build.gradle 내부 내용중 일부

```groovy
...
dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	implementation 'com.querydsl:querydsl-jpa'

	compileOnly 'org.projectlombok:lombok'
	runtimeOnly 'com.h2database:h2'
	annotationProcessor 'org.projectlombok:lombok'
	testImplementation('org.springframework.boot:spring-boot-starter-test') {
		exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
	}
}
```



## build script

build.gradle 가장 하단에 아래와 같이 입력한다.

```groovy
...
...
//querydsl build 스크립트
def querydslDir = "$buildDir/generated/querydsl"

querydsl {
	jpa = true
	querydslSourcesDir = querydslDir
}
sourceSets {
	main.java.srcDir querydslDir
}
configurations {
	querydsl.extendsFrom compileClasspath
}
compileQuerydsl {
	options.annotationProcessorPath = configurations.querydsl
}
```

