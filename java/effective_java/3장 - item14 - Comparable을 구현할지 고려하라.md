# 아이템 14 - Comparable을 구현할지 고려하라



## 핵심정리

순서를 고려해야 하는 값 클래스를 작성한다면 꼭 Comparable 인터페이스를 구현하여, 그 인스턴스들을 쉽게 정렬하고, 검색하고, 비교 기능을 제공하는 컬렉션과 어우러지도록 해야 한다.  

compareTo 메서드에서 필드의 값을 비교할 때 \<와 \> 연산자는 쓰지 말아야 한다. 그 대신 박싱된 기본 타입 클래스가 제공하는 정적 compare 메서드나 Comparator 인터페이스가 제공하는 비교자 생성 메서드를 사용하자.



## Overview

- 인터페이스를 활용하는 수많은 제네릭 알고리즘, 컬렉션의 힘을 누릴 수 있다.
- 사실상 자바 플랫폼 라이브러리의 모든 값 클래스와 열거 타입(아이템 34)이 Comparable을 구현했다.
- 알파벳, 숫자, 연대 같이 순서가 명확한 값 클래스를 작성한다면 반드시 Comparable 인터페이스를 구현하자.



# Comparable 인터페이스의 유일 무이한 메서드 compareTo

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

  

## compareTo 메서드의 일반적인 규칙 (동치성, 추이성 등등)

여기서는 입력이 어떤 값이든 어떤 비교나 이런 연산에 의해 출력값이 -1, 0, 1 로 나오는 시그모이드 함수(sigmoid function) 개념이 나온다. 









