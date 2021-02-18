# JUnit5 관련 라이브러리 레퍼런스

# 1. Mockito

- [Mockito 한글 번역 자료](https://github.com/mockito/mockito/wiki/Mockito-features-in-Korean)
- [Mockito 공식자료](https://www.javadoc.io/static/org.mockito/mockito-core/3.3.3/org/mockito/Mockito.html)

## Mockito 기본적인 사용법 및 Use Case 레퍼런스

- Mockito 기본적인 사용법 
  - [JDM 블로그](https://jdm.kr/blog/222)
  - @Mock, when(), doThrow(), doNothing(), verify(), @InjectMocks, @Spy 등에 대해 요약하는 글
- Mockito When/Then CookBook
  - [Mockito When/Then CookBook](https://www.baeldung.com/mockito-behavior)
- Mockito 사용하기
  - [Mockito 사용하기](https://bestalign.github.io/2016/07/10/intro-mockito-2/)
  - One-liner stubs, 
  - Mocking Details, 
  - Verification with timeout, 
  - capturing argument, 
  - Argument Matcher, 
  - Spying on real objects, 
  - Stubbing with callbacks. 
  - Iterator-style stubbing, 
  - @Mock 어노테이션
- Mocking void Methods with Mockito
  - [Mocking void Methods with Mockito](https://www.baeldung.com/mockito-void-methods)
  - Argument Capture, Answering 등에 대해 설명되어 있다.
- Mockito - Verifying Behavior
  - [Mockito - Verifying Behavior](https://www.tutorialspoint.com/mockito/mockito_verifying_behavior.htm)
  - Mockito 의 verify 를 이용할 때 argument를 어떤 식으로 검증하는 지에 대한 자료
- Mockito : doReturn vs thenReturn
  - http://sangsoonam.github.io/2019/02/04/mockito-doreturn-vs-thenreturn.html
- Mockito Argument Matchers - any()
  - https://www.journaldev.com/21876/mockito-argument-matchers-any-eq

검색어
- mockito when void method
- spymock any argument
- mockito when example
- mockito.spy



# 2. hamcrest

hamcrest Matchers ( Matchers (Hamcrest) )

- [Java Hamcrest Home](http://hamcrest.org/JavaHamcrest/index)
- [Java Hamcrest Cookbook](https://www.baeldung.com/hamcrest-collections-arrays)
- [Hamcrest Matchers - Matchers](http://hamcrest.org/JavaHamcrest/javadoc/1.3/org/hamcrest/Matchers.html)
- [Hamcrest Tutorial](http://hamcrest.org/JavaHamcrest/tutorial)

## 커스텀 Matcher 만드는 방법에 대한 자료

- [MockitoHamcrest](https://www.javadoc.io/static/org.mockito/mockito-core/3.3.3/org/mockito/hamcrest/MockitoHamcrest.html)
- [ArgumentMatchers](https://www.javadoc.io/static/org.mockito/mockito-core/3.3.3/org/mockito/ArgumentMatchers.html)

# 3. 행위 vs 상태 검증



# 4. JUnit5 BDD

## 4.1. JUnit5 계층구조 테스트 코드 (@Nested)

- [기계인간 - JUnit5로 계층구조의 테스트코드 작성하기](https://johngrib.github.io/wiki/junit5-nested/)
  - ex)
  - ![이미자](https://johngrib.github.io/post-img/junit5-nested/test-kor.png)



## 4.2. BDD 스타일 라이브러리 레퍼런스

### AssertJ

- [AssertJ를 BDD 스타일로](https://cheese10yun.github.io/junit5-in-spring/#assertj-1)

### Mockito

- [wedul - BDD 스타일 Mockito](https://wedul.site/648)

