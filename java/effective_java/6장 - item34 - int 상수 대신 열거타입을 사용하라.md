# int 상수 대신 열거 타입을 사용하라

책이 굉장히 산문적이라, 책을 정리하는 데에 애를 좀 먹었다. 부수적으로 따라오는 다른 개념들도 있으니 그것들 역시 정리하면서 링크로 남겨볼까 한다.  



## 핵심정리

> 열거 타입은 확실히 정수 상수보다 뛰어나다. 더 읽기 쉽고 안전하고 강력하다. 대다수 열거 타입이 명시적 생성자나 메서드 없이 쓰이지만, 각 상수를 특정 데이터와 연결 짓거나 상수마다 다르게 동작하게 할 때는 필요하다.



## 용어의 혼선 대치

- 열거 타입 -> enum  
  열거 타입이라는 말로 이야기하기엔 너무 알아듣기 힘들다. 이런 이유로 그냥 enum이라고 통칭해 정리하려 한다. 중간에 혼용해 사용하여 정리할수 있다. 그럴 경우 enum으로 퉁치자
- 

## overview

여기서 언급하는 주제는 네가지이다. (더 있는지 찾아보자 ㅠㅜ)  

- final 키워드  
  enum(열거타입)은 근본적으로 불변이라 모든 필드는 final이어야 한다.  
  (아이템 17 - 변경 가능성을 최소화하라)  
  
- package-private  
  enum(열거 타입)을 선언한 클래스 혹은 그 패키지 안에서만 유용한 기능은 private 또는 package private로 구현한다. 이렇게 구현된 enum (열거타입) 상수는 자신을 선언한 클래스 또는 패키지에서만 사용할 수 있는 기능을 담게 된다.   
  이 부분에 대해 이렇게 말하고 있다. "일반 클래스와 마찬가지로 그 기능을 클라이언트에 노출해야할 합당한 이유가 없다면 private로, 혹은 package-private로 선언하라.  
  (아이템 15 - 클래스와 멤버의 접근 권한을 최소화하라)  
  
- 아이템 16 - public 클래스에서는 public 필드 대신 멤버필드를 private로 만든후 접근자 메서드를 사용하라   
  : enum(열거 타입) 생성 예제에서 생성자를 통해 멤버 필드에 값을 저장하는 부분이 자주 등장한다. 이 부분을 두고 필드를 private로 선언하라고 잔소리한다. (어찌보면 너무 당연한 이야기다.)  

- 널리 쓰이는 열거 타입은 톱레벨 클래스로 만들고  

  특정 톱 레벨 클래스에서만 쓰인다면, 해당 클래스의 멤버 클래스로 만든다.  
  (아이템 24 - 멤버 클래스는 되도록 static으로 만들라)  

- fromString  
  enum에서 유용하게 쓰이는 valueOf 외에도 fromString이라는 함수로 String값을 받아 Enum을 받아오는 경우를 다루고 있다.  
  이 부분에 대한 내용 정리는 생략한다. 그냥 자바 8의 Optional, 등등에 대해 설명하고 있는데, 이야기에 알맹이가 너무 없어서 건너뛴다.  

- enum내에서 메서드를 상수별로 다른 동작하도록 상수별 메서드(abstract 활용)를 구현  
  하나의 메서드가 상수별로 다르게 동작해야 하는 경우가 있다. 이 경우 switch~case문 대신 abstract 메서드를 선언해 상수별로 구현하는 다형성을 이용한다. (switch~case문의 단점 또한 설명한다.)  

- strategy enum (전략 열거타입)  
  enum 내의 상수의 일부가 같은 동작을 공유한다면 strategy enum (전략 열거타입) 패턴을 사용하자.



# enum의 장점, 편리함

enum 은

- 임의의 메서드나 필드를 추가할 수 있다.  
- 임의의 인터페이스를 구현하게 할 수도 있다.  
- Object 메서드들(3장)을 높은 품질로 구현해놓은 문법적 도구다.  
- Comparable(아이템 14)과 Serialiazable(12장)을 구현했으며, 그 직렬화 형태 또한 웬만큼 변형을 가해도 문제없이 동작하도록 구현되어 있다.
- toString  
  toString을 재정의해 사용할 경우 편리함을 제공한다.

toString을 재정의해 사용하는 예제

