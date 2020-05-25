# JUnit5 기반 커스텀 애노테이션 만들기 (1) - ParameterizedTest 분석

테스트시 실행시간을 측정하는 것을 모든 메서드에 하지 않고, 특정 메서드의 시작과 종료 시에만 하고 싶다. 이런 이유로 커스텀 애노테이션 만드는 방식을 찾아보게 되었다. 처음에는 '공부잘하려면 어떻게 해야해?' 하는 질문 하듯이 구글링을 했다. 그런데 조금은 무의미 했다. @ExtendWith를 사용하라는 글을 접한것은 도움이 되었다.  

그러던 차에 @ParameterizedTest 애노테이션을 분석해야 겠다는 생각이 들었다. 이 외에도 참고해서 분석해보면 좋은 클래스들을 정리해보면 아래와 같다.

- @MethodSource  
- @ParameterizedTest  
- @Sql  
  - Spring JDBC 관련 라이브러리 이다.  
  - .sql 파일들을 입력으로 받아 sql 파일을 실행한다.  
- @SqlConfig  
  - Spring JDBC 관련 라이브러리 이다.  
  - .sql 파일들을 입력으로 받아 sql 파일을 실행한다.  

여기서는 @ParameterizedTest 를 분석해보는 과정을 정리해본다. 

# 0. 참고자료들

