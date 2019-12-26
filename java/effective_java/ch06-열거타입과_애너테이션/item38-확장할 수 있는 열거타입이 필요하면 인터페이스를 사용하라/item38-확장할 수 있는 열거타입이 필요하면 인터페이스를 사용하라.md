# 아이템 38 - 확장할 수 있는 열거타입이 필요하면 인터페이스를 사용하라

## 요약

enum이 도입되기 전(java 1.5 이전)에는 enum이라는 키워드가 없었다. 이런 이유로 책의 초판에서 typesafe enum pattern을 사용하라는 이야기를 했던 듯 하다.  Typesafe enum pattern은 enum의 모양을 Class로 직접 구현하는 것이다([참고자료1](https://danguria.tistory.com/57), [참고자료2](http://www.javapractices.com/topic/TopicAction.do?Id=1)).  

typesafe enum pattern은 class로 enum을 구현했기 때문에 상속(확장)이 가능하다. 이 책에서 이야기하는 '확장할수 있는'은 'extendable' 이다. 보통 정식 교과로 공부할때 보통 객체지향언어의 '상속'을 '확장'이라는 단어로도 표현가능하다고 배우지 않았다. 여기서 확장 가능한으로 번역되어 있으니 조금 어색한 면도 있다. '상속'이라는 단어 자체가 애초에 조금 이상하다는 생각도 들긴한다.    

enum은 클래스처럼 상속이 불가능한 대신 interface 를 implements할 수 있다. 이 장에서는 결국

> enum을 사용할 때 그 기능을 확장(상속)하고 싶다면
>
> 1. interface를 사용하세요
> 2. Class 객체 대신 한정적 와일드카드 타입(아이템 31)인  
>    Collection\<? Extends Operation\>  
>    을 넘기는 방식 을 사용해보세요.

라는 이야기를 하고 있다.



## 핵심정리

열거타입 자체는 확장할 수 없지만(상속할 수 없지만), 인터페이스와 그 인터페이스를 구현하는 기본 열거타입을 함께 사용해 같은 효과를 낼 수 있다. 이렇게 하면 클라이언트는 이 인터페이스를 구현해 자신만의 열거타입(또는 다른 타입)을 만들 수 있다.  

API가 (기본 열거타입을 직접 명시하지 않고) 인터페이스 기반으로 작성되었다면 기본 열거타입의 인스턴스가 쓰이는 모든 곳을 새로 확장한 열거타입의 인스턴스로 대체해 사용할 수 있다.  



# 확장할 수 있는 열거 타입

## enum 확장의 단점

대부분의 상황에서 enum(열거 타입)을 확장(=상속)하는 것은 좋지 않은 생각이다. 

1. 확장한 타입의 원소는 기반 타입의 원소로 취급하지만 그 반대는 성립하지 않는다. 이것은 이상해보인다.
2. 기반 타입과 확장된 타입들의 원소 모두를 순회할 방법도 마땅치 않다.
3. 확장성을 높이려면 고려할 요소가 늘어나 설계와 구현이 더 복잡해진다.  

  

## 1. interface를 이용한 확장가능한 enum(열거타입)

enum의 확장을 되도록이면 피하라고 이야기 하고 있지만, 필요한 경우에 대한 예를 들고 있다. 여기서는 연산 코드(operation code)를 예로 들고 있다. 

ex) interface Operation  

```java
public interface Operation{
  double apply(double x, double y);
}
```

  

ex) Operation을 implements한 BasicOperation

```java
public enum BasicOperation implements Operation{
  PLUS("+"){
    public double apply(double x, double y){
      return x+y;
    }
  },
  MINUS("-"){
    public double apply(double x, double y){
      return x-y;
    }
  },
  TIMES("*"){
    public double apply(double x, double y){
      return x*y;
    }
  },
  DIVIDE("/"){
    public double apply(double x, double y){
      return x/y;
    }
  };
  
  private final String symbol;
  
  BasicOperation(String symbol){
    this.symbol = symbol
  }
  
  @Override
  public String toString(){
    return symbol;
  }
}
```

  

ex) Operation을 확장한 또 다른 enum(열거타입)  

지수연산(EXP), 나머지 연산(REMAINDER)을 추가했다.  

```java
public enum ExtendedOperation implements Operation{
  EXP("^"){
    public double apply (double x, double y){
      return Math.pow(x, y);
    }
  },
  REMAINDER("%"){
    public double apply(double x, double y){
      return x % y;
    }
  };
  
  private final String symbol;
  
  ExtendedOperation(String symbol){
    this.symbol = symbol;
  }
  
  @Override
  publid STring toString(){
    return symbol;
  }
}
```

  

개별 인스턴스 수준에서 살펴보면, 새로 추가한 연산인 ExtendedOperation은 Operation 인터페이스를 사용하도록 되어 있다면 어디든 사용 가능하다. apply가 인터페이스(Operation)에 선언되어 있으므로 enum내부에 따로 추상메서드를 선언하지 않아도 된다.  

  

개별 인스턴스 수준 뿐만 아니라 타입 수준에서 살펴보면, 기본 enum 대신 확장된 enum을 넘겨 확장된 열거타입의 원소 모두를 사용하게 할 수도 있다.   

  

ex) ExtendedOperation의 모든 원소를 테스트하는 예  

class 리터럴을 넘겨 확장된 연산이 무엇인지 알려주고 있다. 여기서 class 리터럴은 한정적 타입토큰(아이템 33)역할을 한다.  

```java
public static void main(String [] args){
  double x = Double.parseDouble(args[0]);
  double y = Double.parseDouble(args[1]);
	// ExtendedOperation의 class 리터럴을 넘겨 
  // 확장된 연산들(% - REMAINDER, ^ - EXP)이 
  // 무엇인지 알려준다.
  test(ExtendedOperation.class, x, y); 
}

private static <T extends Enum<T> & Operation> void test(
  Class<T> opEnumType, double x, double y){
  for(Operation op : opEnumType.getEnumConstants()){
    System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x,y));
  }
}
```

매개변수 opEnumType의 선언형태는 복잡하다. 의미를 살펴보면,  

- "Class객체가 열거타입인 동시에 Operation의 하위 타입이어야 한다"는 의미이다.  
  \<T extends Enum\<T\> & Operation\> Class \<T\> 

열거 타입이어야 원소를 순회할 수 있고, Operation이어야 원소가 의미하는 연산을 수행할 수있기 때문이다.   



## 2. Class 객체 대신 환정적 와일드 카드 타입(아이템 31)을 넘기는 방식

(255p)