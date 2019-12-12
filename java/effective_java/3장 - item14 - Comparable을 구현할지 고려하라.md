# 아이템 14 - Comparable을 구현할지 고려하라



## 핵심정리

순서를 고려해야 하는 값 클래스를 작성한다면 꼭 Comparable 인터페이스를 구현하여, 그 인스턴스들을 쉽게 정렬하고, 검색하고, 비교 기능을 제공하는 컬렉션과 어우러지도록 해야 한다.  

compareTo 메서드에서 필드의 값을 비교할 때 \<와 \> 연산자는 쓰지 말아야 한다. 그 대신 박싱된 기본 타입 클래스가 제공하는 정적 compare 메서드나 Comparator 인터페이스가 제공하는 비교자 생성 메서드를 사용하자.



## Overview

- 인터페이스를 활용하는 수많은 제네릭 알고리즘, 컬렉션의 힘을 누릴 수 있다.
- 사실상 자바 플랫폼 라이브러리의 모든 값 클래스와 열거 타입(아이템 34)이 Comparable을 구현했다.
- 알파벳, 숫자, 연대 같이 순서가 명확한 값 클래스를 작성한다면 반드시 Comparable 인터페이스를 구현하자.



# Comparable 인터페이스의 compareTo

compareTo 메서드는 Comparable 인터페이스의 유일무이한 메서드이다. compareTo 메서드가 수행하는 역할은 아래와 같다.

- 단순 동치성 비교
- 순서 비교
- Comparable을 구현했다는 것은 그 클래스의 인스턴스들에는 자연적인 순서(natural order)가 있음을 뜻한다.  

```java
public interface Comparator<T>{
  int compareTo(T t);
}
```

  

## 아주 간단한 예제  

ex) Comparable을 구현한 객체들의 배열 정렬 예

```java
Arrays.sort(a);
```

  

ex) 검색, 극단값 계산, 자동 정렬 되는 컬렉션 관리 예제

```java
public class WordList{
  public static void main(String [] args){
    Set<String> s = new TreeSet<>();
    Collections.addAll(s, args);
    System.out.println(s);
  }
}
```

  

## equals와의 비교

부가적으로 더 읽어보면 좋은 자료로 equals 규약(아이템 10 - 일반 규약을 지켜 재정의하라)이다. 모든 객체에 대해 전역 동치 관계를 부여하는 equals메서드와 달리, compareTo는 타입이 다른 객체를 신경쓰지 않아도 된다.  

  

타입이 다른 주어지면 간단히 ClassCastException을 던져도 되며 대부분 그렇게 한다. 물론 이 규약에서는 다른 타입 사이의 비교도 허용하는데, 보통은 비교할 객체들이 구현한 공통 인터페이스를 매개로 이뤄진다.  

  

# compareTo() 의 대/소 판단 규칙

동치성 등에 대해 조금전 살펴봤던 sigmoid 함수에 대한 규칙들을 다시 풀어서 쓰고 있다. 이 정도면 했던말 반복하기 연습하는 책이라고 봐도 무방하다.  


1. 두 객체 참조의 순서를 바꿔 비교해도 예상한 결과가 나와야 한다.  
   즉, 첫 번째 객체가 두 번째 객체보다 작으면, 두번째가 첫번째보다 커야 한다.  
   첫 번째가 두 번째와 크기가 같다면, 두 번째는 첫 번째와 같아야 한다.  
   첫 번째가 두 번째보다 크면 두 번째는 첫 번째보다 작아야 한다.  
2. 첫번째가 두번째보다 크고 두번째가 세번째보다 크면 첫번째는 세번째보다 크다.  
3. 크기가 같은 객체들 끼리는 어떤 객체와 비교하더라도 항상 같아야 한다.  



> 규칙 3 - 부연 설명    
>
> '크기가 같은 객체들 끼리는 어떤 객체와 비교하더라도 항상 같아야 한다'. 
>
> 는 필수는 아니지만 꼭 지켜주길 바란다고 언급하고 있다.  
>
> Collection, Set, Map 등의 컬렉션이 구현한 인터페이스의 동작은 equals의 규약을 따른다고 되어 있지만, 놀랍게도 정렬된 컬렉션들은 동치성을 비교할 때 equals 대신 compareTo를 사용하기 때문이다.  아주 큰 문제는 아니지만 주의해야 한다.



# compareTo() 내의 수학적 rule

이 부분을 삭제할까 했었다. 그런데 책에서 언급하는 내용이기도 하고, 수학적인 논리로 증명을 한다거나 논리를 세우는게 가끔은 모호한 규칙을 검증하려 할때 도움이 될수도 있겠다 싶어서 조금은 억지로 정리를 한다 ㅠㅠ...  

  