```java
public enum Operation{
  PLUS(“+”) {
    public double apply(double x, double y){
      return x + y;
    }
  },
  MINUS(“-“) {
    public double apply(double x, double y){
      return x -y;
    }
  };
  
  private final String symbol;

  Operation(String symbol) {this.symbol = symbol;}

    @Override
    public String toString() {
        return symbol;
    }

    public abstract double apply(double x, double y);
}


public static void main(String [] args){
    double x = Double.parseDouble(args[0]);
    double y = Double.parseDouble(args[1]);
    for(Operation op : Operation.values())
        System.out.println(“%f %s %f = %f%n”, 
            x, op,y, op.apply(x,y));
}
```



# 아주 간단한 enum

거대한 열거타입(enum)을 만드는 일은 그리 어렵지 않다. enum 상수 각각을 특정 데이터와 연결 지으려면 생성자에서 데이터를 받아 인스턴스 필드에 저장하면 된다.  

책의 저자가 enum 생성시 주의할 점으로 잔소리를 하고 있는데 그 내용은 아래와 같다.  

- enum은 근본적으로 불변이라 모든 필드는 final이어야 한다.  
  (**아이템 17** - 변경가능성을 최소화하라)  
- 필드를 public으로 선언해도 되지만, private으로 선언하고, 별도의 public 접근자 메서드를 두는 것이 낫다.  
  (**아이템 16** - public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라)  



예제

```java
public class WeightTable{
  public static void main(String [] args){
    double earthWeight = Double.parseDouble(args[0]);
    double mass = earthWeight / Planet.EARTH.surfaceGravity();
    
    // enum의 values로 Planet enum내의 모든 상수들을 순회하고 있다.
    for ( Planet p : Planet.values() ){
      System.out.println(“%s에서의 무게는 %f이다. %n”,
                         p, p.surfaceWeight(mass));
    }
  }
}
```

  

# TIP 1) 널리 쓰이는 열거타입은 톱레벨 클래스로 만들고, 특정 톱레벨 클래스에서만 쓰인다면 멤버 클래스로 만들라.

이 부분은 예를 들어 설명하고 있다. 

톱레벨 클래스로 만든 예  

>  자바의 소수 자릿수 반올림 모드를 뜻하는 Enum인 java.math.RoundingMode는 BigDecimal에서 사용하고 있다.  
>
> 그런데 반올림 모드는 BigDecimal 외에도 여러 곳에서 사용가능한 유용한 개념이라 자바 라이브러리 설계자는 RoundingMode enum 을 톱 레벨로 올렸다.  
>
> 이 개념을 많은 곳에서 사용하여 다양한 API가 더 일관된 모습을 갖출수 있도록 장려한 것이다.



멤버 클래스로 만든 예  

> 아이템 24 - 멤버 클래스는 되도록 static으로 만들라 를 참고하자.



# TIP 2) enum의 상수 종류에 따라 다른 연산을 수행하도록 하자.

가장 간단하게 enum내에서 상수에 따라 다른 동작을 하는 함수를 만들고자 할때 

- switch case문을 통해 상수의 종류에 따른 구문을 작성할 수도 있다.
- 하지만 이 역시 abstract를 사용해 다형성을 구현할 수 있다.
- 하지만 또 역시 strategy enum 으로 객체를 전달받아 행위를 대리역할을 할수도 있다.

  

## switch ~ case문을 사용할 경우

예제)

```java
public enum Operation{
  PLUS, MINUS, TIMES, DIVIDE;
  
  public double apply (double x, double y){
    switch(this){
      case PLUS: 		return x+y;
      case MINUS:		return x-y;
      case TIMES:		return x*y;
      case DIVIDE:	return x/y;
    }
    throw new AssertionError("알수 없는 연산 : " + this);
  }
}
```



이 경우 단점은 아래와 같다.

> 새로운 상수를 추가할 때마다 해당 case문도 추가해야 한다. 혹시라도 깜빡한다면 컴파일은 되지만 새로 추가한 연산을 수행하려 할 때 "알수 없는 연산"이라는 런타임 에러를 내며 프로그램이 종료 된다.



책에서는 이 방법에 대한 해결책으로 abstract 메서드를 선언해 상수 각각에서 구현하는 것을 예로 든다. 아마도 Runtime에서 에러를 내는 것보다는 컴파일 타임에 override가 안되었다면서 Compile 타임에 에러를 나서 implements하라는 문구를 출력하면 배포전에 미연에 에러를 방지하는 것이 안전하기 때문일 듯하다.



