# 아이템 39 - 명명 패턴보다 애너테이션을 사용하라

참고할 만한 자료 

1. [Carrey's 기술 블로그](https://jaehun2841.github.io/2019/02/04/effective-java-item39/)



# 과거의 방식 - 명명패턴

애너테이션을 사용하지 않는 경우 대부분 명명패턴을 사용해왔다. 이 책에서 그 예로 드는 것이 JUnit3이다. JUnit3는 테스트 메서드 이름을 test로 시작하게끔 한다. 이렇게 특정 단어로 시작하도록 지정하는 것을 명명 패턴이라고 하는 듯 하다.  

(명명 패턴에 대한 영문 명칭을 아직 못찾았지만, 추후 명명 패턴에 대한 링크를 추가할 예정)  



## 명명패턴의 단점

1. 오타가 나면 안된다.  
   실수로 이름을 tsetSafetyOverride로 지으면 JUnit 3은 이 메서드를 무시하고 지나치기 때문에 개발자는 이 테스트가 통과했다고 오해할 수 있다.  
2. 올바른 프로그램 요소에서만 사용되리라는 보증이 없다는 점  
   클래스 이름을 TestSafetyMechanisms로 지어 JUnit에 던져줬다고 해보자. 클래스 이름 뒤에 Test가 붙어야 테스트가 실행되는데 실행되지 않는다. JUnit이 경고메시지 조차 출력하지 않지만 개발자가 의도한 테스트는 전혀 수행되지 않는다.  
3. 프로그램 요소를 매개변수로 전달할 마땅한 방법이 없다.    
   특정 예외를 던지는지 확인해야 하는 테스트를 예로 들 수 있다. 기대하는 예외의 타입을 매개변수로 전달해야 하는 상황이다. 예외의 이름을 테스트 메서드 이름에 덧붙이는 방법도 있지만 보기도 나쁘고 깨지기도 쉽다. (아이템 62)  



# 애너테이션의 종류

- Built-in Annotation  (자바 표준 애너테이션)
  Java 언어에서 지원하는 내장 어노테이션  
  (@Override, @Deprecated, @SuppressWarning, @SaveVarargs, @FunctionalInterface)  
- Meta Annotation (애너테이션을 위한 애너테이션)  
  어노테이션 위에 붙여놓은 어노테이션. 즉, 애너테이션 선언(@interface) 위에 추가한 애너테이션이다.  



# 커스텀 애너테이션 

## 마커 애너테이션

매개변수를 가지지 않는 애너테이션. 아무 매개변수 없이 단순히 대상에 마킹(marking)한다는 의미에서, 마커(marker) 애너테이션이라고 부른다.



### 예1) JUnit Test 어노테이션

선언

```java
import java.lang.annotation.*;

/**
 * 테스트 메서드임을 선언하는 애너테이션이다.
 * 매개변수 없는 정적 메서드 전용이다.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test{
}
```

  

사용

```java
public class Sample{
  @Test public static void m1(){} // 성공해야 한다.
  public static void m2(){}
  @Test public static void m3(){
    throw new RuntimeException("실패");
  }
  public static void m4(){}
  @Test public void m5(){} // 잘못 사용한 예 : 정적 메서드가 아니다.
  public static void m6(){}
  @Test public static void m7(){
    throw new RuntimeException("실패");
  }
  public static void m8(){}
}
```



- m3(), m7()  
  테스트는 정상 동작, 예외를 던지므로 테스트 결과는 실패 (Fail)
- m1()   
  테스트 정상 동작, 테스트 결과는 성공(success)
- m5()  
  테스트 동작x, 잘못 사용한 예이다. 정적 메서드가 아니다.  
- m2(), m6(), m8()  
  테스트 코드가 아니다. 일반 메서드로 동작한다.  



## 애너테이션 (일반)