여기서는 입력이 어떤 값이든 어떤 비교나 이런 연산에 의해 출력값이 -1, 0, 1 로 나오는 시그모이드 함수(sigmoid function) 개념이 나온다.  

시그모이드 함수의 개념이 생소하다면 찾아봐도 되겠지만, 수학을 공부할 게 아니라면 아래의 내용만 기억하고 있으면 된다.  

> 시그모이드(sigmoid) 함수는 어떤 입력 x 값에 대해 특정 범위 에 있거나 특정 조건을 만족하는 경우에 대해 1 또는 0 또는 -1  과 같은 이진법 안에서 통할 수 있는 값을 출력값으로 내놓는다. 이것을 로그 함수와 오일러 상수로 특정 연산을 도출하기도 하는데 여기까지 알필요는 없고, 출력이 1/0 으로 나오거나 1/0/-1을 나오도록 해주는 함수라고 생각하면 된다. 그리고 책에서는 sigmoid 함수를 sgn() 으로 표현하고 있다.

  

자, 이제 compareTo에 적용된 sigmoid 함수 연산에 대해 알아보자.  

compareTo함수의 내용은 

- compareTo를 잡고 있는 객체가 compareTo함수에 입력으로 들어온 값보다 작으면
  음의 정수를  

- 같으면  
  0을  

- 크면  
  양의 정수를 반환한다.

  

이제 compareTo가 성립하기 위한 조건을 살펴보자. 이게 처음에는 그냥 쓸데없는 소리라고 생각했는데, 비교 연산의 논리를 맞추기 위해서는 아래의 논리 조건이 맞아야 한다는 생각이 들더라. 간단한 삼단 논법, 연역 추리 이런것들을 떠올리면 된다. 읽을때는 고통스럽지만 나중에 다시 읽을때 수월하게 읽힌다.  

- Comparable을 구현한 클래스는 모든 x,y 에 대해  
  sgn(x.compareTo(y)) == -sgn(y.compareTo(x)) 여야 한다. 
  (따라서 x.compareTo(y)는 y.compareTo(x)가 예외를 던질 때에 한해 예외를 던져야 한다. 반대 연산이 원래 연산과 같으므로 비교연산에 논리적인 오류가 있으니까.)

- Comparable을 구현한 클래스는 추이성을 보장해야 한다.  

  즉, (x.compareTo(y) > 0 && y.compareTo(z) > 0) 이면 x.compareTo(z) > 0 이다. 삼단논법과 유사하다.  

- Comparable을 구현한 클래스는 모든 z에 대해  
  x.compareTo(y) == 0 이면 sgn(x.compareTo(z)) == sgn(y.compareTo(z)) 다.   
  x와 y가 같으면, x와 z를 비교한 것과 y와 z를 비교한 결과 역시 같아야 한다.

- 아래 권고는 필수는 아니지만 지키는게 좋다.  
  (x.compareTo(y) == 0) == (x.equals(y)) 여야 한다.  
  Comparable을 구현하고 이 권고를 지키지 않는 모든 클래스는 그 사실을 명시해야 한다. 다음과 같이 명시하면 적당할 것이다.  
  "주의 : 이 클래스의 순서는 equals 메서드와 일관되지 않는다"  



# Comparable을 사용하는 경우

본문 내용

> Java의 주요 컬렉션 클래스인 TreeSet, TreeMap, 검색과 정렬 알고리즘을 활용하는 유틸리티 클래스인 Collections, Arrays의 경우 compareTo 규약을 사용하고 있다. 따라서 hashCode 규약을 지키지 못하면 해시를 사용하는 클래스와 어울리지 못하듯, compareTo 규약을 지키지 못하면 비교를 활용하는 클래스와 어울리지 못한다.  

  

## 실제 사용 예를 들어보자

간단한 비교연산, 대소판별 만 구현한다면 굳이 Comparable을 구현하여 사용하지 않아도 된다. 하지만 필요한 경우를 예를 들어본다면

- 연도별 데이터를 가져온 후 시간 순, 또는 id 순, 가나다 순, 알파벳 순으로 정렬하는 경우를 예로 들어볼 수 있을 듯 하다.
- 어떤 순차적인 수열과 같은 데이터를 정렬해야 하는 경우
- 등등 여러가지 경우가 있겠으나, 더 이상 생각해내지 못하고 있다. 조금씩 채워나가자.



아래(compareTo규약(규칙))에서 언급하고 있는데 컬렉션들은 동치성을 비교할 때에도 equals의 규약을 따른다고 되어있지만, 놀랍게도 정렬된 컬렉션들은 동치성을 비교할 때 실상은 equals 대신 compareTo를 사용한다고 한다.  

아주 큰 문제는 아니지만 주의해야 한다.  



## compareTo 메서드의 두 가지 활용방식 

### Comparator, Comparable

compareTo를 사용하는 방식으로

