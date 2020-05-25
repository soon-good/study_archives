# 커스텀 JUnit Extension 만들기 - 시간측정 어노테이션

커스텀 Extension을 만들때 보통은 Extension 인터페이스를 implements 한 클래스를 만들어서 

- @ExtendsWith 
- @RegisterExtension

둘 중에 하나를 하면 된다. 요번 예제에서 확인해볼 내용은 메서드 실행 시마다 실행시간 측정을 할 수 있는 Extension 예제이다.  

지금부터 만들어볼 실행 시간 측정 기능은 단순히 JUnit5의 라이프사이클을 활용하면 되는데, 이미 JUnit5 에서는 이와 같은 것을 편리하게 이용할 수 있도록 BeforeTestExecutionCallback, AfterTestExecutionCallback 등의 인터페이스를 제공하고 있다. 이러한 것을 Test Lifecycle Callback 이라고 하는데 해당 설명은 [공식문서 - Test Lifecycle Callbacks](https://junit.org/junit5/docs/current/user-guide/#extensions-lifecycle-callbacks) 에서 확인 가능하다.  

각 라이프사이클 계층의 순서는 아래와 같다.

- `BeforeAllCallback`
  - `BeforeEachCallback`
    - `BeforeTestExecutionCallback`
    - `AfterTestExecutionCallback`
  - `AfterEachCallback`
- `AfterAllCallback`

예제의 구현 목표는 아래와 같다.

> - 각 테스트 메서드 하나의 매 실행 시마다 실행된 실행시간을 측정한다.



# 1. @ExtendWith 활용 예

## Extension 작성

테스트 메서드 실행전/후 마다 시간을 체크하기 위해 

- BeforeTestExecutionCallback
- AfterTestExecutionCallback

을 implements한 class 인 TimerTestExtension 을 작성했다.  

**TimerTestExtension.java**

```java
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

public class TimerTestExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

	private Long timeout;

	public TimerTestExtension(){}

	@Override
	public void beforeTestExecution(ExtensionContext context) throws Exception {
		Store store = getStore(context);
		store.put("START_TIME", System.currentTimeMillis());
	}

	@Override
	public void afterTestExecution(ExtensionContext context) throws Exception {
		Store store = getStore(context);

		Long start_time = store.remove("START_TIME", Long.class);
		Long duration = System.currentTimeMillis() - start_time;

		System.out.println("test took " + duration + " ms.");

	}

	private Store getStore(ExtensionContext context){
		String testClassName = context.getRequiredTestClass().getName();
		String testMethodName = context.getRequiredTestMethod().getName();
		Namespace namespace = Namespace.create(testClassName, testMethodName);
		Store store = context.getStore(namespace);
		return store;
	}
}
```

### beforeExecution (ExecutionContext)

- beforeExecution (ExtensionContext) 
  - 테스트 메서드가 호출되기 직전에 호출되는 핸들러와 같은 역할이다.
  - 현재 실행되는 Test가 가지고 있는 Execution Context 의 store를 얻어온다.
  - context에 test가 호출된 클래스명과 테스트 메서드 명을 Namespace로 하여 store에 저장한다.
    - 해당 이름으로 컨텍스트에 해당 네임스페이스의 store가 없다면 해당 네임스페이스로 store 공간을 생성한다.
    - test class name 
      - @Test로 테스트를 실행하는 메서드가 속한 클래스의 클래스명
    - test method name
      - @Test 로 테스트를 실행되는 메서드의 메서드명
  - 실행 전의 시간을 store에 "START_TIME"이라는 이름으로 현재 시간을 저장한다.

### afterTestExecution (ExecutionContext)

- afterTestExecution (ExecutionContext)
  - 테스트 메서드가 호출된 직후에 호출되는 핸들러와 같은 역할이다.
  - 현재 실행되는 Test가 가지고 있는 Execution Context의 store를 얻어온다.
  - store에서 test가 호출된 클래스명과 테스트 메서드 명을 Namespace로 하여 컨텍스트에서 해당 namespace의 스토어를 얻어온다.
  - store 에서 "START_TIME" 에 해당하는 값을 Long 타입으로 가져오고 스토어에서 해당 값을 지운다.
  - 현재 시간에서 - START_TIME 을 하여 수행된 실행시간을 구한다.

## Test 케이스 작성

**TimerTest.java**

```java
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TimerTestExtension.class)
@SpringBootTest
public class TimerTest {

	@DisplayName("슬로우 메서드 #1 ")
	@Test
	public void slow_test_1() throws Exception {
		Thread.sleep(500);
		System.out.println(this);
	}

}
```

실행해보면 시간이 측정되는 것을 확인 가능하다.

# 2. @RegisterExtension 활용 예

매개변수를 Extension 에 전달하여 매개변수에 의한 다른 동작을 수행하게끔 할 수 있다. @RegisterExtension 어노테이션을 활용하면 필드를 선언하여 매개변수를 Extension에 전달하여 매개변수에 의한 동작을 수행하게끔 할 수 있다.

## TimerTestExtension.java

먼저 매개변수를 전달할 수 있도록 TimerTestExtension 클래스의 생성자에 매개변수를 선언하자.  

```java
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

public class TimerTestExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

	private Long timeout;

	public TimerTestExtension(){}

	public TimerTestExtension(Long timeout){
		this.timeout = timeout;
	}

	@Override
	public void beforeTestExecution(ExtensionContext context) throws Exception {
		Store store = getStore(context);
		store.put("START_TIME", System.currentTimeMillis());
	}

	@Override
	public void afterTestExecution(ExtensionContext context) throws Exception {
		Store store = getStore(context);

		Long start_time = store.remove("START_TIME", Long.class);
		Long duration = System.currentTimeMillis() - start_time;

		System.out.println("test took " + duration + " ms.");

		if(timeout != null){
			System.out.println("timeout is :: " + timeout);
		}
	}

	private Store getStore(ExtensionContext context){
		String testClassName = context.getRequiredTestClass().getName();
		String testMethodName = context.getRequiredTestMethod().getName();
		Namespace namespace = Namespace.create(testClassName, testMethodName);
		Store store = context.getStore(namespace);
		return store;
	}
}

```



## TimerTest.java

@RegisterExtension 어노테이션을 활용하여 TimerTestExecution 내에 파라미터를 전달할 수 있도록 필드로 선언하자.

```java
package com.study.junit5.extension;

import com.study.junit5.extension.timer.withoutparam.TimerTestExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TimerTest {

	@RegisterExtension
	TimerTestExtension timeoutExtension = new TimerTestExtension(1000L);

	@DisplayName("슬로우 메서드 #1 ")
	@Test
	public void slow_test_1() throws Exception {
		Thread.sleep(500);
		System.out.println(this);
	}

}
```



## 출력결과

```bash
com.study.junit5.extension.TimerTest@6bfaa0a6
test took 511 ms.
timeout is :: 1000
```

