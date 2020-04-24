# 스프링에서 의존성 주입하는 여러가지 방법들

# 정리 예정.... ㅠㅜ



- [https://zorba91.tistory.com/238](https://zorba91.tistory.com/238)
- [https://preamtree.tistory.com/166](https://preamtree.tistory.com/166)
- [https://yaboong.github.io/spring/2019/08/29/why-field-injection-is-bad/](https://yaboong.github.io/spring/2019/08/29/why-field-injection-is-bad/)
- [https://smallgiant.tistory.com/m/78?category=595945](https://smallgiant.tistory.com/m/78?category=595945)



위 세가지의 링크들에서 제공하는 영어 원문 링크의 내용을 읽어볼 예정이다.  

기본적으로 스프링에서 Injection을 하는 방식은

- **Field Injection**
  - @Autowired, @Inject 등을 사용
  - immutable이 되기 어렵다는 점 존재 
- **Constructor Injection**
  - 멤버 필드에 final로 선언한 멤버필드 들 중
  - 생성자의 인자에 필요한 타입의 파라미터 를 선언
  - 이렇게 하면 Spring에서 주입을 해준다.
  - 테스트 등 여러가지 부분에서 연동하기 편리한 방식이지만, 생성자가 복잡해질 수 있다는 단점 존재.
  - 생성자가 복잡해지는 문제는 Lombok의 @RequiredArgsConstructor 로 해결 가능하다.
- **Setter Injection**

