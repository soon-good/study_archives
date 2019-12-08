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

