# null 처리, null 참조 처리

코틀린은 변수 선언시 기본 옵션은 널 처리를 허용하지 않는다. 하지만 특정 연산자('?')를 사용하면 **해당 변수에 null 값으로 초기화**를 허용한다.  처음 코틀린을 배울때 이 내용이 굉장히 생소하기도 했고, 코드를 칠때 자꾸 망설이게 만들었었다. 내가 치는 코드가 null 을 허용하게 하는 방법이 생각이 안나는데, 개념을 또 찾아보려니 지치고 하는 현상이었던 것 같다. 이런 현상이 자주 반복되다 보니 바로 바로 찾아서 개념을 확인해보는 습관을 들였다. 



# 참고자료

- [Kotlin 안전한 null 처리 - thdev.tech](https://thdev.tech/kotlin/2016/08/04/Kotlin-Null-Safety/)
- [kotlinlang.org - Null Safety](https://kotlinlang.org/docs/reference/null-safety.html) 
- [Keywords and Operators - kotlinlang.org](https://kotlinlang.org/docs/reference/keyword-reference.html)
- ["Unsafe" cast operator - kotlinlang.org, Type Checks and Casts: 'is' and 'as'](https://kotlinlang.org/docs/reference/typecasts.html#unsafe-cast-operator)



# null 허용 변수 - '타입?'

> 코틀린에서 변수 선언시 기본 옵션은 널처리를 허용하지 않는다. 하지만 연산자? 를 사용하면 null 을 허용한다. 

코틀린에서는 변수 선언시 ":" 뒤에 타입을 명시하는 데 '**타입?**' 으로 지정해서 선언하는 경우도 있다. 이렇게 '**타입?**' 으로 선언된 변수는 null 값을 허용한다는 의미로 선언된 것이다. (코틀린에서는 변수선언시 기본옵션으로 널 값 할당을 허용하지 않는다. 하지만 '타입?' 으로 선언된 변수는 null 값을 허용한다.)

  

String 타입의 변수 apple, banana 를 예로 들어보자.  

- val apple : String = "Apple"  
  - 선언과 동시에 "Apple" 로 초기화  
- val banana : String? = null  
  - 선언과 동시에 null 로 초기화  
  - '**타입?**' 은 해당 변수가 null 로 초기화 되는 것을 허용한다 는 의미이다.  

‌

String, String? 은 명백히 다른 자료형이다. 이 Int, Int? 도 명백히 다른 자료형이다. Double, Double? 역시 다른 자료형이다. Employee, Employee? 역시 다른 자료형이다. Apple, Apple? 역시 다른 자료형이다. 널값을 허용하는 자료형 자체를 다르게 인식하려 노력하자. 



# Java 변환 코드 확인해보기

코틀린 파일이 Java 코드로 변환된 내용을 확인해보자. 각각 아래의 세가지 케이스에 대해 어떻게 변환되는지 확인해보려 한다.

- val 변수명 : String 
- var 변수명 : String?
- val 변수명 : String?
  - val 은 java 로 변환시 final 변수명 = 대입; 으로 변환되는데, 이 부분에 주목해서 확인해볼 예정이다.

이제, 각각의 코드가 어떻게 변환되는지 확인해보자.  



**NullSample.kt ** 

예제로 작성한 NullSample.kt 파일이다. 

```kotlin
class NullSample {
    val normalStr : String = "normalString"
    var varNullableString : String? = null
    // 아래 코드는 Java 로 변환시 에러를 낸다.
    val valNullableString : String? = null 
}
```



- **val normalStr : String = "normalString"**
  - val 로 선언했지만, 선언과 동시에 초기화했다. 변수 `normalStr` 이 가진 문자열은 추후 변경되지 못한다.
  - val 로 선언된 변수 `normalStr` 은 java 로 변환시 final String normalStr = "normalString" 과 같은 의미이기 때문이다.
  - 코틀린에서는 val 로 선언한 프로퍼티는 getter 만 제공된다. 
  - (코틀린에서 프로퍼티는 변수, getter/setter 를 묶어서 제공하는 단위이다. [참고](https://gosgjung.gitbook.io/lognomy/kotlin-study/3/private-val-var) )
- **var varNullableString : String? = null**
  - @Nullable private String `변수명` ; 과 같은 의미이다.
- **val valNullableString : String? = null**
  - @Nullable private final String `변수`  ; 과 같은 의미이다.‌

코틀린 Decompiler 가 java로 변환해준 코드는 아래와 같다.



**NullSample.decompiled.java**

```java
public final class NullSample {
   @NotNull
   private final String normalStr = "normalString";
   @Nullable
   private String varNullableString;
   @Nullable
   private final String valNullableString;

   @NotNull
   public final String getNormalStr() {
      return this.normalStr;
   }

   @Nullable
   public final String getVarNullableString() {
      return this.varNullableString;
   }

   public final void setVarNullableString(@Nullable String var1) {
      this.varNullableString = var1;
   }

   @Nullable
   public final String getValNullableString() {
      return this.valNullableString;
   }
}
```



**val, var** 

val 로 선언한 변수 `normalStr` , `valNullableString` 은 getter 만 존재한다. 그리고 val로 선언한 변수의 java 코드는 모두 final 키워드로 선언되어 있는 것을 확인할 수 있다. var 로 선언한 변수 `varNullableString`  은 getter/setter 가 모두 존재한다. 

  

**타입? vs 타입**  

먼저 **타입?** 으로 선언한 변수를 확인해보자‌

- **var varNullableString : String? = null**  
  - @Nullable private String `변수명` ; 으로 변환 되었음을 변환된 코드에서 확인가능하다.  
- **val valNullableString : String? = null**  
  - @Nullable private final String `변수` ; 으로 변환되었음을 변환된 코드에서 확인가능하다.  

가장 정상적인 경우인 **타입** 으로 선언한 변수는 아래와 같이 변환되었다.  

- **val normalStr : String = "normalString"**  
  - @NotNull private final String normalStr = "normalString";  



# null 값의 안전한 호출 (?. 연산자)

**참고자료** 

- [kotlinlang.org - Null Safety](https://kotlinlang.org/docs/reference/null-safety.html#safe-calls)

  

null 이 허용된 변수에 대해 참조/멤버연산자인 `.` 이 호출되는 경우가 있다. 이 경우`널값.멤버`  를 호출하는 곳에서  Null Pointer Exception 이 발생할 수 있다. 이렇게 Null 값에 대한 Exception 이 발생하면 다른 로직이 호출되지 않으므로 제품에서는 치명적인 버그가 될 수 있다.  

kotlin 에서는 ?. 연산자를 제공한다. 예를 들어 `멤버?.프로퍼티` 를 호출할 때 `멤버` 가 null 일 경우 `프로퍼티` 를 참조하거나 호출하지 않고 그대로 null 을 반환한다.  

null 을 허용하는 프로퍼티를 처리하는 짧은 예제코드를 정리해보면, 나중에 다시 볼때 코드만 보면 되기 때문에 코드 기반으로 정리해보려 한다. 작성하려는 코드는 아래와 같다.

- **PoliceOfficer.kt**
  - PoliceOfficer 클래스는 data 클래스 이며 아래의 필드들을 가지고 있따.
  - name : String
  - address : String?
    - null 값 허용으로 했다.
  - id : Long
- **PoliceOfficerTest.kt**
  - PoliceOfficer 클래스의 인스턴스를 두개 선언하고 각 인스턴스를 리스트 `policeOfficers` 에 add 한다.
  - 이 `policeOfficers` 의 각 요소를 차례로 순회하면서 address 를 대문자로 변환하는 함수를 짜보자.
  - address 필드는 null 허용이기 때문에 Kotlin 가상 머신이 어떻게 처리하는지 확인해볼 수 있을 것 같다.



## PoliceOfficer.kt

```kotlin
data class PoliceOfficer (
    val name : String,
    var address : String?,
    val id : Long
){
}
```

address 프로퍼티는 null 허용 자료형인 String? 으로 선언되어 있다. 이 String? 이 포함되어 있을때 코틀린 컴파일러는 어떻게 처리할까? 테스트 코드로 확인해보자.  



## PolliceOfficerTest.kt

```kotlin
package nullable_operator

import PoliceOfficer
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class PoliceOfficerTest {

    @Test
    @DisplayName("Nullable Test")
    fun testNullableAssignment(): Unit {
        val policeOfficer1 : PoliceOfficer = PoliceOfficer(
            name = "경찰관#1",
            address = null,
            id = 1
        )

        val policeOfficer2 : PoliceOfficer = PoliceOfficer(
            name = "경찰관#2",
            address = "South Korea",
            id = 2
        )

        val policeOfficers : MutableList<PoliceOfficer> = mutableListOf<PoliceOfficer>()
        policeOfficers.add(policeOfficer1)
        policeOfficers.add(policeOfficer2)

        policeOfficers.forEach{
            println("${it.name} :: ${it.address?.toUpperCase()}")
        }
    }
}
```



데이터를 아래와 같이 생성했다.

- 경찰관#1
  - name = "경찰관#1"
  - address = null
  - id = 1
- 경찰관#2 
  - name = "경찰관#2"
  - address = "South Korea"
  - id = 2

이 데이터 들을 모두 리스트에 담아서 각각의 address 를 대문자로 변환시키는 테스트 프로그램이다. 경찰관#2 는 명확히 SOUTH KOREA 가 나올 것을 알고 있다. 하지만 경찰관#1 은 어떤 결과가 나올까? 출력결과를 살펴보면 아래와 같다.  



### 출력결과

```kotlin
경찰관#1 :: null
경찰관#2 :: SOUTH KOREA
```

null 인 요소에 대해서는 toUpperCase() 를 수행하지 않고 null 을 그대로 출력한 것을 확인 가능하다. 



# Null Pointer Exception 관련 연산자들‌

## ?: 연산자 (Elvis 연산자)

참고 : [kotlinlang.org - Elvis Operator](https://kotlinlang.org/docs/reference/null-safety.html#elvis-operator)  

연산자의 좌측과 우측에 피 연산자가 존재하는데, 왼쪽에는 실제 null 이 될수도 있는 값이고, 우측의 값은 null 이 될 경우 대체할 값을 반환하는 역할을 한다. 이해를 위해 두가지 예제를 정리해보면 아래와 같다.  

### 예제 1) ?: 연산자 테스트 (Elvis Operator) #1

```kotlin
class NullTest {

    @Test
    @DisplayName("?: 연산자 테스트 (Elvis Operator) #1 ")
    fun testElvisOpertor1 () : Unit {
//        var b = "abcdefgh"
        var b : String? = null
        val l: Int = if (b != null) b.length else -1
        val m: Int = b?.length ?: -1

        println("l : ${l}")
        println("m : ${m}")
    }
}
```



**출력결과**  

```plain
l : -1
m : -1
```

  

### 예제 2) ?: 연산자 테스트 (Elvis Operator) #2

**ElectricalEnergy.kt**  

```kotlin
data class ElectricalEnergy (
    var kwh : Double?,
    var voltage: Double?,
    val date: LocalDate
){
}
```

  

**테스트 코드**  ‌ 

주의할 점이 하나 있다. **Elvis Operator 를 통해 반환받는 값은 Any 라는 타입**이라는 것이다. 따라서 아래의 for 문에서 만약 kwh 와 voltage를 곱하거나 더하거나 나누고 빼는 산술연산을 수행하는 것은 불가능하다. Double? 과 Any 간에 산술연산을 했기 때문이다.   

```kotlin
@Test
@DisplayName("?: 연산자 테스트 (Elvis Operator) #2 ")
fun testElvisOperator2 () : Unit {
    val strDay1 = "20201203"
    val strDay2 = "20201204"

    val ofPattern = DateTimeFormatter.ofPattern("yyyyMMdd")
    val startDate = LocalDate.parse(strDay1, ofPattern)
    val endDate = LocalDate.parse(strDay2, ofPattern)

    val e1 = ElectricalEnergy(kwh = 2000.0, voltage = null, date = startDate)
    val e2 = ElectricalEnergy(kwh = 1500.0, voltage = null, date = endDate)

    val elecList = listOf<ElectricalEnergy>(e1, e2)
    elecList.forEach {
        val nullProcessed : Any = it.voltage?.dec() ?: 0
        println("voltage :: ${nullProcessed}")
    }
}
```

  

**출력결과**  

```kotlin
voltage :: 0
voltage :: 0
```



## !! 연산자

> 참고자료 : https://kotlinlang.org/docs/reference/null-safety.html#the--operator‌

!! 연산자는 어떤 변수가 다른 변수에 대입되거나, 어떤 변수에 대해 참조연산을 할 때 변수 뒤에 붙여서 사용한다. NPE 가 발생하도록 강제하도록 할경우에 사용하는 경우가 많다고 한다.  

- !! 연산자가 뒤에 붙은 변수 a 가 다른 어떤 변수 b에 대입될때 
  - val a : String? = null
  - val b : String = a!!
  - 이때  변수 A가 null 이면 NPE(Null Pointer Exception) 이 발생한다.  

공식 문서에서는 !! 연산자를 이렇게 설명하고 있다.

> he third option is for NPE-lovers: the not-null assertion operator (`!!`) converts any value to a non-null type and throws an exception if the value is null. We can write `b!!`, and this will return a non-null value of `b` (e.g., a `String` in our example) or throw an NPE if `b` is null:



‌

### 예제 1) 

```kotlin
@Test
@DisplayName("!! 연산자 테스트 >> 공백문자로 테스트해보기")
fun testNonNullCase1() : Unit {
    val whitespace: String = ""
    val length1 = whitespace!!.length
    println(length1)
}
```

출력결과

```plain
0
```

‌

### 예제 2)

```kotlin
@Test
@DisplayName("!! 연산자 테스트 >> nullable에 notnull 을 대입해보기")
fun testNonNullCase2() : Unit {
    val label1 : String? = "테스트"
    val label2 : String = label1!!
    val label3 : String? = label1!!
    val label4 : String? = null
//        val label5 : String = label4!!  // NPE 발생

    println("label1 = ${label1}")
    println("label2 = ${label2}")
    println("label3 = ${label3}")
    println("label4 = ${label4}")
//        println("lable5 = ${label5}")
}
```

출력결과

```plain
label1 = 테스트
label2 = 테스트
label3 = 테스트
label4 = null
```

‌

### 예제 3)

```kotlin
@Test
@DisplayName("!! 연산자 테스트 >> nullable 데이터 테스트")
fun testNonNullCase3() : Unit {
    val nullableString: String? = null
    // null 데이터에 대해서 NPE (Null Pointer Exception 을 발생시킨다.)
    println("nullableString!!.length = ${nullableString!!.length}")
}
```

출력결과  

Null Pointer Exception이 발생해야만 한다.

```plain
kotlin.KotlinNullPointerException
	at nullable_operator.NonNullOperatorTest.testNonNullCase3(NonNullOperatorTest.kt:37)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:389)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:115)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeTestMethod$6(TestMethodTestDescriptor.java:167)
	at org.junit.jupiter.engine.execution.ThrowableCollector.execute(ThrowableCollector.java:40)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestMethod(TestMethodTestDescriptor.java:163)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:110)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:57)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.lambda$execute$3(HierarchicalTestExecutor.java:83)
	at org.junit.platform.engine.support.hierarchical.SingleTestExecutor.executeSafely(SingleTestExecutor.java:66)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:77)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.lambda$null$2(HierarchicalTestExecutor.java:92)
	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
	at java.util.stream.ReferencePipeline$2$1.accept(ReferencePipeline.java:175)
	at java.util.Iterator.forEachRemaining(Iterator.java:116)
	at java.util.Spliterators$IteratorSpliterator.forEachRemaining(Spliterators.java:1801)
	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:173)
	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
```



# 안전한 Casting (as? 연산자)

> 참고자료 :  
>
> - [Keywords and Operators - kotlinlang.org](https://kotlinlang.org/docs/reference/keyword-reference.html)
>   - as 연산자는 보통 type cast 를 할 때 사용한다.
> - ["Unsafe" cast operator - kotlinlang.org, Type Checks and Casts: 'is' and 'as'](https://kotlinlang.org/docs/reference/typecasts.html#unsafe-cast-operator)



## Overview

이것 하나만 기억해두고 아래의 예제들을 체크해보자. 

- String 타입과 String? 타입을 코틀린 컴파일러는 명백히 다른 타입으로 인식한다.
- Int 타입과 Int? 타입을 코틀린 컴파일러는 명백히 다른 타입으로 인식한다.
- Long 타입과 Long? 타입을 코틀린 컴파일러는 명백히 다른 타입으로 인식한다. 
- 나머지 다른 타입들에 대해서도 동일하게 `타입` 과 `타입?` 은 명백히 다른 타입으로 인식된다.



## Unsafe cast operator (안전하지 않은 Casting)‌

as? 는 안전한 캐스팅에 사용되는 연산자이다. 하지만, as?를 확인해보기 전에 as를 참고 삼아 정리해두면 더 좋을 것 같아 as 연산자에 대해서 먼저 정리해보기로 했다. 일반적으로 as 는 현재 타입을 다른 타입으로 캐스팅할 때 사용한다.   

java 언어에서의 예를 들어보면 `Map<String, Object>` 를 사용할 때 가끔 Java 의 Object 타입을 String으로 바꾸는 등의 작업을 하는 경우 역시 예로 들수 있을 것 같다.  



### 예제)

```kotlin
@Test
@DisplayName("unsafe operator 1")
fun testOperatorAsCase1() : Unit {
    val y : Any = "hello"
    val x : String = y as String
    println("x is ... ${x}")
}
```

출력결과 

```plain
x is ... hello
```

‌

## Safe case operator (안전한 Casting)‌

as? 는 안전한 캐스팅에 사용되는 연산자이다. as? 에 대해서 캐스팅이 적용될 수 있는 경우를 나열해보면 이해가 쉬울 것 같다.‌

- String? 타입 변수에 String 타입을 대입해서 캐스팅하는 것은 불가능하다. String? 과 String 은 명백히 다른 타입이기 때문이다.
- Int? 타입 변수에 Int 타입을 대입해서 캐스팅하는 것은 불가능하다. Int? 과 Int 은 명백히 다른 타입이기 때문이다.
- Long? 타입 변수에 Long 타입을 대입해서 캐스팅하는 것은 불가능하다. Long? 과 Long은 명백히 다른 타입이기 때문이다.
- 나머지 다른 타입들에 대해서도 동일하게 `타입` 과 `타입?` 은 명백히 다른 타입이고 `as` 와  `as?`  는 명백히 다른 캐스팅 연산이다.‌

  

### 예제1)

```kotlin
@Test
@DisplayName("unsafe operator 1")
fun testOperatorAsCase1() : Unit {
    val y : Any = "hello"
    val x : String = y as String
    println("x is ... ${x}")
}
```

출력결과

```plain
x is ... hello
```

  

### 예제2)

```kotlin
@Test
@DisplayName("unsafe operator 2")
fun testOperatorAsCase2() : Unit {
    val y : Any? = null
    val x : String? = y as? String?
    println("x is ... ${x}")
}
```

출력결과 

```plain
x is ... null
```

‌

### 예제3)

```kotlin
@Test
@DisplayName("unsafe operator 3")
fun testOperatorAsCase3() : Unit {
    val y : Any? = null
    val x : String? = y as String   // Type cast Exception ( Any? -> String )
    println("x is ... ${x}")
}
```

출력결과  ‌

`Type Cast Exception` 이 발생한다.  

```plain
kotlin.TypeCastException: null cannot be cast to non-null type kotlin.String

	at nullable_operator.TheOperatorAsTest.testOperatorAsCase3(TheOperatorAsTest.kt:28)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:498)
	at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:389)
	at org.junit.jupiter.engine.execution.ExecutableInvoker.invoke(ExecutableInvoker.java:115)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.lambda$invokeTestMethod$6(TestMethodTestDescriptor.java:167)
	at org.junit.jupiter.engine.execution.ThrowableCollector.execute(ThrowableCollector.java:40)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.invokeTestMethod(TestMethodTestDescriptor.java:163)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:110)
	at org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor.execute(TestMethodTestDescriptor.java:57)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.lambda$execute$3(HierarchicalTestExecutor.java:83)
	at org.junit.platform.engine.support.hierarchical.SingleTestExecutor.executeSafely(SingleTestExecutor.java:66)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.execute(HierarchicalTestExecutor.java:77)
	at org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutor.lambda$null$2(HierarchicalTestExecutor.java:92)
	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:183)
	at java.util.stream.ReferencePipeline$2$1.accept(ReferencePipeline.java:175)
	at java.util.Iterator.forEachRemaining(Iterator.java:116)
	at java.util.Spliterators$IteratorSpliterator.forEachRemaining(Spliterators.java:1801)
	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)
	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:150)
```

  

# Nullable 값들에 대한 컬렉션들, filterNotNull() 함수

Nullable 값들이 컬렉션내에 포함되는 경우가 있다. 

예)

```kotlin
val nullableList: List<Int?> = listOf(1, 2, null, 4)
```

그런데 이 값을 Int 타입을 나열하는 리스트 타입의 컬렉으로 변환하고 싶을 경우 filterNotNull 을 사용해 대입해주면 된다.

```kotlin
val intList: List<Int> = nullableList.filterNotNull()
```