## abstract 메서드를 통해 상수별 다형성 메서드 구현

switch ~ case 문을 사용할 경우 새로운 상수가 추가되었는데 apply함수내에 해당 처리 로직을 작성하지 않았을 경우 Runtime에서 Exception을 내도록 했다. 이 경우의 단점은 해당 Application을 배포한 후에 버그를 잡을수 있다는 점일 것 같다.  



이러한 경우를 미연에 방지할 수 있는 방법이 있는데, enum내에 abstract 메서드를 선언해두고 각각의 enum내에서 overriding하는 것이다. 이렇게 하면 추가된 상수에 대한 구현(override)가 안되면 컴파일 타임에 에러가 나기 때문에 미연에 방지할 수 있는 것 같다.  

  

책에서는 따로 장점이 열거되어 있지는 않다. 단지 조금 어려운 표현식이 되었다고 이야기하고 있다. 내가 생각한 장점이 맞는지는 나도 잘 모른다. 아무튼 그런것 같다. ㅋㅋ  

예제)

```java
public enum Operation{
  PLUS{
    public double apply(double x, double y){
      return x+y;
    }
  },
  MINUS{
    public double apply(double x, double y){
      return x-y;
    }
  },
  DIVIDE{
    public double apply(double x, double y){
      return x/y;
    }
  };
  
  public abstract double apply(double x, double y);
  
}
```



## Strategy Enum 패턴(전략 열거 타입 패턴)

여기서는 관리 관점에서 위험한 코드의 예를 들고 이러한 경우에 Strategy Enum 패턴을 사용하는 것을 추천하고 있다.  

  

먼저 위험한 코드의 예를 들어본다.

예) 급여 정산 코드

휴가와 같은 새로운 값을 enum 내에 추가하려면 그 값을 처리하는 case문을 잊지 말고 쌍으로 넣어주어야 한다. 자칫 깜빡하는 날에는 휴가기간에 열심히 일해도 평일과 같은 임금을 받게 된다. (미국식 조크인듯 하다.)

```java
enum PayrollDay{
  MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY,
  SATUERDAY, SUNDAY;
  
  private static final int MINS_PER_SHIFT = 8 * 60;
  
  int pay(int minutesWorked, int payRate){
    int basePay - minutesWorked * payRate;
    int overtimePay;

    switch(this){
      case SATURDAY: 
      case SUNDAY:
        overtimePay = basePay/2;
        break;
      default:
        overtimePay = 
          minutesWorked <= MINS_PER_SHIFT ? 
          0 : (minutesWorked - MINS_PER_SHIFT) * payRate /2;

    }
        
    return basePay + overtimePay;
  }
}
```

  

이와 같은 문제(case 문을 작성하지 않았을 경우 에러는 아니지만 논리적인 에러가 나는 상황)에 대해 strategy enum(전략 열거 타입) 패턴에 대한 사용예를 설명한다.

  

위 문제를 해결하기 위해 가장 깔끔한 방법은 잔업수당 "전략(Strategy)"을 선택하도록 하는 것이다. switch 문이나 상수별 메서드 구현이 필요 없게 된다.

1. 잔업수당 계산을 private 중첩 열거 타입(PayType)으로 옮긴다.
2. PayrollDay 열거 타입의 생성자에서 이중 적당한 것을 선택한다.
3. PayrollDay 열거 타입은 잔업 수당 계산을 그 전략 열거 타입에 위임하여 switch~case의 상수별 메서드 구현이 필요없게 된다.

  

ex) Strategy Enum을 사용한 예

```java
enum PayrollDay{
  // 생성자 안에 PayType의 상수를 대입하고 있다.
  MONDAY(WEEKDAY), TUESDAY(WEEKDAY), WEDNESDAY(WEEKDAY),
  THURSDAY(WEEKDAY), FRIDAY(WEEKDAY),
  SATURDAY(WEEKEND), SUNDAY(WEEKEND);

  // strategy
  private final PayType payType;

  // 여기서 strategy로 묶어주게(binding) 된다.
  PayrollDay(PayType payType) { 
    this.payType = payType; 
  }

  int pay(int minutesWorked, int payRate){
    return payType.pay(minutesWorked, payRate);
  }

  enum PayType{
    WEEKDAY{
      int overtimePay(int minsWorked, int payRate){
        return minsWorked <= MINS_PER_SHIFT ? 0 : (minsWorked - MINS_PER_SHFIT) *payRate /2;
      }
    },
    WEEKEND {
      int overtimePay(int minsWorked, int payRate){
        return minsWorked * payRate / 2;
      }
    };

    abstract int overtimePay(int mins, int payRate);
    private static final int MINS_PER_SHIFT = 8 * 60;

    int pay(int minsWorked, int payRate){
      int basePay = minsWorked * payRate;
      return basePay + overtimePay(minsWorked, payRate);
    }
  }
}

```



