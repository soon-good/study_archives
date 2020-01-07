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



```java
import java.lang.annotation.*;

/**
 * 테스트 메서드임을 선언하는 애너테이션
 * 매개변수 없는 정적 메서드 전용
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test{
}
```



위 예제 코드에서 @Test 애너테이션 선언 위의

- @Retention
- @Target

이 메타 애너테이션이다.



# 애너테이션 활용

빌트인(내장) 애너테이션을 사용하는 것 외에도 중복되는 로직들을 직접 애너테이션으로 만들어 사용하는 것 또한 가능하다.  

여기서는

- 파라미터(매개변수)가 없는 애너테이션 (마커 애너테이션)
- 파라미터(매개변수)를 가진 애너테이션

을 직접 만들어본다.

  

애너테이션을 제작하고 선언한 애너테이션을 단순히 @[에너테이션명] 과 같이 사용하는 것만으로 애너테이션을 사용할 수 없다. 애너테이션의 선언과, 사용 구문을 작성 후에는 이것을 로딩하는 구문을 따로 작성해야 한다.    

보통 애너테이션을 만들때 아래와 같이 세가지의 작업을 하게 된다.  

- 애너테이션 선언.  
  @interface [애너테이션 이름]  

- 사용 구문(로직) 작성  
  필요한 로직의 method, class 등의 요소 위에 @Test 등의 어노테이션을 붙이는 과정이다.  

- 애너테이션 로딩 구문  
  프로그램의 main문 과 같은 실행 컨텍스트를 잡고 있는 부분에서 reflection을 이용해 이 어노테이션을 실행한다.



## 파라미터가 없는 애너테이션 (마커 애너테이션)

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

  

로딩   

```java
import java.lang.reflect.*;

public class RunTests {
  public stratic void main(String [] args) throws Exception{
    int tests  = 0;
    int passed = 0;
    Class<?> testClass = Class.forName(args[0]);
    for(Method m : testClass.getDeclaredMethods()){
      if(m.isAnnotationPresent(Test.class)){
        tests++;
        try{
          m.invoke(null);
          passed++;
        }
        catch(InvocationTargetException wrappedExc){
          Throwable exc = wrappedExc.getCause();
          System.out.println(m + " 실패 : " + exc);
        }
        catch(Exception exc){
          System.out.println("잘못 사용한 @Test : " + m);
        }
      }
    }
    System.out.printf("성공 : %d, 실패\: %d%n", passed, tests-passed);
  }
}
```



## 파라미터(매개변수)를 가진 애너테이션

매개변수의 타입으로 Class\<? extends Throwable\> 을 받는 애너테이션을 작성해본다. 여기서 와일드 카드 ?은 

- "Thrwoable 을 확장한(상속한) 클래스의 Class 객체" 

라는 의미이다. 따라서 모든 예외(와 오류) 타입을 다 수용한다. (item33에서 언급하는 한정적 타입 토큰을 사용하는 예가 될 수 있다.)



### ExceptionTest.java

```java
import java.lang.annotation.*;
/*
 * 예외가 던져져야만 성공하는 테스트 메서드용 애너테이션
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTest{
  Class<? extends Throwable> value();
}
```



### 사용예제 - Sample2.java  

이 에너테이션을 실제 다른 메서드위에서 사용하는 예

```java
public class Sample2{
  @ExceptionTest(ArithmeticException.class)
  public static void m1(){    // 성공해야 한다.
    int i = 0;
    i = i/i;
  }
  
  @ExceptionTest(ArithmeticException.class)
  public static void m2(){    // 실패해야 한다. (다른 예외 발생)
    int [] a = new int [0];
    int i = a[1];
  }
  
  @ExceptionTest(ArithmeticException.class)
  public static void m3(){}   // 실패한다.
}
```

  

### 로딩구문 예제

