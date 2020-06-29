# Spring Transactional



# 1. 참고자료

@Transactional 을 사용하는 이유 에 대해 자세하게 설명하는 곳

- [crosstheline.tistory.com](https://crosstheline.tistory.com/96)

스프링 설정에 대해 간결하게 설명하고 있는 자료

- [태태태](https://taetaetae.github.io/2017/01/08/transactional-setting-and-property/)



# 2. @Transactional 을 사용하는 경우

예를 들어 아래와 같은 코드가 있다고 해보자.

```java
@Service
public class SomeUserServiceImpl{
  
  // ...
  @Transactional
  public int updateUserData(int sal) throws Exception{
    // 전체 사원들의 기본급 업데이트
    int cnt = userRepository.updateSalary(sal);
    
    // 전체 사원들에 대해 bonus 를 200만 으로 세팅해준다.
    if(cnt > 0){
      userRepository.updateBonus(2000000);
    }
    return cnt;
  }
}
```

@Transactional 을 사용하면, updateUserData 를 실행할 때 updateSalary(int)가 정상적으로 수행되었지만, updateBonus(int)는 실패했을 경우 updateSalary(int)로 update했던 SQL 을 RollBack 처리해준다.  

만약 위의 예제에서 @Transactional을 사용하지 않으면, 이미 updateSalary(int)를 통해 모든 사원의 기본급 데이터가 업데이트 되었기 때문에 다시 DB를 복구해놓아야 한다.



## @Transactional 사용시 유의해야 하는 내용들

참고자료

- [Transactional 이란? 사용이유](https://crosstheline.tistory.com/96)

  

@Transactional을 사용하는 데에도 유의해야 하는 내용들이 있는데, 그 내용들을 정리해보면 아래와 같다.

- 인터페이스를 구현한 클래스로 선언된 빈은 인터페이스 메서드에 한해서 트랜잭션이 적용된다.
- 인터페이스에 붙인 @Transactional 은 인터페이스 내의 모든 메서드에 적용된다.
- 메서드 레벨에도 @Transactional을 지정할 수 있다. 
  - 메서드 선언 > 인터페이스 선언 순으로 우선순위가 결정된다.
  - 메서드 레벨의 선언이 인터페이스 레벨의 선언을 덮어쓰는 것으로 보인다.
- 클래스의 @Transactional > 인터페이스의 @Transactional
- @Transactional 적용 대상은 미리 결정하고 애플리케이션 안에서 통일하는 게 좋다. 인터페이스와 클래스 양쪽에 불규칙하게 @Transactional이 혼용되는 것은 바람직하지 못하다.



# 3. @Transactional 스프링 설정

참고자료

- https://taetaetae.github.io/2017/01/08/transactional-setting-and-property/



