# Spring Data JPA 웹 확장 기능

# 1. 사용자 정의 리포지터리

QueryDsl, Mybatis, Jooq, jdbcTemplate 을 사용하려 하는데, DataRepository 내에서 해당 기능을 통으로 제공하고 싶을 때가 있다. 운영하면서 Spring Data JPA 에서 제공하는 기능보다는 약간 정교하고 세밀한 컨트롤이 필요한 쿼리를 사용해야만 되는 경우가 자주 있다. 이런 경우 DataSource를 따로 쓰는 경우도 있고, 여러가지 세부적인 특화된 설정을 가져다 쓰는 사용자 정의Repository를 가져다 쓴다.   

  

이때, 사용자 정의 리포지터리를 새로 하나 만들고, 이 사용자 정의 리포지터리를 기존 DataRepository 내에서 사용할 수 있도록 상속받아 기능을 확장할 수 있다. 여기서는 이러한 경우에 대해 다룬다. 

> 실제로, 사용자 정의 리포지터리를 기존 Data JPA Repository 내에 결합해서 사용하는 것은 사람마다 취향차이이겠지만, 여러가지 라이브러리가 짬뽕된 리포지터리가 만들어지는 관계로 인해 나의 경우는 그다지 선호하지 않는다. 복잡도도 올라가고, 결합도도 높아지기 때문이다. 어느 정도의 용도별 분리가 더 중요한 편이라고 생각하는 편이다.  

예제에서는  

- 사용자 정의 리포지터리 클래스/인터페이스를 생성한다.
  - QEmpRepository, QEmpRepositoryImpl 이라는 이름으로 생성한다.
    - 이름을 보면 알수 있겠지만, QueryDsl을 사용하는 경우를 가정했다. 
    - 여기서 QueryDsl을 설정하지는 않는다. 단순 가정일 뿐이다. (시간이 된다면 예제로 추가할 예정)
  - 단순 select 문을 추가
  - 여기서 QueryDsl 을 정리하지는 않는다. (시간이 된다면 예제로 추가해보자.)
- Spring Data JPA 기반의 리포지터리를 만든다. 
  - EmpCustomDataRepository 라는 이름으로 생성한다.
  - 이 리포지터리에서 QEmpRepository 의 select 구문을 호출할 수 있도록 QEmpRepository 인터페이스를 상속받는다.

자, 이제 예제를 살펴보자.

## 예제

### QEmpRepository.java

아직까지는, QueryDsl을 사용하는 경우를 흉내낸 단순 리포지터리이다. 나중에 볼때 이해가 쉬우려면 예제의 단순함이 훨씬 중요하다는 생각에 QueryDsl 로직을 배제했다.

```java
package io.study.erd_example.emp.repository.custom.rawjpa;

import io.study.erd_example.emp.entity.Employee;
import java.util.List;

public interface QEmpRepository {
	List<Employee> selectAllEmployees();
}
```



### QEmpRepositoryImpl.java

아직까지는, QueryDsl을 사용하는 경우를 흉내낸 단순 리포지터리이다. 나중에 볼때 이해가 쉬우려면 예제의 단순함이 훨씬 중요하다는 생각에 QueryDsl 로직을 배제했다.

```java
public class QEmpRepositoryImpl implements QEmpRepository {

	private EntityManager em;

	public QEmpRepositoryImpl(EntityManager em){
		this.em = em;
	}

	@Override
	public List<Employee> selectAllEmployees() {
		return em.createQuery("select e from Employee e")
			.getResultList();
	}
}
```



### EmpCustomDataRepository.java

이전까지 만들었던 Data JPA 예제를 지저분하게 만드는 것이 영 내키지 않아서, EmpCustomDataRepository 라는 이름의 interface를 새로 만들었다. 디렉터리의 위치는 아래와 같다. (아예 custom이라는 패키지를 새로 만들어 예제용으로 준비했다.)

![이미자](/Users/kyle.sgjung/workspace/sgjung/study_archives/java/JPA/DataJPA-Basic/Advanced/img/CUSTOM_JPA_1.png)

예제 코드는 아래와 같다.

```java
public interface EmpCustomDataRepository extends JpaRepository<Employee, Long>, QEmpRepository {

	@Query("select e from Employee e left join fetch e.dept")
	List<Employee> findAllFetchJoin();
  
	// ... 이전에 작성했던 예제들 ... 모두 중략 
  
}
```



일반적인 Data JPA Repository 는 JpaRepository 라는 이름의 interface를 상속받는 편이다. 그런데 사용자 정의 리포지터리의 기능도 이 Data JPA Repository 에서 사용할 수 있도록 하기 위해 QEmpRepository interface도 상속받았다.  

결과적으로는 

- JpaRepository\<Employee, Long\>
- QEmpRepository

를 상속받았다. UML 로 정리해보려 했는데, 직접 작성해본 결과, UML을 아무리 보는 것보다는 실제로 코드를 쳐봐야 UML이 이해가 된다. 이런 이유로 UML은 생략~ (절대 그리기 싫어서가 아니다. ㅋㅋ ㅠㅜ)



### EmpCustomDataRepositoryTest.java

```java
/**
 * 사용자 정의 리포지터리 테스트
 */
@SpringBootTest
@Transactional
public class EmpCustomDataRepositoryTest {

	@Autowired
	DeptDataRepository deptDataRepository;

	@Autowired
	EmpCustomDataRepository qEmpRepository;

	@Autowired
	EntityManager em;

	@BeforeEach
	void insertData(){
		Department police = new Department("POLICE");
		Department firefighter = new Department("FIREFIGHTER");

		deptDataRepository.save(police);
		deptDataRepository.save(firefighter);

		Employee empPolice1 = new Employee("경찰관1", 1000D, police);
		Employee empPolice2 = new Employee("경찰관2", 1000D, police);
		Employee empPolice3 = new Employee("경찰관3", 1000D, police);
		Employee empPolice4 = new Employee("경찰관4", 1000D, police);
		Employee empPolice5 = new Employee("경찰관5", 1000D, police);
		Employee empFireFighter = new Employee("소방관1", 1000D, firefighter);

		qEmpRepository.save(empPolice1);
		qEmpRepository.save(empPolice2);
		qEmpRepository.save(empPolice3);
		qEmpRepository.save(empPolice4);
		qEmpRepository.save(empPolice5);

		qEmpRepository.save(empFireFighter);

		em.flush();
		em.clear();
	}

	@Test
	@DisplayName("커스텀 리포지터리 테스트")
	void testCustomRepository(){
		List<Employee> employeeCustom = qEmpRepository.selectAllEmployees();

		System.out.println(employeeCustom);
	}
}
```

