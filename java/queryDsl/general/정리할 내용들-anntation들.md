# 정리할 내용들 - annotation 들



# lombok - 롬복 라이브러리
## @NoArgsConstructor

매개변수가 없는 기본생성자를 롬복에서 만들어주는 고마운 애노테이션. 

## @RequiredArgsCosntructor

의존성 주입을 생성자 기반으로 할 때 사용하는 롬복 애노테이션.  

생성자 기반의 의존성 주입은 롬복 없이도 할 수 있다. 단지 의존성 주입이 필요한 인자를 모두 생성자의 파라미터로 넘겨주도록 일일이 코딩해야 한다.  

@RequiredArgsConstructor는 이런 단순 노가다 작업을 단순화하기 위해 존재하는 롬복의 어노테이션이다.  

## @Transactional

테스트 코드에서 메서드 또는 클래스 위에 @Transactional을 추가하면 해당 클래스/메서드의 실행이 종료된 후에 Transaction을 롤백한다.  

