# item35 - ordinal 메서드 대신 인스턴스 필드를 사용하라

enum을 처음 사용하는 경우 생성자를 어떻게 사용하는지 잘 모르기 때문에 이것 저것 IDE에서 시도를 하다가 ordinal을 사용하는 경우가 있다. 이 책에서는 그 경우에 대해서 친절하게 어떻게 하면 되는지를 설명하고 있다.  

개인적인 경험으로, 이 방식은 사실 알고나면 굉장히 쉬운 방법이고 누구든 응용할때 누구도 생각지 못한 창의적인 방법으로 Enum을 활용할 수 있다.  

# 예제 시나리오

이 책에서는 합주단의 종류를 연주자가 1명인 솔로(solo)부터 10명인 디텍트(detect)까지 정의하는 열거타입을 예로 들고 있다.

# 나쁜 예 - ordinal을 사용하는 경우

예제) ordinal을 잘못 사용한 예 - 따라하지 말것

```java
public enum Ensemble{
  SOLO, DUET, TRIO, QUARTET, QUINTET,
  SEXTET, SEPTET, OCTET, NONET, DECTET;
  
  public int numberOfMusicians() {
    return ordinal() + 1;
  }
}
```

  

상수 선언 순서를 바꾸는 순간 

- numberOfmusicians()메서드에서 에러는 나지 않지만 오동작이 발생하게 된다.  
- 이미 사용 중인 정수와 값이 같은 상수는 추가할 방법이 없다.  
  예를 들면 8중주(octet) 상수가 이미 있으므로 똑같이 8명이 연주하는 복4중주(double quarter)는 추가할 수 없다.  
- 값을 중간에 비워둘 수 없다.  
  예를 들면 12명이 연주하는 3중 4중주를 추가하려 할때를 예로 들수 있다. 그러려면 11명짜리 상수도 채워야 하는데 11명으로 구성된 연주를 일컫는 이름이 없다. 이 경우 빈 값을 채워넣어 상수로 메꾸어 더미(dummy)상수로 같이 추가하게 된다. 코드가 지저분해지며, 유지보수에 용이하지 않고 실용적이지 못하다.

# 해결책

어려운 내용은 아니다. 직접 코딩을 해보거나 받아 적어보면서 IDE의 도움을 조금만 받으면 Enum내에 클래스의 인스턴스 필드처럼 특정 필드를 가지고 있는 방식에 대해 배울 수 있다.  

### enum(열거타입)에 연결된 값은 ordinal 메서드로 얻지 말고 인스턴스에 저장하자.

예제)  

```java
public enum Ensemble{
  SOLO(1), DUET(2), TRIO(3), QUARTET(4), QUINTET(5),
  SEXTET(6), SEPTET(7), OCTET(8), DOUBLE_QUARTET(8),
  NONET(9), DECTET(10), TRIPLE_QUARTET(12);
  
  private final int numberOfMusicians;
  
  Ensemble(int size){
    this.numberOfMusicians = size;
  }
  
  public int numberOfMusicians() {
    return numberOfMusicians;
  }
}
```

  

Enum의 API문서를 보면 ordinal에 대해 이렇게 쓰여있다고 한다.  

> "대부분의 프로그래머는 이 메서드를 사용할 일이 없다.  
>
> 이 메서드(ordinal())는 EnumSet과 EnumMap 처럼 enum(열거타입) 기반의 범용 자료구조에 쓸 목적으로 설계되었다." 

  

이 말인 즉, EnumSet, EnumMap같은 컬렉션 내에서 내부적으로 사용할 목적으로 설계되었다는 이야기이다.  

  

따라서, 이런 용도가 아니라면 ordinal 메서드는 절대 사용하지 말자. 라고 책에서는 다시 한번 강조하면서 마무리를 짓고 있다.  

