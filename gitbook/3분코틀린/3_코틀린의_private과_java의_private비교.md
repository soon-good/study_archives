# 프로퍼티, private, val, var

여기에 정리하는 내용은 gitbook.io 블로그에 정리했던 내용이다. gitbook.io에 정리하는 모든 내용들을 github에서 관리하기로 했다.

- [gitbook 블로그 - 3분 코틀린 / 프로퍼티,private,val,var](https://gosgjung.gitbook.io/lognomy/kotlin-study/3/private-val-var)

  

# 참고자료

- [쾌락코딩 - 코틀린 프로퍼티](https://wooooooak.github.io/kotlin/2019/05/24/property/)  

  

# 들어가기 전에

코틀린을 처음 배울때 굉장히 무시했던 개념이 프로퍼티 였다. 그리고... 나중에 이 프로퍼티라는 개념에서 막혀서 손을 못뗀 부분이 많았따. Java/C#/C 등의 언어에서 말하는 변수라고 취급하면서 대충 보고 넘어갔기 때문이다.  

코틀린을 사용할 때에는 이 프로퍼티라는 개념을 다른 언어와는 다른 개념으로 생각해야한다. 기존 언어기반의 사고방식 자체에서 벗어나 새로 개념을 세워야 한다.  

  

# 프로퍼티란??

코틀린에서 프로퍼티는 아래의 두 가지를 모두 통합해서 제공하는 기능을 의미한다.

- 필드 
- 접근자 (getter/setter)

개념을 이해하기 위해 몇가지 실험을 해보려 한다. 필드는 Java 에서는 필드와 getter/setter를 각각 선언한다. 하지만 코틀린에서는 프로퍼티를 선언하면 내부적으로 필드와 접근자(getter/setter)를 모두 선언하게 된다. 물론 getter/setter를 커스텀하게 오버라이딩 하는 것 역시 가능하다.

  

이제 코틀린의 프로퍼티 개념이 java 코드로 어떻게 변환되는지 실험을 통해서 확인해보자. 실험해 볼 케이스는 3가지 인데, 추후 더 추가하게 될지는 나도 장담하지 못하겠다. 오늘 지금 당장은 아래의 세가지 케이스로 글을 정리해봐야겠다.

- 실험 1) 일반적인 프로퍼티의 Java 변환 코드
  - 코틀린 코드에서 프로퍼티를 private 로 선언하지 않았을 때 Java 로 변환된 코드를 확인해보자.
- 실험 2) priviate 프로퍼티의 Java 변환 코드 
  - 코틀린 코드에서 프로퍼티를 private 로 선언했을 때의 Java 로 변환된 코드를 확인해보자. 
- 실험 3) val 프로퍼티의 Java 변환 코드
  - 코틀린 코드에서 프로퍼티를 val 로 선언했을 때의 Java 로 변환된 코드를 확인해보자.

  

# 실험 1) 일반적인 프로퍼티의 Java 변환 코드

> 코틀린 코드에서 프로퍼티를 private 로 선언하지 않았을 때 Java 로 변환된 코드를 확인해보자.

  

## 실험 (Fruit.kt, Fruit.decompiled.java)

Fruit.kt 라는 이름으로 Fruit 클래스를 코틀린으로 작성한 코드를 확인해보자.

```kotlin
class Fruit (var type: String)
```

  

이렇게 작성한 Fruit 클래스에서는 type 이라는 프로퍼티를 private 으로 선언하지 않았다. (변수처럼 생긴 `type` 을 지칭할 때 `프로퍼티` 라고 했다. `필드` 는 값을 저장하는 변수를 의미한다. `프로퍼티`는 필드 하나에 대한 getter/setter 까지 모두 제공해주는 개념이다.)  

Java 코드로 변환된 모습을 확인해보자.  

```kotlin
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

코틀린 코드에서 private 로 선언하지는 않았지만, Java 코드 내에서는  `type`  이라는 필드에 대한 private 으로 지정된 것을 확인 가능하다. (코틀린에 처음 입문했을 때 당황한 점이 프로퍼티를 private 로 선언했는데 해당 프로퍼티에 접근이 안되는 점이었다.)   

  

## 결론 

결론을 내려보면 이렇다. 멤버 프로퍼티의 선언시 접근제한자의 지정은 아래와 같이 이루어진다.

- 필드의 선언은 private 로 선언된다.
- getter/setter 의 선언은 public 으로 선언된다.

  

# 실험 2) private 프로퍼티의 Java 변환 코드

> 코틀린 코드에서 프로퍼티를 private 로 선언했을 때의 Java 로 변환된 코드를 확인해보자. 

  

## 실험 (Fruit.kt, Fruit.decompiled.java)

Fruit.kt 라는 이름으로 Fruit 클래스를 코틀린으로 작성한 코드를 확인해보자. 프로퍼티를 private 로 선언했다.

```kotlin
class Fruit (private var type: String)
```

  

실험 1)에서 강조했듯이 필드라고 지칭해서 언급하지 않았다. 프로퍼티는 필드하나에 대한 getter/setter 까지 모두 제공해주는 개념이다.  

Java로 변환된 코드를 확인해보자.  

```kotlin
public final class Fruit {
   private String type;

   public Fruit(@NotNull String type) {
      Intrinsics.checkNotNullParameter(type, "type");
      super();
      this.type = type;
   }
}
```

getter/setter 메서드 자체가 생성되지 않은 것을 확인할 수 있다. 프로퍼티를 private 로 선언했다는 것은 getter/setter 역시 접근이 불가능하다는 것을 의미한다.

  

## 결론 

결론을 지어보자. 프로퍼티를 private 로 선언하면 아래와 같은 효과를 낸다.

- 필드의 선언은 private 로 선언된다.
- getter/setter 가 존재하지 않으므로 리플렉션 말고는 멤버에 접근할 방법이 따로 존재하지 않는다.

​    

# 실험 3) val 프로퍼티의 Java 변환 코드

> 코틀린 코드에서 프로퍼티를 val 로 선언했을 때의 Java 로 변환된 코드를 확인해보자.

  

## 실험 (Fruit.kt, Fruit.decompiled.java)

Fruit.kt 라는 이름으로 Fruit 클래스를 코틀린으로 작성한 코드를 확인해보자. 프로퍼티를 val 로 선언했다.

```kotlin
class Fruit (val type: String)
```

  

실험 1)에서 강조했듯이 필드라고 지칭해서 언급하지 않았다. 프로퍼티는 필드하나에 대한 getter/setter 까지 모두 제공해주는 개념이다.   

Java로 변환된 코드를 확인해보자.   

```kotlin
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

코틀린 코드에서 private 로 지정한 프로퍼티인 type 은 java 코드로 변환되었을 때 final 로 선언된 것을 확인할 수 있다. 그리고 setter 가 생성되지 않은 것을 확인 가능하다.

  

## 결론 

결론을 지어보자. 프로퍼티를 val 로 선언하면 아래와 같은 효과를 낸다.

- 필드의 선언이 final 로 선언된다.
- getter 만 선언된 효과를 낸다. (setter는 java 코드에 생성되지 않는다.)

