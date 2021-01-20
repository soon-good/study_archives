# 프로퍼티, val, var

# 참고자료

[쾌락코딩 - 코틀린 프로퍼티](https://wooooooak.github.io/kotlin/2019/05/24/property/)

  

# 들어가기 전에

코틀린을 처음 배울때 굉장히 무시했던 개념이 프로퍼티 였다. 그리고... 나중에 이 프로퍼티라는 개념에서 막혀서 손을 못뗀 부분이 많았따. Java/C#/C 등의 언어에서 말하는 변수라고 취급하면서 대충 보고 넘어갔기 때문이다.  

코틀린을 사용할 때에는 이 프로퍼티라는 개념을 다른 언어와는 다른 개념으로 생각해야한다. 기존 언어기반의 사고방식 자체에서 벗어나 새로 개념을 세워야 한다.  

  

# 프로퍼티란??

코틀린에서 프로퍼티는 아래의 두 가지를 모두 통합해서 제공하는 기능을 의미한다.

- 필드 
- 접근자 (getter/setter)

개념을 이해하기 위해 몇가지 실험을 해보려 한다. 필드는 Java 에서는 필드와 getter/setter를 각각 선언한다. 하지만 코틀린에서는 프로퍼티를 선언하면 내부적으로 필드와 접근자(getter/setter)를 모두 선언하게 된다. 물론 getter/setter를 커스텀하게 오버라이딩 하는 것 역시 가능하다.  

오늘은 아래의 두가지 실험 코드로 var, val 키워드의 개념을 정리해보려고 한다. var, val 로 선언한 각각의 경우에 대해 내부적으로 변환된 JAVA 코드는 어떻게 변환되는지 확인하는 것이 목표이다.  

- 실험 1) var 프로퍼티의 Java 변환 코드
  - 코틀린 코드에서 프로퍼티를 var 로 선언했을 때의 Java 로 변환된 코드를 확인해보자.
- 실험 2) val 프로퍼티의 Java 변환 코드
  - 코틀린 코드에서 프로퍼티를 val 로 선언했을 때의 Java 로 변환된 코드를 확인해보자.  



# 실험 1) var 프로퍼티 Java 변환 코드

> 코틀린 코드에서 프로퍼티를 var 로 선언했을 때의 Java 로 변환된 코드를 확인해보자.  

  

## 코틀린 예제 (Fruit.kt)

Fruit.kt 라는 이름으로 Fruit 클래스를 코틀린으로 작성한 코드를 확인해보자.  
```kotlin
class Fruit (var type: String)
```

Fruit 크래스 내에서는 type 을 private로 선언하지 않았다. kotlin의 private 키워드는 뒤에서 정리할 예정이다. 위의 클래스에서 변수처럼 생긴 `type` 은 보통 `프로퍼티` 라고 부른다. `프로퍼티` 는 필드와 함께 getter/setter 의 조합을 제공해주는 역할을 한다.  참고로... JAVA 에서는 보통 필드라는 개념이 있는데, 이 필드라는 것은 코틀린에서는 프로퍼티의 내부에 있는 개념이다. 프로퍼티가 필드와 getter/setter를 감싸고 있다.  

이렇게 작성한 Fruit 클래스에서는 type 이라는 프로퍼티를 private 으로 선언하지 않았다. (변수처럼 생긴 `type` 을 지칭할 때 `프로퍼티` 라고 했다. `필드` 는 값을 저장하는 변수를 의미한다. `프로퍼티`는 필드 하나에 대한 getter/setter 까지 모두 제공해주는 개념이다.)  

## Java 코드 변환 (Fruit.decompiled.java)
Java 코드로 변환된 모습을 확인해보자.

```java
public final class Fruit {
   @NotNull
   private String type;

   @NotNull
   public final String getType() {
      return this.type;
   }

   public final void setType(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "<set-?>");
      this.type = var1;
   }

   public Fruit(@NotNull String type) {
      Intrinsics.checkNotNullParameter(type, "type");
      super();
      this.type = type;
   }
}
```

getter와 setter 가 모두 생성되어 있음을 알 수 있다. 이 것으로 미루어볼 때 **프로그램 내의 값/상태가 주기적으로 변경되거나 외부에 의해서 값/상태가 변경되어야 하는 경우에 사용될 것 이라는 것을 추측**할 수 있을 것 같다.  

## 결론 
var 로 선언한 프로퍼티는 아래와 같은 성격을 지니는 것으로 요약이 가능할 듯 하다.  
- 값 또는 상태를 변경할 수 있는 프로퍼티이다.  
- 프로퍼티에 대한 자바 코드 내에 getter/setter 모두가 존재한다.  
- 프로퍼티를 private로 지정하지 않았지만, java로 변환된 내부적인 동작의 필드의 선언은 private 로 선언된다. 대신 내부적으로 getter/setter 를 제공해준다.  
- getter/setter 모두 선언된 효과를 낸다.  
  
# 실험 2) val 프로퍼티의 Java 변환 코드
> 코틀린 코드에서 프로퍼티를 val 로 선언했을 때의 Java 로 변환된 코드를 확인해보자.  

## 코틀린 예제 (Fruit.kt)
Fruit.kt 라는 이름으로 Fruit 클래스를 코틀린으로 작성한 코드를 확인해보자. 프로퍼티를 val 로 선언했다.  
```kotlin
class Fruit (val type: String)
```
> 실험 1) 에서 강조했듯이 필드라고 지칭해서 언급하지 않았다. 프로퍼티는 필드하나에 대한 getter/setter 까지 모두 제공해주는 개념이다.   

## Java 코드 변환 (Fruit.decompiled.java)
Java로 변환된 코드를 확인해보자. 
```java
public final class Fruit {
   @NotNull
   private final String type;

   @NotNull
   public final String getType() {
      return this.type;
   }

   public Fruit(@NotNull String type) {
      Intrinsics.checkNotNullParameter(type, "type");
      super();
      this.type = type;
   }
}
```

Kotlin 코드 내에서 val 로 선언한 필드인 `type` 은 final 키워드로 지정되어 있는 것을 확인가능하다. 이로 미루어볼 때 val 키워드는 불변성 키워드임을 알 수 있다. 예를 들어보면, **어떤 동시성 프로그래밍 환경에서 객체의 상태가 변경되지 않음을 보장하기 위한 경우**를 예로 들수 있을것 같다.

## 결론 
결론을 지어보자.  
- val 키워드는 값 또는 상태가 변경되지 않도록 강제하고자 할 때 사용되는 키워드이다.  
- 읽기전용으로 사용된다.  
- 필드의 선언이 final 로 선언된다.  
- 프로퍼티를 private 지정하지 않았지만, java로 변환된 내부적인 동작의 필드의 선언은 private 로 선언된다. 대신 내부적으로 setter 를 제공해준다.  
- getter 만 선언된 효과를 낸다.  