```java
import java.lang.reflect.*;

public class RunTests {
  public stratic void main(String [] args) throws Exception{
    int tests  = 0;
    int passed = 0;
    Class<?> testClass = Class.forName(args[0]);
    for(Method m : testClass.getDeclaredMethods()){
      if(m.isAnnotationPresent(Test.class)){
        tests++;
        try{
          m.invoke(null);
          passed++;
        }
        catch(InvocationTargetException wrappedExc){
          Throwable exc = wrappedExc.getCause();
          // 추가된 구문 (아래 구문들 참고)
          // 1) ExceptionTest 애너테이션 내부로 전달된 파라미터의 
          //    클래스 인스턴스를 얻어온다.
          Class<? extends Throwable> excType = 
            m.getAnnotation(ExceptionTest.class).value();
          
          // 2) Exception 인스턴스가 ExceptionTest 클래스의 인스턴스인지 체크
          if(excType.isInstance(exc)){
            passed++;
          }
          else{
            System.out.printf("테스트 %s 실패 : 기대한 예외 %s, 발생한 예외 %s%n", m, excType.getName(), exc);
          }
        }
        catch(Exception exc){
          System.out.println("잘못 사용한 @Test : " + m);
        }
      }
    }
    System.out.printf("성공 : %d, 실패\: %d%n", passed, tests-passed);
  }
}
```



## 여러개의 파라미터를 사용할 때



1. 반복문 사용
2. @Repeatable 애너테이션 사용



## 여러개의 파라미터 사용 (1) - 반복문 사용  

애너테이션이 배열을 파라미터로 받아야 할 경우가 있다. 예를 들어 여러가지 종류의 예외가 발생할 경우를 대비해 Exception 클래스 별로 다른 예외 문구 출력을 하도록 지정하는 경우를들 수 있다.  
여러개의 예외를 배열로 명시하고 그 중 하나가 발생하면 성공하게 만드는 방식이다.   

예) @ExceptionTest 애너테이션의 매개변수 타입을 Class 객체의 배열로 수정

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTest{
  Class<? extends Throwable> [] value();
}
```

  

배열 매개변수를 직접 전달하는 예

```java
@ExceptionTest({IndexOutOfBoundsException.class,
               NullPointerException.class})
public static void doublyBad(){ 	// 성공하는 코드
  List<String> list = new ArrayList<>();
  list.addAlll(5, null);
}
```

  

배열 파라미터를 처리하도록 테스트 러너(로딩 구문)을 수정

```java
import java.lang.reflect.*;

public class RunTests {
  public stratic void main(String [] args) throws Exception{
    int tests  = 0;
    int passed = 0;
    Class<?> testClass = Class.forName(args[0]);
    for(Method m : testClass.getDeclaredMethods()){
      if(m.isAnnotationPresent(Test.class)){
        tests++;
        try{
          m.invoke(null);
          System.out.printf("테스트 %s 실패: 예외를 던지지 않음%n", m);
        }
        catch(InvocationTargetException wrappedExc){
          Throwable exc = wrappedExc.getCause();
          int oldPassed = passed;
          
          // ExceptionTest 어노테이션이 반환하는 값의 클래스 인스턴스 배열을 얻어온다.
          Class <? extends Throwable> excTypes =
            m.getAnnotation(ExceptionTest.class).value();
          
          // 예외들의 배열을 Class 
          for(Class <? extends Throwable> excType : excTypes){
            // 실제 발생한 예외와 어노테이션에서 래핑하는 예외값을 일일이 비교
            if(excType.isInstance(exc)){
              passed++;
              break;
            }
          }
          
          if(passed == oldPassed){
            System.out.printf("테스트 %s 실패: %s %n", m, exc);
          }
        }
      }
    }
  }
}
```





## 여러개의 파라미터 사용 (2) - @Repeatable

자바 8 에서는 여러개의 파라미터를 받는 애너테이션을 다른 방식으로도 만들 수 있다. 배열 매개변수를 사용하는 대신 애너테이션에 @Repeatable 메타 에너테이션을 다는 방식이다.

  

@Repeatable을 단 애너테이션은 하나의 프로그램 요소에 여러번 다는 것이 가능하다. 단, 주의할 점이 있다. 

1. @Repeatable을 단 애너테이션을 반환하는 '컨테이너 애너테이션'을 하나 더 정의하고, @Repeatable에 이 컨테이너 애너테이션의 class객체를 매개변수로 전달해야 한다.
2. 컨테이너 애너테이션은 내부 애너테이션 타입의 배열을 반환하는 value 메서드를 정의해야 한다. 
3. 마지막으로, 컨테이너 애너테이션 타입에는 적절한 보존정책(@Retention)과 적용대상(@Target)을 명시해야 한다. 그렇지 않으면 컴파일되지 않는다.  

  

```java
// 반복 가능한 애너테이션
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ExceptionTestContainer.class) // 이 부분에 주목하자
public @interface ExceptionTest{
  Class <? extends Throwable> value();
}

