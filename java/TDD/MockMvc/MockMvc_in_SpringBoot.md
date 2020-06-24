# MockMVC in Spring Boot

# 1. 참고자료

- [스프링 부트에서 컨트롤러 테스트하기](https://siyoon210.tistory.com/145)
- [Spring Boot 의 MockMvc를 사용해 GET, POST 응답 테스트 하기](https://shinsunyoung.tistory.com/52)
- [MockMvc - Add Request Parameter to Test](https://stackoverflow.com/questions/17972428/mock-mvc-add-request-parameter-to-test)
- [Spring MockMvc 로 테스트 하기](https://pangyo-dev.tistory.com/2)



# 2. MockMvc 설정

## 1-1) @SpringBootTest + @AutoConfigureMockMvc

이상하게도 MockMvc 설정할 때 @WebMvcTest 가 잘 먹지 않더라... 이것 저것 다른 설정하는게 시간이 아깝다고 느껴지면 그냥 @AutoConfigureMockMvc 를 사용하면 된다.

### JUnit4

```java
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TrendingChartTest {
  
  @Autowired
  private MockMvc mockMvc;
}
```



### JUnit5

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TrendingChartTest {
  
  @Autowired
  private MockMvc mockMvc;
}
```



## 1-2) @WebMvcTest

### JUnit4

```java
@RunWith(SpringRunner.class)
@WebMvcTest
public class TrendingChartTest {
  
  @Autowired
  private MockMvc mockMvc;
  // ...
}
```



# 3. MockMvc 테스트 시 자주 사용하는 함수들

이렇게 정리하면 암기과목 같지만,... 일하는게 어디 항상 이해식으로만 바르게 진행되던가. 2시간 동안 같은 걸 반복하며 이해하는 것보다 10분동안 고통스럽게 외우는게 더 유익할 때가 있다. MockMvc 관련 라이브러리 들을 반드시 외울 필요는 없다. 어차피 자연스레 외우게 되긴 하니까 그럴 필요가 없기는 하다.  

  

초창기에 사용하기전에 전체적인 쓰임새를 파악하면 좋을 것 같아 정리한다. Handlers 에는 뭐가 있고, Matchers에는 어떤게 있고 Builders로는 이런게 있다. 이렇게 정리해두면, 처음에 반복적으로 MockMvc를 익히는데에 도움이 될 듯 하다. 항상 학습도 효율적으로 하고, 정리 깔끔하게 하는게.... 중요한 것 같다.   

## MockMvcRequestBuilders

- get
- post

## MockMvcResultHandlers

- print

## MockMvcResultMatchers

- status
- jsonpath



# 3. MockMvc 에서 jsonPath 확인하기

MockMvc 내에서 json을 확인하는 방법은 jsonPath(...) 라는 함수를 쓴다. 이 jsonPath는 MockMvcResultMatchers 를 사용한다.

![이미자](/Users/kyle.sgjung/workspace/sgjung/study_archives/java/TDD/MockMvc/img/JSON_PATH_1.png)