이렇게 Strategy Enum (전략 열거 타입)패턴으로 다른 동작을 하도록 하는 좋은 방법도 있지만, 굳이 이렇게 안해도 간단한 연산을 처리하는 경우도 있다. 이런 경우는 switch ~ case를 쓸수도 있다고 마지막에 변명같은 코드를 남기고 있다. 누군가 '너무 어렵게 쓰는거 아냐?'같은 반문을 제기할 경우, 용도에 맞게 쓰면 된다는 이야기 인듯 하다.  

ex) 반대 연산을 얻어 내는 경우  

이 경우 굳이 어렵게 쓴 코드를 써서 일을 열심히 하는척 했다는 오해를 살 필요가 없다.  

아래처럼 간단한 코드로 가능하다.  

```java
public static Operation inverse(Operation op){
    switch(op){
        case PLUS:         return Operation.MINUS;
        case MINUS:        return Operation.PLUS;
        case TIMES:        return Operation.DIVIDE;
        case DIVIDE:       return Operation.TIMES;

        default: throw new AssertionError(“알 수 없는 연산 : “ + op);
    }
}
```

추가하려는 메서드가

- 의미상 열거타입에 속하지 않는다면 직접 만든 enum (열거 타입)이라도 이 방식을 적용하는 것이 좋다.
- 종종 쓰이지만 enum(열거 타입)안에 포함할 만큼 유용하지 않은 경우도 마찬가지다.



# toString의 장점

Operation이란 enum의 toString을 재정의해 해당 연산을 뜻하는 기호를 반환하도록 하는 경우의 예제를 들어본다. 아래의 예제는 toString이 계산식 출력을 얼마나 편하게 해주는지 보여주고 있다.  

ex) 상수별 클래스 몸체(class body)와 데이터를 사용한 enum  

Operation의 toString을 재정의해 해당 연산을 뜻하는 기호를 반환한다.  

```java
public enum Operation{
    PLUS(“+”) {
        public double apply(double x, double y){
            return x + y;
        }
    },
    MINUS(“-“) {
        public double apply(double x, double y){
            return x -y;
        }
    }
        …
    private final String symbol;
	  // 주목
    Operation(String symbol) {this.symbol = symbol;}

  	// 주목
    @Override
    public String toString() {
        return symbol;
    }

    public abstract double apply(double x, double y);
}


public static void main(String [] args){
    double x = Double.parseDouble(args[0]);
    double y = Double.parseDouble(args[1]);
    for(Operation op : Operation.values())
        System.out.println(“%f %s %f = %f%n”, 
            // 여기를 주목
            x, op,y, op.apply(x,y));
}
```



# fromString (지랄이다... ㅠㅜ)

valueOf 처럼 String을 주는 것 말고도, fromString이라는 함수로 String값을 받아 Enum을 받아오는 경우를 다루고 있다. 이부분에 대한 설명은 생략한다. 그냥 자바8과 Optional, 등등에 대해 설명하고 있는데, 이야기에 알맹이가 너무 없어서 건너뛴다.  



# 열거타입은 언제쓰란 말인가요?

- 필요한 원소를 컴파일 타임에 다 알수 있는 상수집합이라면 항상 열거타입을 사용하자.  

- 태양계행성, 한주의 요일, 체스 말 처럼 본질적으로 열거타입인 타입은 당연히 포함된다.  

- 메뉴아이템, 연산코드, 명령줄 플래그 등 허용하는 값 모두를 컴파일타임에 이미 알고 있을 때도 쓸수 있다.  

- 열거 타입에 정의된 상수 개수가 영원히 고정불변일 필요가 없다.  

- 열거타입은 나중에 상수가 추가돼도 바이너리 수준에서 호환되도록 설계되었다.  