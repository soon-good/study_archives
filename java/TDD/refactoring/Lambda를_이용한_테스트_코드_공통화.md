# 테스트 리팩토링 - 람다식을 활용한 공통화

테스트 코드 작성시 실제 운영 로직 내에서 굉장히 자주 쓰이는 로직이 있다. 해당 기능이 메서드로 분리되어 있지 않을 경우도 충분히 있다. 이 경우 해당 기능을 수정할 경우 여러곳에 독버섯처럼 퍼져있는 테스트 코드가 깨지게 될 가능성이 충분하다.  

이 경우 중복되는 테스트 코드를 테스트 함수로 분리해내면 된다. 그런데, 해당 기능이 여러 클래스에 걸쳐서 나뉘어 있는 경우 테스트 클래스마다 공통함수를 만들어내기도 참 어중간하다. 이런 경우, 여러가지 해결 방법이 있을 것 같다. 여기서는 Mock객체를 인자로 전달받아서 lambda 식으로 공통화하는 방식을 정리해보려 한다.  


# 1. 순수 가상함수(?) 생성

**<u>순수 가상함수(?) 이 용어는 정확한 용어를 찾아서 따로 정리할 예정</u>**  

람다식의 몸체로 치환될 수 있는 순수 가상함수(?)를 interface로 선언한다.

## ListTestLambda.java

```java
package io.study.tddlambda.sample.lambda;

public interface ListTestLambda <M>{
	public void test(M mock);
}
```

\<M\> 으로 표시한 부분은 Generic 으로 표현한 부분이다. 자세한 개념은 java.util.List를 살펴보면 될 듯 하다...



# 2. 람다식 관리 클래스/메서드 정의/구현

## ListTestLambdaManager.java

```java
package io.study.tddlambda.sample.lambda;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

public class ListTestLambdaManager {

	public static ListTestLambda<List> listTestLambda(){
		ListTestLambda<List> l = (mockList)->{
			verify(mockList, times(1)).add("1");
			verify(mockList).add("1");
		};

		return l;
	}
  
}
```



## 설명

```java
public interface ListTestLambda <M>{
	public void test(M mock);
}
```

인터페이스 ListTestLambda 를 아래와 같이 lambda 식으로 구현해 리턴하는 함수는 아래와 같다.

```java
// ...
	public static ListTestLambda<List> listTestLambda(){
		ListTestLambda<List> l = (mockList)->{
			verify(mockList, times(1)).add("1");
			verify(mockList).add("1");
		};

		return l;
	}
// ...
```

이렇게 리턴되는 lambda 바디는 바로 실행되지 않는다. 아래의 **3.테스트코드에서의 호출/사용**에서 직접 호출해야 실행된다.  

# 3. 테스트 코드에서의 호출/사용

## LambdaTest.java

```java
package io.study.tddlambda.sample;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.study.tddlambda.sample.lambda.ListTestLambdaManager;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LambdaTest {

	@Test
	@DisplayName("pass Mock Object as an Argument in lambda")
	void test_mock_lambda1(){
		List list = mock(List.class);
		list.add("1");

		ListTestLambdaManager
			.listTestLambda()
			.test(list);
	}

	@Test
	@DisplayName("Test 2")
	void test_mock_lambda2(){
		List list = mock(List.class);
		list.add("1");

		ListTestLambdaManager
			.listTestLambda()
			.test(list);

		list.add("2");

		verify(list, times(1)).add("2");
	}
}
```






