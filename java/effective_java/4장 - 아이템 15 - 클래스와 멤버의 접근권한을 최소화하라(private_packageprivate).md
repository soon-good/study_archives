# 아이템 15 - 클래스와 멤버의 접근권한을 최소화하라

일반 클래 스와 마찬가지로, 그 기능을 클라이언트에 노출해야 할 합당한 이유가 없다면 private으로，혹은 (필요하다면) package-private으로 선언하라  

> (가장 바깥이라는 의미의) 톱레벨 클래스와 인터페이스에 부여할 수 있는 접근 수준은 package-private과 public 두 가지다. 톱레벨 클래스나 인터페이스 를 public으로 선언하면 공개 API가 되며，package-private으로 선언하면 해당 패키지 안에서만 이용할 수 있다. 패키지 외부에서 쓸 이유가 없다면 package- private 으로 선언하자.  
>
>
> 그러면 이들은 API가 아닌 내부 구현이 되어 언제든 수정할 수 있다. 즉, 클라이언트에 아무런 피해 없이 다음 릴리스에서 수정, 교체， 제거할 수 있다. 반면, public으로 선언한다면 API가 되므로 하위 호환을 위해 영원히 관리해줘야만 한다.  
>
> 톱 레벨로 두면 같은 패키지의 모든 클래스가 접근할 수 있지만，private static으 로 중첩시키면 바깥 클래스 하나에서만 접근할 수 있다.  
>
> 한편, 이보다 훨씬 중 요한 일이 있다. 바로 public일 필요가 없는 클래스의 접근 수준을 package- private 톱레벨 클래스로 좁히는 일이다. public 클래스는 그 패키지의 API인 반면，package-private 톱레벨 클래스는 내부 구현에 속하기 때문이다.  
> 멤버(필드，메서드，중첩 클래스，중첩 인터페 이스)에 부여할 수 있는 접근 수 준은 네 가지다. 접근 범위가 좁은 것부터 순서대로 살펴보자.



## 접근권한자들의 종류

- private  
  멤버를 선언한 톱레벨 클래스에서만 접근할 수 있다.  
- package-private  
  멤버가 소속된 패키지 안의 모든 클래스에서 접근할 수 있다. 접근 제한자를 명시하지 않았을 때 적용되는 패키지 접근 수준이다(단， 인터페이스의 멤버는 기본적으로 public이 적용된다).  
- protected  
  package-private의 접근 범위를 포함하며, 이 멤버를 선언한 클래 스의 하위 클래스에서도 접근할 수 있다(제약이 조금 따른다[JLS，6.6.2]).  
- public  
  모든 곳에서 접근할 수 있다.

private과 package-private 멤버는 모두 해 당 클래스의 구현에 해당하므로 보통은 공개 API에 영향을 주지 않는다.   

단, Serializable을 구현한 클래스에서는 그 필드들도 의도치 않게 공개 API가 될 수도 있다(아이템 86, 87).

public 클래스에서는 멤버의 접근 수준을 package-private에서 protected로 바꾸는 순간 그 멤버 에 접근할 수 있는 대상 범위가 엄청나게 넓어진다. public 클래스의 protected 멤버는 공개 API이므로 영원히 지원돼야 한다.  



# package private?

effective java의 6장 item 34-int 상수대신 열거 타입을 사용하라에서는 아래와 같이 말하고 있다.  

> 열거 타입을 선언한 클래스 혹은 그 패키지에서만 유용한 기능은 private이나 package-private 메서드로 구현한다. 이렇게 구현된 열거타입상수는 자신을 선언한 클래스 또는 패키지에서만 사용할 수 있는 기능을 담게 된다.

이렇게 말하고 있다. 일반 클래스와 마찬가지로 그 기능을 클라이언트로 노출할 이유가 없다면 private로 또는 필요하다면 package private로 선언하라

> package-private 란?  
>
> 결론부터 말하자면 package-private는 default 접근자와 같은 말이다. paraphrasing 한 단어인 것으로 보인다. 즉 package-private == default  
>
> 참고자료  
>
> - [hashcode - 자바의 접근제어자 default와 package-private는 같은 말인가요?](https://hashcode.co.kr/questions/6389/%EC%9E%90%EB%B0%94%EC%9D%98-%EC%A0%91%EA%B7%BC%EC%A0%9C%EC%96%B4%EC%9E%90-default%EC%99%80-package-private%EB%8A%94-%EA%B0%99%EC%9D%80%EB%A7%90%EC%9D%B8%EA%B0%80%EC%9A%94)  
>
> Q>  
> 책으로 자바를 공부하면서 더 알고 싶은 부분은 검색으로 공부하고 있습니다. 책에서는 접근제어자가 public protected package-private private 가 있다고 나오고 점프 투 자바에서는 package-private대신 default가 그 자리에 있는데 둘의 차이를 알고싶습니다  
>
> A>  
>
> 표현의 차이지 같습니다.
>
> 별다른 키워드 없이 접근자를 지정하여 default 접근자 라고 표현한 것이고, 패키지에서만 접근할 수 있어서 package-private 접근자라고 표현한 것일 뿐입니다.  
>
> [oracle - Access to Package-Access Fields, Methods, and Constructors](https://docs.oracle.com/javase/specs/jls/se10/html/jls-6.html#d5e10050)  
>
> 을 보시면 package-access라고 표현하고 있습니다. 저는 패키지 접근자란 말을 쓰고 있습니다. 