// 컨테이너 애너테이션
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTestContainer{
  ExceptionTest[] value();
}
```

@ExceptionTest 애노테이션이 @ExceptionTestContainer를 여러번 반복해서 받고 있다.  

사용

```java
@ExceptionTest(IndexOutOfBoundsException.class)
@ExceptionTest(NullPointerException.class)
public static void doublyBad(){...}
```

  

테스트 러너(로딩 구문)  

```java
import java.lang.reflect.*;

public class RunTests {
  public stratic void main(String [] args) throws Exception{
    int tests  = 0;
    int passed = 0;
    Class<?> testClass = Class.forName(args[0]);
    for(Method m : testClass.getDeclaredMethods()){
      if(m.isAnnotationPresent(ExceptionTest.class) || 
        	m.isAnnotationPresent(ExceptionTestContainer.class)){
        tests++;
        try{
          m.invoke(null);
          System.out.printf("테스트 %s 실패: 예외를 던지지 않음%n", m);
        }
        catch(Throwable wrappedExc){
          Throwable exc = wrappedExc.getCause();
          int oldPassed = passed;
          ExceptionTest[] excTests = 
            m.getAnnotationsByType(ExceptionTest.class);
          for(ExceptionTEst excTest : excTests){
            if(excTest.value().isInstance(exc)){
              passed++;
              break;
            }
          }
          
          if(passed == oldPassed)
            System.out.println("테스트 %s 실패 : %s %n", m, exc);
        }
      }
    }
  }
}
```



### @Repeatable 사용시 주의

@Repeatable 을 사용할 때 어노테이션을 로딩하는 구문(로더, 위 예에서는 테스트 러너)에서 Annotation 존재 여부 검사하는 구문에서 주의해야 하는 부분이 있다.  

- isAnnotationPresent() 메서드 사용시  
  ExceptionTest, ExceptionTestContainer 클래스 모두에 대해 존재 여부를 체크해야 한다.  

  

> 우리는 @ExceptionTest라는 어노테이션을 만들었다. 그리고 이 @ExceptionTest 어노테이션을 반복해서 사용가능하도록 선언했다. 이렇게 만든 ExceptionTest를 검사하는 체크 로직에서 isAnnotationPresent 메서드를 사용시 ExceptionTestContainer의 클래스 인스턴스도 함께 체크해야 한다.  
>
> 예를 들어 isAnnotationPresent() 메서드로 ExceptionTestContainer 존재 여부를 체크하는 로직을 추가하지 않은 후, 반복 애너테이션을 사용할 경우   isAnnotationPresent()로 반복 가능 애너테이션이 달렸는지 검사한다면 "그렇지 않다"라고 알려주게 된다.





# 마치면서

여러분이 다른 프로그래머가 소스코드에 추가정보를 제공할 수 있는 도구를 만드는 일을 한다면 적당한 애너테이션 타입도 함께 정의해 제공하자. 애너테이션으로 할 수 있는 일을 명명 패턴으로 처리할 이유는 없다.  







