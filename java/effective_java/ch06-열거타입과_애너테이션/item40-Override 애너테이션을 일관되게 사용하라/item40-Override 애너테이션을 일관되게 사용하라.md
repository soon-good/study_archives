# 아이템 40 - @Override 애너테이션을 일관되게 사용하라

이번 장에서는 @Override를 해야 하는 구문을 Overloading 하는 경우를 예로 들어서 설명하며, @Override를 할 경우 잘못된 수정을 할 경우 컴파일러에서 컴파일 에러를 내어, 런타임 환경에서 예기치 못한 오류가 발생하는 경우를 미연에 방지할 수 있다는 이야기를 하고 있다.  

메서드를 상속할 경우 재정의(overriding)하는 것이 아니라 다중정의(overloading)를 하는 경우가 있다. 메서드를 다중정의(overloading)을 하기 위한 목적이 아니라면 재정의(overriding)하기 위한 목적의 로직들에는 일관적으로 모두 @Override 애너테이션을 붙이는 것을 강조하고 있다.  

보통 IDE의 단축키(refactor>override methods... ) 등을 통해 override를 할 수 있으므로 그렇게 중요하게 생각하지 않을 수도 있다. 이런 ui 메뉴가 있기 때문에 override할 목적이었는데 실수로 overloading 하게 되는 경우를 방지할 수 있는 것은 맞다.  

하지만  

- 프로그램 내에서 논리적인 오류를 찾아내야 할때 @Override가 아닌 오버로드 구문인데, Overriding된 용도로 사용한 것과 같은 구문에 대한 오류를 찾아내는 경우
- 부주의 및 실수로 잘못 작성된 코드

에 대해 판단의 기준을 가질 수 있기 때문에 이번 item40을 무시하고 넘어가기만 할 수는 없다.  



# ex) Overriding을 잘못하여 Overloading 하는 예



예) 영어 단어 2개로 구성된 문자열 표현

```java
public class Bigram{
  private final char first;
  private final char second;
  
  public Bigram(char first, char second){
    this.first = first;
    this.second = second;
  }
  
  public boolean equals(Bigram b){
    return b.first = first && b.second == second;
  }
  
  public int hashCode(){
    return 31*first+second;
  }
  
  public static void main(String [] args){
    Set<Bigram> s = new HashSet<>();
    for(int i=0; i<10; i++)
      for(char ch='a'; ch<='z'; ch++)
        s.add(new Bigram(ch, ch));
    
    System.out.println(s.size());
  }
}
```



main 메서드 내에서 소문자 2개로 구성된 바이그램 26개를 10번 반복해 집합에 추가하고 그 집합의 크기를 출력하고 있다. Set은 중복을 허용하지 않으므로 26이 출력될 것 같지만, 실제로는 260이 출력된다.  

위 코드에서는 Object 클래스의 equals를 <u>재정의(overriding)한 것이 아니라 다중정의(overloading)하고 있기 때문</u>이다. Object의 equals()메서드는 매개변수 타입을 Object로 해야 하는데 그렇게 하지 않았기 때문이다.

재정의(overriding)가 목적이었다면, @Override를 붙이는 것으로 위 코드와 같은 논리적 오류를 방지할 수 있다. @Override를 붙이면 컴파일러가 프로그램 실행 전에 메소드 시그니처가 틀렸다는 에러를 내 줄 수 있기 때문에 아주 편리하다.  

예) @Override 붙이기  
익셉션이 컴파일 타임에 나므로, 프로그램 실행시의 논리적 오류를 미연에 방지한다.

```java
@Override public boolean equals(Bigram b){
  return b.first == first && b.second == second;
}
```

  

예) 잘못 사용된 오버라이딩 구문을 용도에 맞도록 수정  
```java
@Override public boolean equals(Object o){
  if(!(o instanceof Bigram)){
    return false;
  }
  Bigram b = (Bigram) o;
  return b.first == first && b.second == second;
}
```



대부분의 IDE에서 refactor 메뉴를 활용하면 @Override 애너테이션을 메서드 시그니처에 맞춰서 달아준다. 최근에는 프로그래머가 직접 수작업으로 @Override 애너테이션을 표기하지 않음으로써 오류를 내는 경우가 생길 수 있다. 이런 경우에 대한 대비책으로 코딩 컨벤션으로 @Overriding 애너테이션을 사용하도록 권장하는 것으로 보인다.   
(Overriding과 Overloading을 문법적으로 공부하고, 파악하려고 노력하거나, 시간 공수를 들이는 것보다 명시적으로 Overriding을 목적으로 하는 곳에는 일괄적으로. @Overriding 애너테이션을 달도록 코딩 컨벤션을 정하는게 더 효율적인것 같기도 하다.)  


