# item34 - int 상수 대신 열거 타입을 사용하라



## 정수 열거 패턴

```java
public static final int APPLE_FUJI		= 0;
public static final int APPLE_PIPPIN	= 1;
...
```



- 정수 열거 패턴을 사용한 프로그램은 깨지기 쉽다. 평범한 상수를 나열한 것 뿐이라 컴파일하면 그 값이 클라이언트 파일에 그대로 새겨진다.
  상수의 값이 바뀌면 클라이언트도 반드시 다시 컴파일해야 한다. 다시 컴파일하지 않ㅇ느 클라이언트는 실행이 되더라도 엉뚱하게 동작할 것이다.
- 정수 상수는 문자열로 출력하기가 다소 까다롭다.
- 정수 대신 문자열 상수를 사용하는 변형 패턴도 있다. 하지만 이 변형은 더 나쁘다. 상수의 의미를 출력할 수 있다는 점은 좋지만 경험이 부족한 프로그래머가 문자열 상수의 이름 대신 문자열 값을 그대로 하드 코딩하게 만들기 때문이다.



## 열거 타입(Enum)

자바는 열거 패턴의 단점을 말끔히 씻어주는 동시에 여러 장점을 안겨주는 대안을 제시했다. 바로 열거 타입(enum type)이다.  

간단한 예

```java
public enum Apple  {FUJI, PIPPIN, GRANNY_SMITH}
public enum Orange {NAVEL, TEMPLE, BLOOD}
```

  

enum type은 컴파일 타입 안정성을 제공한다. 



