# item2 - 생성자 매개변수가 많으면, 빌더를 고려하라 

## 참고자료  

- [Effective Java 제공 공식 예제](https://github.com/WegraLee/effective-java-3e-source-code/tree/master/src/effectivejava/chapter2/item2)
  - [builder](https://github.com/WegraLee/effective-java-3e-source-code/tree/master/src/effectivejava/chapter2/item2/builder)
  - [hireachial builder](https://github.com/WegraLee/effective-java-3e-source-code/tree/master/src/effectivejava/chapter2/item2/hierarchicalbuilder)
  - [javabeans](https://github.com/WegraLee/effective-java-3e-source-code/tree/master/src/effectivejava/chapter2/item2/javabeans)
  - [talescopingconstructor](https://github.com/WegraLee/effective-java-3e-source-code/tree/master/src/effectivejava/chapter2/item2/telescopingconstructor)
- [백기선님 Youtube Link](https://www.youtube.com/watch?v=OwkXMxCqWHM&list=PLfI752FpVCS8e5ACdi5dpwLdlVkn0QgJJ&index=2)
  - Effective Java 에서 공식으로 제공한 예제를 동영상 강의로 풀어주심
  - 덤으로 Effective Java 책을 요약해주심
  - Effective Java 책이 워낙 글이 산문형이라 정리하기 쉽지 않은데, 이런 요약자료는 너무 감사



# 0.생성자의 매개변수가 많으면 생기는 단점

- 읽기가 어렵다.  
- 어떤 역할을 하는 생성자인지 알기 쉽지 않다.
- 외부에서 사용할 때 어떤 생성자를 사용할지 판단하기 쉽지 않다.
- 생성자의 몇 번째 파라미터에 어떤 값을 넣을지 일일이 찾아보고 넘겨주어야 한다.
- 참고)  
  - C#, python, scala 에서는 Named Optional Parameter라는 문법적 개념을 제공해준다.



# 1. 가능한 해결방안들

## 1) 생성자의 매개변수를 최소한으로 구성한다.

> 생성자가 매개변수를 최소한으로 받아들이도록 구성한다.  
>
> 이런 생성자를 쓰다보면 필요없는 매개변수도 넘겨야 하는 경우가 발생하는데, 보통 0 같은 기본값을 넘긴다. 물론 이런 방법이 동작하긴 하지만, 이런 코드는 작성하기도 어렵고 읽기도 어렵다.

```java
public class NutritionFacts {
    private final int servingSize;
    private final int servings;
    private final int calories;
    private final int fat;
    private final int sodium;
    private final int carbohydrate;
		
  	public NutritionFacts(int servingSize, int servings, int calories){
      	this.servingSize = servingSize;
      	this.servings = servings;
      	this.calories = calories;
    }
}
```

  

내피셜 : 생성자의 매개변수를 필요한 만큼만 구성하는 경우는 그리 많지 않기 때문에 그리 좋은 방법은 아닌 듯 하다.  

## 2) 자바 빈

> 