- Comparable 인터페이스 사용  
  Comparable을 구현하여 사용한다.
- 비교자(Comparator) 사용  
  하지만 해당 객체가 Comparable을 구현하지 않은 타입이거나, 표준이 아닌 순서로 비교해야 한다면 Comparator(비교자)를 대신 사용한다. 비교자는 직접 만들거나 자바가 제공하는 것 중에 골라 쓰면 된다.  

이렇게 두 가지의 방식을 이야기하고 있다.  

ex) 비교자를 사용하는 예제 (객체 참조 필드가 하나뿐인 비교자) 

>  CaseInsensiveString이 Comparable\<CaseInsensitiveString\>을 구현(implements)했다.  
>
> CaseInsensitiveString의 참조는 CaseInsensitiveString 참조와만 비교할 수 있다는 의미이다. 이것은 Comparable을 구현할 때 일반적으로 따르는 패턴이다.  

```java
public final class CaseInsensitiveString 
  	implements Comparable<CaseInsensitiveString>{
  public int compareTo(CaseInsensitiveString cis){
    return String.CASE_INSENSITIVE_ORDER.compare(s, cis, s);
  }
  ... // 나머지 코드 생략
}
```



ex) 클래스의 핵심필드가 여러개일때 비교를 중첩으로 할 경우에 대한 예제  

areaCode로 비교

- -> areaCode가 같으면 prefix로 비교
- -> prefix가 같으면 lineNum으로 비교

```java
public int compareTo(PhoneNumber pn){
  int result = Short.compare(areaCode, pn.areaCode);
  if(result == 0){
    result = Short.compare(prefix, pn.prefix);
    if(result == 0){
      result = Short.compare(lineNum, pn.lineNum);
    }
  }
}
```

자바 8에서는 이런 코드를 Comparator 인터페이스를 일련의 비교자 생성 메서드(comparator construction method)와 팀을 꾸려 메서드 연쇄방식으로 비교자를 생성할수 있게 되었다. (참고로, 자바의 정적 임포트(static import) 기능을 활용하면 정적 비교자 생성메서드들을 이름만으로 사용할 수 있어 코드가 더 깔끔해진다.)  

이 비교자들을 Comparable 인터페이스가 원하는 compareTo를 구현하는데에 비교적 멋있게 활용할 수 있다. java8의 비교자 생성메서드 활용방식은 약간의 성능저하가 뒤따른다. (PhoneNumber인스턴스의 정렬된 배열에 적용해보니 내컴퓨터에서 10%정도 느려졌다.)

  

ex) 비교자 생성 메서드를 활용한 비교자

```java
private static final Comparator<PhoneNumber> COMPARATOR = 
  comparingInt((PhoneNumber pn) -> pn.areaCode)
  	.thenComparingInt(pn->pn.prefix)
  	.thenComparingInt(pn->pn.lineNum);

public int compareTo(PhoneNumber pn){
  return COMPARTOR.compare(this, pn);
}
```

  

## 주의해야 하는 경우

가끔은 '값의 차'를 기준으로 

- 첫 번째 값이 두 번째 값보다 작으면 음수를 
- 두 값이 같으면 0을
- 첫 번째 값이 크면 양수를 반환

하는 경우를 보게 될 것이라고 책에서 이야기하고 있다. 책에서 이야기한걸 보니 실제로 그런 경우가 있는것 같다. 꼼꼼히 읽어보면 위에서 정리한 compareTo()의 대/소 판단 규칙과는 조금 다르다.  

 

ex) 대/소 값만을 판단해 -1, 0,1을 return 하지 않는 실제 경우

```java
static Comparator<Object> hashCodeOrder = new Comparator<>(){
  public int compare(Object o1, Object o2){
    return o1.hashCode() - o2.hashCode();
  }
}
```

  

이 경우 compare의 결과가 -1, 0, 1 을 return 하는 것이 아니라 양수/음수/0을 return하고 있다.  

- 이 경우 정수가 표현가능한 수의 범위를 넘어갈 경우  
  정수 오버플로우를 일으키거나
- IEEE 754 부동소수점 계산 방식에 따른 오류를 낼 수 있다.

그리고, 실제로 -1/0/1을 return 하는 코드보다 월등히 빠르지도 않다.  

  

위의 경우 아래와 같이 코드를 고쳐서 활용하는 것을 권장하고 있다.

ex) 정적 compare 메서드를 활용한 비교자

```java
static Comparator<Object> hashCodeOrder = new Comparator<>(){
  public int compare(Object o1, Object o2){
    return Integer.compare(o1.hashCode(), o2.hashCode());
  }
}
```



이것을 람다식으로 표현하면 아래와 같다.

```java
static Comparator<Object> hashCodeOrder = 
  Comparator.comparingInt(o->o.hashCode());
```