- [JUnit5 Extension 클래스](https://junit.org/junit5/docs/5.0.0/api/org/junit/jupiter/api/extension/Extension.html)
- [JUnit5 공식문서 - ParameterizedTest](https://junit.org/junit5/docs/5.3.0/api/org/junit/jupiter/params/ParameterizedTest.html)
- [JUnit5 공식문서 - TestTemplateInvocationContextProvider](https://junit.org/junit5/docs/5.0.0/api/org/junit/jupiter/api/extension/TestTemplateInvocationContextProvider.html)

  

@ParameterizedTest 어노테이션의 선언을 Go To Definition 기능으로 탐색하다보면 @ParameterizedTest 어노테이션이 관련된 junit5 내의 클래스들과의 관계도는 아래와 같다.

![이미자](./img/CLASS_DIAGRAM_PARAMETERIZED_TEST.png)

결론적으로 **ParameterizedTestExtension** 클래스는 **Extension** 타입(org.junit.jupiter.api.extension.Extension)으로 취급될 수 있다. (업캐스팅 다운캐스팅 이거 까먹었다. 정리하자. ㅋㅋㅋ 아...)  

보통 커스텀 어노테이션을 사용하려면 내부 프레임워크 내에서의 Listener 역할을 하는 곳에서 프로그램이 로딩되었을때의 이벤트를 감지해서 로딩 시점에 reflection으로 해당 어노테이션을 등록했던 것으로 기억한다. 아직 확실하지는 않지만 org.junit.jupiter.api.extension.Extension 인터페이스를 상속한 클래스는 모두 JUnit5 내에서 @ExtendWith 할 수 있는 것으로 추측이 가능하다.  



### ParameterizedTestExtension

ParameterizedTestExtension 은 Extension 인터페이스 타입으로 취급가능하다. 이 말은 @ExtendWith 할수 있다는 의미이다. 

- ParameterizedTestExtension 은 TestTemplateInvocationContextProvider 인터페이스를 implements 한 클래스 인데 
- TestTemplateInvocationContextProvider는 Extension 인터페이스를 상속하고 있다. 위에 그려놓은 클래스 다이어그램을 참고하자.
- 참고) Extension 인터페이스를 상속받는 인터페이스들은 [참고자료](https://junit.org/junit5/docs/5.0.0/api/org/junit/jupiter/api/extension/Extension.html) 에서 확인 가능하다.



이제 소스를 보면서 파악해보고 JUnit5를 위한 커스텀 어노테이션을 작성해 @ExtendWith 하려 할 때는 어떻게 하면 될지 가늠해보자.

# 1. @ParameterizedTest

- 참고자료
  - [JUNIT 5 공식문서 - @ParameterizedTest](https://junit.org/junit5/docs/5.3.0/api/org/junit/jupiter/params/ParameterizedTest.html)

@ParameterizedTest 는 ParameterizedTestExtension 클래스를 @ExtendWith 하고 있다.  

```java
@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = EXPERIMENTAL, since = "5.0")
@TestTemplate
@ExtendWith(ParameterizedTestExtension.class)
public @interface ParameterizedTest {
    // ...
	String name() default "[{index}] {arguments}";

}
```

# 2. @ParameterizedTestExtension

- 참고자료
  - 공식문서 링크가 없다... 찾아봐야 한다.

@ParameterizedTestExtension 은 TestTemplateInvocationContextProvider 인터페이스를 implements 하고 있다.  
그리고 TestTemplateInvocationContextProvider 는 Extension 인터페이스가 super class 이다.
이 Extension 인터페이스를 상속받는 인터페이스들은 아래와 같다. ([참고자료](https://junit.org/junit5/docs/5.0.0/api/org/junit/jupiter/api/extension/Extension.html))   

```java
class ParameterizedTestExtension implements TestTemplateInvocationContextProvider {

	@Override
	public boolean supportsTestTemplate(ExtensionContext context) {
		if (!context.getTestMethod().isPresent()) {
			return false;
		}

		Method testMethod = context.getTestMethod().get();
		if (!isAnnotated(testMethod, ParameterizedTest.class)) {
			return false;
		}

		Preconditions.condition(AggregationUtils.hasPotentiallyValidSignature(testMethod),
			() -> String.format(
				"@ParameterizedTest method [%s] declares formal parameters in an invalid order: "
						+ "argument aggregators must be declared after any indexed arguments "
						+ "and before any arguments resolved by another ParameterResolver.",
				testMethod.toGenericString()));

		return true;
	}

	@Override
	public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
		Method templateMethod = context.getRequiredTestMethod();
		ParameterizedTestNameFormatter formatter = createNameFormatter(templateMethod);
		AtomicLong invocationCount = new AtomicLong(0);
		// @formatter:off
		return findRepeatableAnnotations(templateMethod, ArgumentsSource.class)
				.stream()
				.map(ArgumentsSource::value)
				.map(ReflectionUtils::newInstance)
				.map(provider -> AnnotationConsumerInitializer.initialize(templateMethod, provider))
				.flatMap(provider -> arguments(provider, context))
				.map(Arguments::get)
				.map(arguments -> consumedArguments(arguments, templateMethod))
				.map(arguments -> createInvocationContext(formatter, arguments))
				.peek(invocationContext -> invocationCount.incrementAndGet())
				.onClose(() ->
						Preconditions.condition(invocationCount.get() > 0,
								"Configuration error: You must provide at least one argument for this @ParameterizedTest"));
		// @formatter:on
	}

	private TestTemplateInvocationContext createInvocationContext(ParameterizedTestNameFormatter formatter,
			Object[] arguments) {
		return new ParameterizedTestInvocationContext(formatter, arguments);
	}

	private ParameterizedTestNameFormatter createNameFormatter(Method templateMethod) {
		ParameterizedTest parameterizedTest = findAnnotation(templateMethod, ParameterizedTest.class).get();
		String name = Preconditions.notBlank(parameterizedTest.name().trim(),
			() -> String.format(
				"Configuration error: @ParameterizedTest on method [%s] must be declared with a non-empty name.",
				templateMethod));
		return new ParameterizedTestNameFormatter(name);
	}

	protected static Stream<? extends Arguments> arguments(ArgumentsProvider provider, ExtensionContext context) {
		try {
			return provider.provideArguments(context);
		}
		catch (Exception e) {
			throw ExceptionUtils.throwAsUncheckedException(e);
		}
	}

	private Object[] consumedArguments(Object[] arguments, Method templateMethod) {
		int parameterCount = templateMethod.getParameterCount();
		return hasAggregator(templateMethod) ? arguments
				: (arguments.length > parameterCount ? Arrays.copyOf(arguments, parameterCount) : arguments);
	}

}
```



# 3. TestTemplateInvocationContextProvider 

- 참고자료
  - [JUNIT 5 공식문서 - TestTemplateInvocationContextProvider](https://junit.org/junit5/docs/5.0.0/api/org/junit/jupiter/api/extension/TestTemplateInvocationContextProvider.html)
- TestTemplateInvocationContextProvider 인터페이스는 Extension 인터페이스를 상속하고 있다.
  - (Extension 인터페이스를 super class로 두고 있다.)  

- TestTemplateInvocationContextProvider 인터페이스는 
  - provideTestTemplateInvocationContexts(ExtensionContext context) 메서드 하나만을 가지고 있는 인터페이스이다.
- TestTemplateInvocationContextProvider 는 Extension 인터페이스가 super class 이다.
  이 Extension 인터페이스를 상속받는 인터페이스들은 아래와 같다. ([참고자료](https://junit.org/junit5/docs/5.0.0/api/org/junit/jupiter/api/extension/Extension.html))  
  - AfterAll, AfterEach, BeforeEach 등에 관련된 Callback 류의 인터페이스들
    - AfterAllCallback
    - AfterEachCallback
    - AfterEachMethodAdapter
    - ...
  - ExecutionCondition
  - ParameterResolver
  - TestExecutionExceptionHandler
  - TestInstancePostProcessor
  - TestTemplateInvocationContextProvider

  

```java
@API(status = STABLE, since = "5.0")
public interface TestTemplateInvocationContextProvider extends Extension {
  // 1)
  boolean supportsTestTemplate(ExtensionContext var1);
  
  // 2)
	Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context);

}
```

  

- 1) boolean supportsTestTemplate(ExtensionContext var1);
  - Determine if this provider supports providing invocation contexts for the test template method represented by the supplied `context`.
- 2) Stream\<TestTemplateInvocationContext\> provideTestTemplateInvocationContexts(ExtensionContext context);
  - Provide [invocation contexts](https://junit.org/junit5/docs/5.3.0/api/org/junit/jupiter/api/extension/TestTemplateInvocationContext.html) for the test template method represented by the supplied `context`.

# 4. Extension

- 참고자료
  - [JUNIT 5 공식문서 - Extension](https://junit.org/junit5/docs/5.0.0/api/org/junit/jupiter/api/extension/Extension.html)
- 설명
  - An `Extension` can be registered declaratively via [`@ExtendWith`](https://junit.org/junit5/docs/5.0.0/api/org/junit/jupiter/api/extension/ExtendWith.html).

  

```java
package org.junit.jupiter.api.extension;

import static org.apiguardian.api.API.Status.STABLE;

import org.apiguardian.api.API;

@API(status = STABLE, since = "5.0")
public interface Extension {
}
```





