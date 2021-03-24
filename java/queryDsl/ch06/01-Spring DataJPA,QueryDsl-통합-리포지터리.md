# Spring DataJPA,QueryDsl 통합 리포지터리

**예제 파일**

- [github.com/example-querydsl-with-data-jpa](https://github.com/soongujung/example-querydsl-with-data-jpa)
  - QueryDsl 과 DataJPA 를 하나의 리포지터리에 통합적으로 제공하는 방식    
    

QueryDsl 과 함께 Spring Data 기반으로 작성된 Repository 에서 사용할 수 있도록 상속관계를 만들어서 하나의 Repository 파일 내에서 Data JPA 와 QueryDsl 을 짬뽕해서 사용할 수 있다. 이 문서에서 정리하는 내용은 Data JPA Repository 와 QueryDSL 을 함께 사용할 수 있도록 하는 예제를 직접 정리해보고자 한다. 

> 개인적인 취향이지만, 나의 경우는 QueryDsl 리포지터리는 따로 구현해서 별도의 리포지터리로 빼두는 것이 낫다는 생각이 있다. 객체지향 적으로 멋있게 풀어내는 것보다는 제품에 대한 시각이 더 중요하지 않나 하는 생각이 있어서이다. 여러가지 라이브러리가 강한 결합을 가지는 것은 좋지 않다는 생각도 들었었다. 다른 사람이 쉽게 파악할 수 있는 코드가 좋은 코드라는 생각도 들었다. 실제로 강의를 들으면서 강사님(김영한 님) 께서도 꼭 이렇게 합쳐놓는게 만능은 아니라는 설명 역시 있었었다.


아래에서 정리하는 예제의 시나리오를 정리해봤다.

- `QEmpRepository` , `QEmpRepositoryImpl` 라는 이름의 사용자 정의 리포지터리를 생성
  - 단순 조회 쿼리만을 수행하는 Repository 만
- `EmpCustomDataRepository`라는 이름의 Spring Data JPA 기반의 리포지터리 생성
  - 이 Data Repository 용도의 인터페이스 내에서 select 구문을 호출할 수 있도록 QEmpRepository 인터페이스를 상속받는다.



## 예제

### QEmpRepository.java

`QEmpRepository.java` 는 `QEmpRepositoryImpl.java` 에서 implements 하고 있다. 

```java
package io.study.qdsl_and_datajpa.employee.repository;

import java.util.List;

public interface QEmployeeRepository {
	List<Employee> findByAnyNameOrSalary(String name, Double salary);
}
```



### QEmpRepositoryImpl.java

아직까지는, QueryDsl을 사용하는 경우를 흉내낸 단순 리포지터리이다. 나중에 볼때 이해가 쉬우려면 예제의 단순함이 훨씬 중요하다는 생각에 QueryDsl 로직을 배제했다.

```java
package io.study.qdsl_and_datajpa.employee.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class QEmployeeRepositoryImpl implements QEmployeeRepository{

	private final EntityManager entityManager;

	private final JPAQueryFactory queryFactory;

	@Autowired
	public QEmployeeRepositoryImpl(EntityManager entityManager){
		this.entityManager = entityManager;
		this.queryFactory = new JPAQueryFactory(entityManager);
	}

	public List<Employee> findByAnyNameOrSalary(String name, Double salary){
		return queryFactory.selectFrom(QEmployee.employee)
			.where(QEmployee.employee.name.eq(name)
				.or(QEmployee.employee.salary.eq(salary)))
			.fetch();
	}

}
```



### EmpCustomDataRepository.java

> Data JPA 리포지터리 이다.

```java
package io.study.qdsl_and_datajpa.employee.repository;

import io.study.qdsl_and_datajpa.employee.Employee;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeCustomRepository extends JpaRepository<Employee, Long>, QEmployeeRepository{

	@Query("select e from Employee e where e.id =:id")
	List<Employee> findEmployeesById(@Param("id") Long id);
}
```

DataJPA 리포지터리인  `EmpCustomDataRepository` 는 JpaRepository 를 상속받으면서도, QEmpRepository 도 상속받고 있다. interface는 다중상속을 받을 수 있다. 이렇게 되면 EmpCustomDataRepository 는 아래의 기능을 모두 가지게 된다.  

- JpaRepository\<Employee, Long\> 
- QEmployeeRepository
  - findByAnyNameOrSalary(String name, Double salary)



### EmpCustomDataRepositoryTest.java

```java
package io.study.qdsl_and_datajpa.employee;

import io.study.qdsl_and_datajpa.employee.repository.EmployeeCustomRepository;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class EmployeeCustomRepositoryTest {

	@Autowired
	EmployeeCustomRepository customEmpRepository;

	@Autowired
	EntityManager em;

	@BeforeEach
	void insertData(){
		Employee e1 = new Employee("소방관 #1", 1000D);
		Employee e2 = new Employee("소방관 #2", 2000D);

		customEmpRepository.save(e1);
		customEmpRepository.save(e2);
	}

	@Test
	@DisplayName("커스텀_리포지터리_테스트")
	void 커스텀_리포지터리_테스트(){
		List<Employee> employeeCustom = customEmpRepository.findByAnyNameOrSalary("소방관 #1", 1000D);
		System.out.println(employeeCustom);
	}

}
```



출력결과

```plain
[Employee(id=1, name=소방관 #1, salary=1000.0)]
```

