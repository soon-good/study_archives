# 아이템 16 - public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라

public 클래스는 절대 가변 필드를 노출해서는 안된다. 불변 필드라면 노출해도 덜 위험하지만 안심할 수 없다.  
package-private 클래스 또는 private 중첩 클래스 에서는 종종(불변이든, 가변이든) 필드를 노출하는 편이 나을 때도 있다.  

```java
class Point{
    public double x;
    public double y;
}
```

와 같이 멤버필드를 private가 아닌 public으로 놓는 경우에 대한 지적이다. 데이터 필드에 직접 접근할 수 있는 것은 캡슐화의 이점을 해친다고 한다.

- API를 수정하지 않고는 내부 표현을 바꿀 수 없고
- 불변식을 보장하지 못하며
- 외부에서 필드에 접근할 때 부수작업을 수행할 수 없다

는 이런 저런 잔소리가 있고  

  

학교에서 배워오기도 했고, Java 책을 처음부터 끝까지 읽은 경험이 있다면

- 멤버 필드 모두를 private으로 지정
- public getter 를 추가

는 당연히 알고 있다. 이와 같은 방식으로 해결하면 된다.  

이 방식을 사용하면 클래스 내부 표현식을 언제든 바꿀 수 있는 유연성을 얻을수 있는 장점이 있다고 이야기 하고 있다.  



## package-private / private

하지만, package-private or private 중첩 클래스에 대해서는 데이터 멤버 필드를 노출해도 하등의 문제가 없다고 한다. 그 클래스가 표현하려는 추상 개념만 올바르게 표현해주면 된다고 한다.  
이 방식이 getter를 사용하는 방식보다 깔끔하다. 라고 추천은 하고 있기는 하다.  

- 클라이언트 코드가 이 클래스 내부 표현이 묶이는 단점이 존재하지만  
  클라이언트도 어차피 이 클래스를 포함하는 패키지 안에서만 동작하는 코드일 뿐이다. 따라서  바깥 코드를 전혀 손대지 않고 데이터 표현방식을 바꿀 수 있다고 함.
- private 중첩 클래스의 경우  
  수정범위가 더 좁아져서 이 클래스를 포함하는 외부 클래스로 제한된다.  

자바 플랫폼 라이브러리에도 이 규칙(public 클래스의 필드를 노출하지 말라)를 어기는 사례가 자주 나타난다.  

예) java.awt.package 의 Point, Dimension

  

public 클래스의 필드가 불변이라면 직접 노출할 때의 단점이 조금은 줄어들지만 좋은 아이디어가 아니라고 한다.  

그 이유는

- API를 변경하지 않고는 표현방식을 바꿀 수 없다.
- 필드를 읽을 때 부수작업을 수행할 수 없다.
- 하지만 불변식은 보장가능하다.

  

ex) public 필드이지만 불변성을 부여한 코드의 예를 든다.  

```java
public final class Time{
    private static final int HOURS_PER_DAY = 24;
    private static final int MINUTES_PER_HOUR = 60;

    public final int hour;
    public final int minute;
    public Time (int hour, int minute){
        if(hour <0 || hour >= HOURS_PER_DAY)
            throw new IllegalArgumentException(“시간 : ” + hour);
        if(minute<0 || minute >= MINUTES_PER_HOUR)
            throw new IllegalArgumentException(“분 : “ + minute);
        this.hour = hour;
        this.minute = minute;
    }
}
```













