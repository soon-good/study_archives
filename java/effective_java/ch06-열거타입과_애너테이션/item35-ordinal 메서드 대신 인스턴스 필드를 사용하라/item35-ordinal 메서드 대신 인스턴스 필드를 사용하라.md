# item36 - 비트 필드 대신 EnumSet을 사용하라

# 정수 열거 상수를 사용할 경우의 단점

예전의 코드들에는 열거하려는 상수 값들이 단독이 아닌 집합으로 사용될 경우 각 상수를 선언할 때마다 2의 거듭제곱을 한번 더 하도록 선언 및 정의하는 경우가 있었다고 한다. 예를 들어 아래와 같은 경우이다.  

```java
public class Text{
    public static final int STYLE_BOLD 			= 1 << 0;	// 1
    public static final int STYLE_ITALIC		= 1 << 1;	// 2
    public static final int STYLE_UNDERLINE		= 1 << 2;	// 4
    public static final int STYLE_STRIKETHROUH	= 1 << 3;	// 8
    
    public void applyStyles (int styles) { ... }
}
```
이와 같은 방식은 구닥다리 기법이라고 설명하고 있다. 위의 상수 코드를 아래와 같이 비트 연산으로 하나의 집합으로 모을 수 있다고 한다. 실제로 2009년도 ~ 2013년도 사이의 학부 재학 시절에 Swing 등의 GUI 코드에서는 아래와 같은 코드가 많았다.

```java
text.applyStyles(STYLE_BOLD | STYLE_ITALIC);
```

위와 같이 비트 필드를 사용하면 비트별 연산을 사용해 합집합, 교집합과 같은 집합 연산을 효율적으로 수행가능하다. 하지만 비트 필드는 정수 열거 상수(정수로 직접 선언한 상수)의 단점을 그대로 지니고 있다.
ex)

```java
public class Text{
	// ...
    public static final int STYLE_UNDERLINE = 1 << 2;
}
```
추가로 아래의 단점이 있다.
- 비트 필드 값이 그대로 출력될 경우 단순한 정수 열거 상수를 출력할 때보다 해석하기가 훨씬 어렵다.
- 비트 필드 하나에 여러 원소들을 포함시켜서 포함된 원소들을 모두 순회하기도 쉽지 않다.
- 최대 몇 비트가 필요한지를 API 작성시 미치 예측하여 적절한 타입(int,log)으로 선언해야 하는데, 이 경우 변경이 발생할 경우 API를 수정하지 않고는 비트 수(32비트 or 64비트)를 더 늘릴수 없기 때문이다.

# EnumSet을 이용해보자
> java.util 패키지의 EnumSet 클래스는 열거타입 상수의 값으로 구성된 집합을 효과적으로 표현해준다.

## 예제

EnumSet을 이용해 위의 코드를 변경해보면 아래와 같다. ([예제링크](https://github.com/WegraLee/effective-java-3e-source-code/blob/master/src/effectivejava/chapter6/item36/Text.java))
```java
import java.util.*;

public class Text {
    public enum Style { BOLD, ITALIC, UNDERLINE, STRIKETHROUGH }
    
    // 어떤 Set을 넘기든 괜찮지만, EnumSet을 넘기는것이 가장 좋다.
    public void applyStyles(Set<Styles> styles) {
    	System.out.printf("Applying styles %s to text%n",
                Objects.requireNonNull(styles));
    }
    
    // 사용 예
    public static void main(String[] args) {
        Text text = new Text();
        text.applyStyles(EnumSet.of(Style.BOLD, Style.ITALIC));
    }
}
```
> 위의 코드에서는 applyStyles 메서드가 EnumSet\<Style\> 이 아닌 Set\<Style\> 을 받고 있다. 모든 클라이언트가 EnumSet을 건네리라 짐작되는 상황이라도 이왕이면 인터페이스로 받는게 일반적으로 좋은 습관이다. 
>
> 조금 특이한 경우에 EnumSet이 아닌 다른 Set 구현체를 넘기더라도 처리가 가능하게 되기 때문에 인터페이스를 사용하는 것을 권장한다고 잔소리를 하고 있다. (아이템 64 - 객체는 인터페이스를 사용해 참조하라)

## EnumSet의 특징
- EnumSet 은 Set 인터페이스를 완벽하게 구현(implements)하고 있다.
- 타입 안전하다.
- 다른 타입의 Set과도 함께 사용가능하다.
- EnumSet 의 내부는 비트 벡터로 구현되었는데, 원소가 총 64개 이하라면, 대부분의 경우 EnumSet 전체를 long 변수 하나로 표현하여(자릿수가 64개이므로) 비트 필드에 비견되는 성능을 보여준다.
- EnumSet 의 내부는 removeAll, retainAll 과 같은 대량 작업 수행시 비트를 효율적으로 처리할 수 있는 산술연산을 써서 구현했다.
- EnumSet은 집합 생성 등 다양한 기능의 정적 팩터리를 제공하는데, 위의 예제에서는 그 중 of 메서드를 사용했다.  

# 핵심정리
EnumSet의 유일한 단점은 (자바 9까지는 아직) 불변 EnumSet을 만들수 없다는 것이다.  
열거할 수 있는 타입을 한데 모아 집합 형태로 사용한다고 해도 비트 필드를 사용할 이유는 없다.  

EnumSet 클래스는
- 비트 필드 수준의 명료함과 성능을 제공하고
- enum(열거타입)의 장점까지 선사(item 34)하기 때문이다.  
  

> **불변 EnumSet을 만들지 못하는 문제**  
>
> 자바 11까지도 불변 EnumSet을 만들지 못하는 문제에 대한 수정은 이루어지지 않았다고 한다. 조슈아 블로크의 바람과 달리 자바 개발팀은 불변 EnumSet이 그리 필요하지 않다고 보는 것 같다.  
>
> 구글의 Guava 라이브러리를 사용하면 불변 EnumSet을 만들수 있긴 하지만(https://bit.ly/2NlxW60 참고), 이 역시 내부에서는 EnumSet을 사용해 구현했으므로 성능 면에서는 손해다.  

# 내 생각
상수의 사용에 성능까지 고려할 필요는 없다고 본다. 라이브러리 설계자의 경우 엄격하게 기준을 세워야 하므로 성능과 불변 EnumSet 여부를 따지는 것이라고 본다. 상수의 경우 일반 데이터들 처럼 데이터가 많거나 예외적인 상황으로 병목현상을 유발시키는 경우가 없기 때문에 위의 단점을 감수하더라도 사용해도 되지 않을까? 하는 생각이다.  