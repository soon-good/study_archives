# 스프링 Data JPA 확장기능

아주 옛날에 정리한 문서를 몇번씩 자주, 다시 돌아볼 때마다 가끔은 얼굴이 화끈 거릴 정도로 쑥스럽기는 하다. 오늘도 다시 문서를 보면서 같은 감정이 들었다. 아침마다 애널리스트들의 30분짜리 시황을 들어서 생각하는 방식이 변해서일까? 아니면 문서로 정리하는 일을 오랫동안 해오면서 나 자신도 철이 든 것일까? 하는 생각이 들었다. 오늘은 예전에 정리했던 문서를 최대한 깔끔하게 정리해봤다. 그래도 1년이 넘게 꾸준히 뭐가 되든 직접 실천해온 나에게 대견한 감정이 들었다. 과거의 나는 빨리 뭔가를 더 배우고 싶어서 굉장히 성급했었던 것 같다.    



# 1. 사용자 정의 리포지터리

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



# 2. Auditing

엔티티의 생성/변경 시점에 아래의 네 가지 항목들을 기록으로 남겨둘 수 있다.

- 등록일
- 수정일
- 등록자
- 수정자

엔티티마다 이러한 내용들을 남겨놓으면 추후 운영단계에서 번거로움을 많이 덜어낼 수 있다.  



## 예제1) 순수 JPA - 등록일/수정일

### JpaBaseEntity

```java
package io.study.erd_example.emp.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@MappedSuperclass
public class JpaBaseEntity {

	@Column(updatable = false)
	private LocalDateTime createdDate;
  
  @Column(updatable = false)
	private LocalDateTime updatedDate;

	@PrePersist
	public void prePersist(){
		LocalDateTime now = LocalDateTime.now();
		createdDate = now;	// 데이터 초기 생성시 현재 시점의 시간을 지정
		updatedDate = now;	// 데이터 초기 생성시 현재 시점의 시간을 지정
//		updatedDate = null;
//		디폴트 데이터를 null 로 지정하게 되면 추후 불편해지기도 한다.
	}

	@PreUpdate
	public void preUpdate(){
		updatedDate = LocalDateTime.now();
	}
}
```

- @MappedSuperclass
  - "**다른 엔티티들에 의해 매핑되는 수퍼클래스**" 임을 지정할 때 사용하는 애노테이션 
  - 다른 Entity 클래스 (e.g. Employee) 가 상속받아서 사용한다.
  - 위 예제에서 작성한  JpaBaseEntity 이름의 엔티티 클래스에 @MappedSuperClass 를 지정해야만 다른 엔티티(e.g. `Employee` )에서 JpaBaseEntity 타입의 클래스를 인식할 수 있다.
  - 자세한 원리는 JPA 기본편에서 자세하게 설명해준다.
- @Column(updatable = false)
  - 데이터의 초기 생성시점은 변경되면 안된다는 조건을 걸고 싶을 때 사용한다.
  - 초기 생성 시점에 대한 기록이 자주 변경된다면 조회상으로도 모호함을 낳는 것으로 인해 createdDate와 같은 필드에는 가급적 @Column(updatable = false)를 사용하는 것을 권장하는 편이다.
  - @Column(updatable = false, insertable = true) 와 같이 지정할 수 도 있다.
- @PrePersist
  - Persist, 즉 DB에 저장하기 직전에 호출되도록 하고 싶은 메서드에 @PrePersist 를 지정해준다.
- @PreUpdate
  - Entity 에 대해 Update 동작이 발생할 때에 수행할 동작을 기술한 메서드에 @PreUpdate 를 지정해준다.  

  

### Employee.java

`Employee` 엔티티에서는 위에서 작성한 `JpaBaseEntity` 클래스를 상속(확장)하고 있다.

```java
@Getter @Setter
@ToString(exclude = "dept")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "EMPLOYEE")
public class Employee extends JpaBaseEntity{

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long empNo;

	@Column(name = "USERNAME")
	private String username;

	@Column(name = "SALARY")
	private Double salary;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DEPT_NO")
	@JsonIgnore
	private Department dept;

//	public Employee(){}

	public Employee (String username, Double salary, Department dept){
		this.username = username;
		this.salary = salary;
		this.dept = dept;
		dept.getEmployees().add(this);
	}

}
```



### Test :: EmpRepositoryTest

```java
@SpringBootTest
@Transactional
public class EmpRepositoryTest {

	@Autowired
	private EntityManager em;

	@Autowired
	private EmpRepository empRepository;

	@Autowired
	private EmpDataRepository empDataRepository;

	@Autowired
	private DeptDataRepository deptDataRepository;

	private Department police;

	@BeforeEach
	void insertData(){
		police = new Department("POLICE");
		Department firefighter = new Department("FIREFIGHTER");

		deptDataRepository.save(police);
		deptDataRepository.save(firefighter);

		Employee empPolice1 = new Employee("경찰관1", 1000D, police);
		Employee empPolice2 = new Employee("경찰관2", 1000D, police);
		Employee empPolice3 = new Employee("경찰관3", 1000D, police);
		Employee empPolice4 = new Employee("경찰관4", 1000D, police);
		Employee empPolice5 = new Employee("경찰관5", 1000D, police);
		Employee empFireFighter = new Employee("소방관1", 1000D, firefighter);

		empDataRepository.save(empPolice1);
		empDataRepository.save(empPolice2);
		empDataRepository.save(empPolice3);
		empDataRepository.save(empPolice4);
		empDataRepository.save(empPolice5);

		empDataRepository.save(empFireFighter);

		em.flush();
		em.clear();
	}

  // ...
  
	@Test
	@DisplayName("Auditing #1 - 순수 JPA 기반 테스트")
	public void testAuditingByRawJPA() throws Exception{
		Employee employee = new Employee("경찰관 #99", 1000D, police);
		empDataRepository.save(employee); 	// save 메서드는 순수 JPA를 사용하기에는 시간이 많지 않아서... Data JPA를 썼다...
											// 추후 순수 JPA 기반 메서드 만들 예정!!

		Thread.sleep(100);
		employee.setUsername("경찰관 #100");

		em.flush();
		em.clear();

		Employee e = empDataRepository.findById(employee.getEmpNo()).get();
		System.out.println("createDate  :: " + e.getCreatedDate());
		System.out.println("updatedDate :: " + e.getUpdatedDate());
	}
}
```

출력결과

```text
createDate  :: 2020-08-10T23:24:03
updatedDate :: 2020-08-10T23:24:04
```



## 예제2) Spring Data JPA 를 사용할 때

> 위에서 살펴본 JpaBaseEntity 는 Spring Data JPA 를 사용하게 된다면 보일러플레이트 성격의 중복 코드들을 또 다시 공통화 할 수 있다. 
>
> - LocalDateTime now = LocalDateTime.now();
> - createdDate = now
>
> 와 같은 구문은 직접 사용할 경우 예기치 않은 실수가 발생할 수도 있고, 중복코드를 공통화한다면 좋다. Spring Data JPA 에서는 아래의 애노테이션들을 사용하여 이런 중복 코드들을 공통화하는 것이 가능하다.
>
> - @EntityListeners
> - @CreatedDate
> - @LastModifiedDate



순수 JPA를 사용할 때에 비해 코드가 짧아지거나 간결해지진 않는다. 작성하는 코드의 양은 비슷하다. 다만, 차이점은 아래와 같다.

- 순수 JPA를 사용할 때

  - @PrePersist, @PreUpdate 에서 프로그래머가 직접 작성하게 될때 부주의하게 실수할 수 있는 부분이 있다.

- Data JPA를 사용할 때

  - 아래와 같은 중복될 수밖에 없는 코드들을 공통화한 @CreatedDate, @LastModifiedDate 어노테이션을 사용한다.

  - 즉, 중복되는 코드들이 Data JPA 내부적인 원리로 내재화 되어 있다는 것이 다른 점으로 보인다.  

    ```java
    LocalDateTime now = LocalDateTime.now();
    createdDate = now;
    ```


  

Spring Data JPA 기반으로 Auditing 을 사용하는 한 가지 장점이 있다.

- 등록자, 수정자를 AuditorAware 와 연동하여 @CreatedBy, @LastModifiedBy 등으로 지정해줄 수 있다는 점이다.


  

Spring Data JPA 기반으로 Auditing 엔티티를 작성할 때 사용하는 어노테이션을 정리해보면 아래와 같다.

- @EnableJpaAuditing
  - 스프링 부트 설정 클래스에 적용
- @MappedSuperclass
  - 엔티티에 적용 (e.g. `JpaBaseEntity` or `DataJpaBaseEntity`)
- @EntityListeners(AuditingEntityListener.class)
  - 엔티티에 적용
- @CreatedDate
  - 엔티티 생성 시점 
  - org.springframework.data.annotation
- @LastModifiedDate
  - 엔티티 수정 시점
  - org.springframework.data.annotation



### DataJpaBaseEntity.java

```java
package io.study.erd_example.emp.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class DataJpaBaseEntity {

	@CreatedDate
	@Column(updatable = false)
	LocalDateTime createdDate;

	@LastModifiedDate
	LocalDateTime lastModifiedDate;
}
```

Data JPA를 사용할 때 사용하는 어노테이션은 아래와 같다.

- @EntityListeners(AuditingEntityListener.class)
  - 정리하자. 정리열매~!!!
- @CreatedDate
  - 엔티티 생성 시점 지정
- @LastModifiedDate
  - 엔티티 수정 시점 지정



### Employee.java

순수 JPA 기반 Auditing 예제에서는 Employee 클래스가 JpaBaseEntity를 상속하도록 했었다. Data JPA 기반 Auditing 예제에서는 DataJpaBaseEntity 를 상속받도록 하자.

```java
//@Data
@Getter @Setter
@ToString(exclude = "dept")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "EMPLOYEE")
//public class Employee extends JpaBaseEntity{
public class Employee extends DataJpaBaseEntity{

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long empNo;

	@Column(name = "USERNAME")
	private String username;

	@Column(name = "SALARY")
	private Double salary;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DEPT_NO")
	@JsonIgnore
	private Department dept;

//	public Employee(){}

	public Employee (String username, Double salary, Department dept){
		this.username = username;
		this.salary = salary;
		this.dept = dept;
		dept.getEmployees().add(this);
	}

}
```



### ErdApplication.java

왠만하면 깔끔하게 @Configuration 어노테이션과 함께 @EnableJpaAudting을 추가해서 설정 파일을 만들자. 여기서는 단순예제를 빨리 만들어보기 위해 main() 문을 가지고 있는 Spring Boot Application 클래스에 해당 설정 어노테이션을 추가해주었다. (귀찮다는 핑계이긴 하다.)

```java
package io.study.erd_example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ErdApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErdApplication.class, args);
	}

}
```



### Test :: EmpDataRepositoryTest.java

```java
@SpringBootTest
@Transactional
public class EmpDataRepositoryTest {

	@Autowired
	private EntityManager em;

	@Autowired
	private EmpDataRepository empDataRepository;

	@Autowired
	private DeptDataRepository deptDataRepository;

	private Department police;

	@BeforeEach
	void insertData(){
		police = new Department("POLICE");
		Department firefighter = new Department("FIREFIGHTER");

		deptDataRepository.save(police);
		deptDataRepository.save(firefighter);

		Employee empPolice1 = new Employee("경찰관1", 1000D, police);
		Employee empPolice2 = new Employee("경찰관2", 1000D, police);
		Employee empPolice3 = new Employee("경찰관3", 1000D, police);
		Employee empPolice4 = new Employee("경찰관4", 1000D, police);
		Employee empPolice5 = new Employee("경찰관5", 1000D, police);
		Employee empFireFighter = new Employee("소방관1", 1000D, firefighter);

		empDataRepository.save(empPolice1);
		empDataRepository.save(empPolice2);
		empDataRepository.save(empPolice3);
		empDataRepository.save(empPolice4);
		empDataRepository.save(empPolice5);

		empDataRepository.save(empFireFighter);

		em.flush();
		em.clear();
	}

  // ...

	@Test
	@DisplayName("Data JPA 에서의 Auditing #1")
	void testDataJpaAuditing() throws Exception {
		Employee employee = new Employee("경찰관 #99", 1000D, police);
		empDataRepository.save(employee);

		Thread.sleep(1000);
		employee.setUsername("경찰관 #100");

		em.flush();
		em.clear();

		Employee e = empDataRepository.findById(employee.getEmpNo()).get();
		System.out.println("createDate  :: " + e.getCreatedDate());
		System.out.println("updatedDate :: " + e.getLastModifiedDate());
	}
}
```



### 출력결과

```text
createDate  :: 2020-08-11T21:11:50
updatedDate :: 2020-08-11T21:11:51
```



## 예제3) 글쓴이, 수정한 사람을 Auditing 하기

글 쓴이, 수정한 사람에 대한 데이터 역시 Data JPA 의 Auditing 기능을 이용하여 지정해주는 것이 가능하다. 가끔 이런 기능들을 보면 정말 유지보수성이 뛰어난 라이브러리 이구나 하는 생각도 든다.

- @CreatedBy, @LastModifiedBy 가 엔티티 내에 지정하고 있는 필드에 데이터를 주입하려면 그냥은 사용할 수 없다.  
- AuditorAware 타입의 컴포넌트를 빈으로 등록해두어야 @CreatedBy, @LastModifiedBy 가 가리키는 필드에 데이터를 주입할 수 있다.  
- 이때 AuditorAware 를 이용해서 해당 글을 작성한 사람(@CreatedBy), 수정한 사람(@LastModifiedBy)를 리턴하는 구문을 작성한다. 
- 보통 이때 Spring Security 의 Security Context Holder 를 사용한다. 꼭 Spring Security 가 아니더라도 로그인 한 사용자의 이름이나, 닉네임을 얻어오기 위해 사용 가능한 다른 방법을 이용해서 데이터를 리턴해주면 된다. 

  

**참고자료**  

- [AuditorAware 를 사용하여 등록자 데이터를 자동으로 생성하기](https://mia-dahae.tistory.com/150)
- [[JPA] @CreatedBy](https://ziponia.github.io/2019/05/13/@CreatedBy.html)



### ErdApplication.java

> - @CreatedBy , @LastModifiedBy 가 지정하고 있는 엔티티 내의 필드에 주입할 데이터를 생성하는 부분이 필요하다. 
> - AuditAware 를 이용해서 @CreatedBy, @LastModifiedBy 가 지정하고 있는 필드에 데이터를 주입해줄 수 있다.
> - 꼭 SecurityContextHolder 등의 스프링 시큐리티 기능을 사용해야 하는 것은 아니다. 작성자를 지정할 수 있는 id를 얻어낼 수 있거나, 또는 nickname을 얻어오는 로직이 따로 있다면 그것을 AuditAware 를 빈 등록하는 구문에서 사용할 수 있다.
> - 아이디를 얻어와 지정해주는 로직은 설정(Configuration 또는 Bean)으로 따로 분리해놓았는데, 여기서는 글이 길어지기 때문에 Application 클래스 내에서 바로 확인가능하도록 @Bean으로 등록했다.

  

이번 예제에서는 UUID를 이용해 난수를 생성했다. 보통은 SecurityContextHolder 를 이용해 현재 세션의 사용자를 얻어내는 편이다.  

SecurityContextHolder 를 이용해 현재 세션의 사용자를 얻어냈을 때 아래의 람다 구문의 Optional.of( ... ) 메서드 내에 해당 세션에 대한 요청 ID를 넣어주면 된다.  

```java
package io.study.erd_example;

import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ErdApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErdApplication.class, args);
	}

  // 아래 예제는 
	@Bean
	public AuditorAware<String> auditorAware(){
		return () -> Optional.of(UUID.randomUUID().toString());
	}

}
```



### DataJpaBaseEntity.java

- @CreatedBy
  - 글쓴이에 대한 데이터를 AuditorAware 타입의 컴포넌트로부터 받아와 @CreatedBy 가 가리키는 필드에 데이터를 주입
  - 스프링 시큐리티 또는 내부 개발 팀에서 사용하는 로직을 이용해 로그인 한 사용자 정보에 대한 데이터를 전달해주는 로직을 AuditorAware 관련 로직에 추가해준다.
  - 아이디를 얻어와 지정해주는 로직은 설정으로 따로 분리해놓았는데, 바로 앞전에서 본 AuditingAware 함수를 @Bean으로 등록하는 구문에서 등록하고 있다.
  - 이번 예제에서는 등록자에 단순히 난수를 등록해주고 있다.

- @LastModifiedBy

  - 글을 수정한 사람에 대한 데이터를 AuditorAware 타입의 컴포넌트로부터 받아와 @LastModifiedBy 가 가리키는 필드에 데이터를 주입
  - 보통은 SecurityContextHolder에서 가져온 현재 요청을 보낸 사용자에 대한 아이디를 얻어와 지정해준다.
  - 아이디를 얻어와 지정해주는 로직은 설정(Configuration 또는 Bean)으로 따로 분리해놓았는데, 바로 앞전에서 본 AuditingAware 함수를 @Bean으로 등록하는 구문에서 등록하고 있다.
  - 이번 예제에서는 수정자에 단순히 난수를 등록해주고 있다.




```java
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class DataJpaBaseEntity {

	@CreatedDate
	@Column(updatable = false)
	LocalDateTime createdDate;

	@LastModifiedDate
	LocalDateTime lastModifiedDate;

	@CreatedBy
	@Column(updatable = false)
	String createdBy;

	@LastModifiedBy
	String lastModifiedBy;
}
```



### EmpDataRepositoryTest.java

```java
@SpringBootTest
@Transactional
public class EmpDataRepositoryTest {

	@Autowired
	private EntityManager em;

	@Autowired
	private EmpDataRepository empDataRepository;

	@Autowired
	private DeptDataRepository deptDataRepository;

	private Department police;

	@BeforeEach
	void insertData(){
		police = new Department("POLICE");
		Department firefighter = new Department("FIREFIGHTER");

		deptDataRepository.save(police);
		deptDataRepository.save(firefighter);

		Employee empPolice1 = new Employee("경찰관1", 1000D, police);
		Employee empPolice2 = new Employee("경찰관2", 1000D, police);
		Employee empPolice3 = new Employee("경찰관3", 1000D, police);
		Employee empPolice4 = new Employee("경찰관4", 1000D, police);
		Employee empPolice5 = new Employee("경찰관5", 1000D, police);
		Employee empFireFighter = new Employee("소방관1", 1000D, firefighter);

		empDataRepository.save(empPolice1);
		empDataRepository.save(empPolice2);
		empDataRepository.save(empPolice3);
		empDataRepository.save(empPolice4);
		empDataRepository.save(empPolice5);

		empDataRepository.save(empFireFighter);

		em.flush();
		em.clear();
	}

  // ...
  
	@Test
	@DisplayName("Data JPA 에서의 Auditing #2")
	void testDataJpaAuditing2() throws Exception {
		Employee employee = new Employee("경찰관 #99", 1000D, police);
		empDataRepository.save(employee);

		Thread.sleep(1000);
		employee.setUsername("경찰관 #100");

		em.flush();
		em.clear();

		Employee e = empDataRepository.findById(employee.getEmpNo()).get();
		System.out.println("createDate  :: " + e.getCreatedDate());
		System.out.println("updatedDate :: " + e.getLastModifiedDate());
		System.out.println("createdBy :: " + e.getCreatedBy());
		System.out.println("updatedBy :: " + e.getLastModifiedBy());
	}
}
```



### 출력결과

```text
createDate  :: 2020-08-11T21:29:05
updatedDate :: 2020-08-11T21:29:06
createdBy :: 8566f8cf-7d63-4172-b7c2-7cbd9ebb62f6
updatedBy :: e19fbedc-1a39-4241-83ea-2c440e3c29c0
```



## 예제 4) 글쓴이, 수저한 사람을 Auditing 하기 :: update시에는 null 로 지정

권장되는 방법은 아니다. 등록할 때에만 데이터를 입력하고, 수정할 때에는 null 로 지정하고자 할 때가 있다. 설정 파일위에 작성한 @EnableJpaAuditing 어노테이션에 아래와 같이 추가해준다. 

> @EnableJpaAuditing(modifyOnCreate = false)

위와 같은 어노테이션을 추가해준다.

### 예제

```java
package io.study.erd_example;

import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(modifyOnCreate = false)
@SpringBootApplication
public class ErdApplication {

	public static void main(String[] args) {
		SpringApplication.run(ErdApplication.class, args);
	}
  
	@Bean
	public AuditorAware<String> auditorAware(){
		return () -> Optional.of(UUID.randomUUID().toString());
	}

}

```



## 예제 5) @EntityListeners 대신 전역적으로 Auditing을 강제로 지정할 경우

@EntityListeners(AuditingListener.class) 를 BaseEntity 마다 지정해주기는 조금 번거로울 경우가 있다. 이 경우 src/resources/META-INF/ 밑에 아래와 같이 orm.xml 파일을 작성해준다.

```xml
<?xml version=“1.0” encoding="UTF-8”?>
<entity-mappings xmlns=“http://xmlns.jcp.org/xml/ns/persistence/orm”
xmlns:xsi=“http://www.w3.org/2001/XMLSchema-instance”
xsi:schemaLocation=“http://xmlns.jcp.org/xml/ns/persistence/
orm http://xmlns.jcp.org/xml/ns/persistence/orm_2_2.xsd”
version=“2.2">
	<persistence-unit-metadata>
		<persistence-unit-defaults>
			<entity-listeners>
				<entity-listener
class="org.springframework.data.jpa.domain.support.AuditingEntityListener”/>
			</entity-listeners>
		</persistence-unit-defaults>
	</persistence-unit-metadata>
</entity-mappings>
```



## 예제 6) 상속관계로 용도에 따라 분리

- ErdBaseTimeEntity
  - 생성일, 수정일을 관리하는 클래스
- ErdBaseEntity
  - 생성일, 수정일
  - 작성자, 수정자
  - 네가지 필드 모두를 관리하는 클래스
  - ErdBaseTimeEntity 클래스를 상속받은 클래스

로 분리하는 방식을 용도에 맞게 각 Entity 클래스에서 상속받아 사용하면 분리가 간편해진다. 

### ErdBaseTimeEntity.java

```java
package io.study.erd_example.emp.entity.base;

import java.time.LocalDateTime;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class ErdBaseTimeEntity {

	@CreatedDate
	private LocalDateTime createdDate;

	@LastModifiedDate
	private LocalDateTime lastModifiedDate;
}
```

### ErdBaseEntity.java

```java
package io.study.erd_example.emp.entity.base;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class ErdBaseEntity extends ErdBaseTimeEntity{

	@CreatedBy
	private String createdBy;

	@LastModifiedBy
	private String lastModifiedBy;
}
```

### Employee.java

```java
package io.study.erd_example.emp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.study.erd_example.emp.entity.base.ErdBaseEntity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//@Data
@Getter @Setter
@ToString(exclude = "dept")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "EMPLOYEE")
//public class Employee extends JpaBaseEntity{
//public class Employee extends DataJpaBaseEntity{
public class Employee extends ErdBaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long empNo;

	@Column(name = "USERNAME")
	private String username;

	@Column(name = "SALARY")
	private Double salary;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DEPT_NO")
	@JsonIgnore
	private Department dept;

//	public Employee(){}

	public Employee (String username, Double salary, Department dept){
		this.username = username;
		this.salary = salary;
		this.dept = dept;
		dept.getEmployees().add(this);
	}

}
```



# 3. 도메인 클래스 컨버터

그다지 권장하는 방식은 아니다. Controller 내의 HTTP 요청을 받는 메서드에서 메서드의 파라미터로 도메인 클래스(엔티티 클래스)를 두면, HTTP 요청에 의해 전달되는 데이터를 스프링 내부에서 실제 인자값을 도메인 클래스내의 해당 멤버 필드로 바인딩해준다.  

  

단점은 화면에 종속적인 정보를 도메인에 연결시킨다는 점이다. 이렇게 할 경우, 기획이 자주 바뀔수 밖에 없는 화면단의 관점에 따라 도메인이 변하게 되고, 도메인에 종속된 repository 들의 로직들도 일괄적으로 변할 수 밖에 없다는 점이 단점이다.  

  

가급적이면, 굉장히 급한 프로토타입을 만드는 경우가 아니고서는 권장되지 않는 방식이다. 주의할것!!!

## 예제

### EmpController.java

```java
@Controller
public class EmpController {

	private final EmpDataRepository empDataRepository;

	private final DeptDataRepository deptDataRepository;

	private final EntityManager em;

	public EmpController(EmpDataRepository empDataRepository,
							DeptDataRepository deptDataRepository,
							EntityManager em){
		this.empDataRepository = empDataRepository;
		this.deptDataRepository = deptDataRepository;
		this.em = em;
	}

	// 도메인 클래스 컨버터 예제 - 일반 요청 (비교를 위해 예제로)
	@GetMapping("/employee/v1/{id}")
	public String getEmployeeById(@PathVariable("id") Long id){
		Employee employee = empDataRepository.findById(id).get();
		return employee.getUsername();
	}

	// 도메인 클래스 컨버터 예제 - 도메인 클래스 컨버터를 사용
	@GetMapping("/employee/v2/{id}")
	public String getEmployeeById2(@PathVariable("id") Employee employee){
		return employee.getUsername();
	}

	@PostConstruct
	public void initData(){
		Department police = new Department("POLICE");
		deptDataRepository.save(police);

		for(int i=0; i<100; i++){
			final Employee e = new Employee("경찰관 #"+String.valueOf(i), 1000D, police);
			empDataRepository.save(e);
		}
    
    em.flush();
    em.clear();
	}
}

```



# 4. Web 계층에서의 페이징 & 정렬

> 웹(Web) 계층인 컨트롤러 측에서의 데이터 전달방식 및 페이징에 관련된 세부 특화된 설정들을 다룬다.  
>
> 이전에 정리한 문서에서 Repository 계층에서 페이징을 적용할 때 반환값 및 인자값을 Pageable로 지정하여 쿼리를 하면 페이징이 적용된 결과값을 가져오는 것을 확인했었다. 웹 계층, 즉, 컨트롤러에서도 이러한 방식이 동일하게 적용될 수 있다.
>
> 웹(Web) 계층인 컨트롤러에서 반환타입을  Page\<E\> 를 지정해주게 되면 페이징이 적용된 결과를 클라이언트에게 전달해줄 수 있다. Page\<E\> 정보 내에 포함된 정보들은 아래와 같다.
>
> - content : {...}
>   - 본문데이터
> - pageable
>   - 페이징 관련 데이터
> - last, totalPages, totalElements, size, number, numberOfElements ...
>   - 부가적으로 위와 같은 필드들 또한 클라이언트 측에 전달해줄 수 있게 도와준다.
>   - Page\<E\> 를 반환타입으로 하는 메서드에서 제공되는 부가정보이다.
>
> Page\<E\> 는  org.springframework.data.domain 내에 포함된 클래스인데, 주로  Page\<Dto\> 와 같은 형식으로 리턴하는 것이 관례이다. (Entity 클래스를 직접 전달하지는 않는다). JPA 기반의 어떤 메서드에서 Page\<E\> 타입으로 데이터를 리턴하면 페이징이 적용된 데이터를 받아볼수 있다.

컨트롤러의 파라미터로 Pageable을 인자값으로 지정하면, 브라우저에서 Pageble의 하위 구현체인 PageRequest의 형식에 맞게끔 파라미터를 전달해주면, 컨트롤러에서 이 것을 해석해 페이지네이션을 적용하게 된다. 음... 조금 말을 정리할 필요가 있다. 너무 두서가 없다. 하지만 시간이 없으니...  

org.springframework.data.domain.Page 를 컨트롤러의 해당 메서드의 리턴타입으로 사용하게 되면 컨트롤러의 해당 메서드는 페이징이 적용된 결과를 json 으로 변환하여 브라우저에게 전달해주게 된다.


```java
@RestController
public class SampleController{
  @GetMapping("sample/index")
  public Page<EmployeeDto> getSampleIndex(){
    return empDataRepository.findAll();
  }
}
```

이렇게 작성된 Url Mapping 으로 브라우저에서 요청을 보내면 브라우저에서는 응답값에 아래와 같은 형식의 데이터를 받을 수 있다.

```json
{
    "content": [
        {
            "empNo": 1,
            "username": "경찰관 #0",
            "salary": 1000.0
        },
        // ...
    ],
    "pageable": {
        "sort": {
        	// ...
        },
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 5,
        "paged": true,
        "unpaged": false
    },
    "last": false,
    "totalPages": 20,
    "totalElements": 100,
    "size": 5,
    "number": 0,
    "numberOfElements": 5,
    "first": true,
    "sort": {
        "unsorted": false,
        "sorted": true,
        "empty": false
    },
    "empty": false
}
```

본문 내용인 content, 페이징 관련 데이터인 pageable 외에도 

- last
- totalPages
- totalElements
- size
- number
- numberOfElements
- first

등의 파라미터를 제공해준다. 위의 필드들은 Pageable 을 웹 계층인 Controller에서 return 타입으로 제공할 때 응답값에 포함되는 값들이다. 

## Pageable 을 사용하게 되면 사용할 수 있는 파라미터들

자세한 내용은 공식 문서를 보는 것이 낫다. 하지만, 정말 급할 때 보고 참고할 수 있도록 몇가지의 파라미터 들을 정리해보려 한다.

- page
  - **0 부터 시작하는 것이 원칙이다.**
  - 몇 번째 페이지를 가져올 지를 지정한다.
  - 만약 page=9, size=7 을 지정했다면 쿼리는 limit 63, 7 이 된다. (63 번째 데이터에서부터 7개의 데이터를 들고온다)
- size
  - 몇개의 데이터를 들고 올 지를 지정한다.
- sort
  - asc, desc 를 지정가능하다.



## 예제) Controller

예제로 사용할 컨트롤러의 코드이다. Service 계층을 두어 별도의 가공 및 처리를 두는 계층을 두는 것이 코드의 응집도를 낮추는 좋은 방법이다. 하지만, 예제의 단순함이 복습에 더 유리하다고 판단되어 Service 계층없이  Controller에서 모든 가공 및 처리를 모두 수행하도록 했다.

### EmpController.java

```java
@Controller
public class EmpController {

	private final EmpDataRepository empDataRepository;

	private final DeptDataRepository deptDataRepository;

	private final EntityManager em;

	public EmpController(EmpDataRepository empDataRepository,
							DeptDataRepository deptDataRepository,
							EntityManager em){
		this.empDataRepository = empDataRepository;
		this.deptDataRepository = deptDataRepository;
		this.em = em;
	}

	// ...
  
	@GetMapping("/employees")
	@ResponseBody
	public Page<EmployeeDto> list(@PageableDefault(size = 5, sort = "username") Pageable pageable){
		Page<Employee> all = empDataRepository.findAll(pageable);

    // Dto 로 반환 
		Page<EmployeeDto> map = all.map(e -> {
			final String username = e.getUsername();
			final Double salary = e.getSalary();
			return new EmployeeDto(username, salary);
		});

		return map;
	}

	@PostConstruct
	public void initData(){
		Department police = new Department("POLICE");
		deptDataRepository.save(police);

		for(int i=0; i<100; i++){
			final Employee e = new Employee("경찰관 #"+String.valueOf(i), 1000D, police);
			empDataRepository.save(e);
		}
	}
}
```



## 예제 1) 아무인자값 없이 요청해보기

> Spring Data JPA는 default page size 를 20으로 지정하고 있기 때문에, 아무 인자값 없이 Pageable 한 리퀘스트를 받으면, Spring Data JPA는 자동으로 page size를 20으로 지정한 가장 첫번째 결과를 돌려준다.  

  

아무 인자값 없이 [http://localhost:8080/employees](http://localhost:8080/employees) 로 요청을 보내면, 해당 URL 에 매핑된 컨트롤러의 파라미터로 Pageable 이 지정되어 있다면 스프링 부트는 자동으로 Pageable 의 하위구현체인 PageRequest 객체를 생성해낸다. 위의 컨트롤러의 list 에서는 Pageable 을 인자로 받을수 있는 Spring Data JPA 메서드를 사용하고 있다.

> empDataRepository.findAll(pageable);

즉, 위와 같은 쿼리 메서드를 컨트롤러에서 호출하고 있기 때문에 페이징이 적용된 결과를 클라이언트에게 돌려준다. 

### URL

postman 또는 insomnia 와 같은 rest client 에서 아래와 같은 요청을 보내보자.

> [http://localhost:8080/employees](http://localhost:8080/employees)

### 응답결과

```json
{
    "content": [
        {
            "username": "경찰관 #0",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #1",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #2",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #3",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #4",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #5",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #6",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #7",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #8",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #9",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #10",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #11",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #12",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #13",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #14",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #15",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #16",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #17",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #18",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #19",
            "salary": 1000.0
        }
    ],
    "pageable": {
        "sort": {
            "sorted": false,
            "unsorted": true,
            "empty": true
        },
        "pageNumber": 0,
        "pageSize": 20,
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "last": false,
    "totalElements": 200,
    "totalPages": 10,
    "sort": {
        "sorted": false,
        "unsorted": true,
        "empty": true
    },
    "numberOfElements": 20,
    "size": 20,
    "number": 0,
    "first": true,
    "empty": false
}
```



### SQL

```sql
select 
	employee0_.emp_no as emp_no1_1_, 
	employee0_.dept_no as dept_no4_1_, 
	employee0_.salary as salary2_1_, 
	employee0_.username as username3_1_ 
from employee employee0_ 
limit 20;
```

처음 데이터에서부터 20개의 데이터를 가져왔다.



## 예제 2) page 번호 지정해보기

### URL

postman 또는 insomnia 와 같은 rest client 에서 아래와 같은 요청을 보내보자.

> [http://localhost:8080/employees?page=9&size=7](http://localhost:8080/employees?page=9&size=7)

이 요청에 대한 결과는 아래와 같다.

### 응답결과

```json
{
    "content": [
        {
            "username": "경찰관 #63",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #64",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #65",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #66",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #67",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #68",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #69",
            "salary": 1000.0
        }
    ],
    "pageable": {
        "sort": {
            "sorted": false,
            "unsorted": true,
            "empty": true
        },
        "pageNumber": 9,
        "pageSize": 7,
        "offset": 63,
        "paged": true,
        "unpaged": false
    },
    "last": false,
    "totalElements": 200,
    "totalPages": 29,
    "sort": {
        "sorted": false,
        "unsorted": true,
        "empty": true
    },
    "numberOfElements": 7,
    "size": 7,
    "number": 9,
    "first": false,
    "empty": false
}
```



### SQL

생성된 쿼리는 아래와 같다.

```sql
select 
	employee0_.emp_no as emp_no1_1_, 
	employee0_.dept_no as dept_no4_1_, 
	employee0_.salary as salary2_1_, 
	employee0_.username as username3_1_ 
from employee employee0_ 
limit 63, 7;
```

> SQL의 의미는 '63번째 데이터에서부터 7개의 데이터를 가져왕~' 이다.



## 예제 3) sorting 사용해보기

> sorting 사용시 [url]?sort=[필드명],[ASC/DESC]&sort=[필드명],[ASC/DESC]&... 과 같은 형식으로 요청을 보내면 된다.  



### URL

> [http://localhost:8080/employees?page=0&size=5&sort=salary,desc&sort=username,asc](http://localhost:8080/employees?page=0&size=5&sort=salary,desc&sort=username,asc)

파라미터 값으로 

- page = 0
- size = 5
- sort = salary, desc
- sort = username, asc

를 지정해주었다.

### 응답결과

```json
{
    "content": [
        {
            "username": "경찰관 #0",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #0",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #1",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #1",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #10",
            "salary": 1000.0
        }
    ],
    "pageable": {
        "sort": {
            "sorted": true,
            "unsorted": false,
            "empty": false
        },
        "pageNumber": 0,
        "pageSize": 5,
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "last": false,
    "totalElements": 200,
    "totalPages": 40,
    "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
    },
    "numberOfElements": 5,
    "size": 5,
    "number": 0,
    "first": true,
    "empty": false
}
```



### SQL

```sql
select 
	employee0_.emp_no as emp_no1_1_, 
	employee0_.dept_no as dept_no4_1_, 
	employee0_.salary as salary2_1_, 
	employee0_.username as username3_1_ 
from employee employee0_ 
order by 
	employee0_.salary desc, 
	employee0_.username asc 
limit 5;
```



## 예제 4) 디폴트 페이징 size 를 변경하기 (default-page-size)

> 스프링 부트에서 spring data jpa 를 의존성으로 추가해주었다면, 기본으로 지정된 페이징 사이즈는 디폴트 값이 20이다. 이렇게 지정된 디폴트 페이징 size를 커스터마이징할 필요가 있을 때가 있다. 

별다른 설정 없이 스프링 부트의 페이징 기능을 사용하고 있다면, 프로젝트 내에 기본으로 적용된 페이징 size는 20이다. 즉, 클라이언트에서 아무런 인자값 없이 요청이 왔다면, 디폴트 페이징 size가 20 인채로 요청이 수행되게 된다.  

기본으로 지정된 페이징 size를 바꾸려 한다면 application.properties 또는 application.yml 에서  

- spring.data.web.pageable.default-page-size

에 값을 지정해주면 된다.  

### application.yml

디플트 페이징 사이즈를 변경해보자

```yaml
spring:
  profiles:
    active: testdocker
---
spring:
  profiles: testdocker

	# spring.data.web.pageable.default-page-size 를 10으로 지정해주었다.
  data:
    web:
      pageable:
        default-page-size: 10

  jpa:
    hibernate:
      ddl-auto: update
      use-new-id-generator-mappings: false
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL5InnoDBDialect
        
  datasource:
    url: jdbc:mariadb://localhost:23306/ec2_web_stockdata
    username: testuser
    password: 1111
    hikari:
      auto-commit: true
    driver-class-name: org.mariadb.jdbc.Driver

logging.level:
  org.hibernate.SQL: debug
```



### URL

> [http://localhost:8080/employees](http://localhost:8080/employees)

  

### 응답결과

```json
{
    "content": [
        {
            "username": "경찰관 #0",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #1",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #2",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #3",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #4",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #5",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #6",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #7",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #8",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #9",
            "salary": 1000.0
        }
    ],
    "pageable": {
        "sort": {
            "sorted": false,
            "unsorted": true,
            "empty": true
        },
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 10,
        "paged": true,
        "unpaged": false
    },
    "totalPages": 10,
    "last": false,
    "totalElements": 100,
    "size": 10,
    "number": 0,
    "sort": {
        "sorted": false,
        "unsorted": true,
        "empty": true
    },
    "numberOfElements": 10,
    "first": true,
    "empty": false
}
```



### SQL

```sql
select 
	employee0_.emp_no as emp_no1_1_, 
	employee0_.dept_no as dept_no4_1_, 
	employee0_.salary as salary2_1_, 
	employee0_.username as username3_1_ 
from employee employee0_ 
limit 10;
```



## 예제 5) 최대 한계 페이징 size 변경하기 (max-page-size)

> 간혹 클라이언트 단에서 이상한 요청을 보낼때가 있다. 악의적인 공격일 수도 있고, 프로그램상의 오류일 수도 있다. 그런데, 그런 요청중에 DB의 데이터에서 10만개를 페이징 size로 지정하고 2020 번째 페이징을 요청하게 되는 경우가 생길 수 있다. 트래픽이 많은 서비스라면 생각만 해도 아찔하긴 하다.

스프링 부트내의 설정에서 spring data web 에서 제공하는 파라미터가 있다. 

- spring.data.web.pageable.max-page-size

를 원하는 값으로 지정해주면 지정해준 값 만큼만 클라이언트에서 온 요청의 페이징 사이즈가 max-page-size를 넘어서면 자동으로 max-page-size 만큼만 return 해주게 된다.  

예제는 나중에 추가하자. (default-page-size = 3, max-page-size = 5 로 지정해서 예제 돌려보고 정리합시다~)



## 예제 6) URL 매핑 하나에 대해서만 특화된 설정을 하려 할 때

> 트래픽이 많은 서비스의 경우 간혹 정말 많은 트래픽이 집중되는 페이지나, URL이 있을 수 있다. 이런 경우 application.yml 에 설정해둔 설정을 오버라이딩 해서 해당 요청에 대해서만 특화된 설정이 적용되도록 할수도 있다. 이때 사용하는 것이 @PageableDefault 어노테이션이다.  



### EmpController.java

```java
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
// ... 중략 ... 

@Controller
public class EmpController {

	private final EmpDataRepository empDataRepository;

	private final DeptDataRepository deptDataRepository;

	private final EntityManager em;

	public EmpController(EmpDataRepository empDataRepository,
							DeptDataRepository deptDataRepository,
							EntityManager em){
		this.empDataRepository = empDataRepository;
		this.deptDataRepository = deptDataRepository;
		this.em = em;
	}
  
	// ...
  
	@GetMapping("/employees")
	@ResponseBody
	public Page<EmployeeDto> list(@PageableDefault(size = 5, sort = "username") Pageable pageable){
		Page<Employee> all = empDataRepository.findAll(pageable);

    // Dto 로 변환 
		Page<EmployeeDto> map = all.map(e -> {
			final String username = e.getUsername();
			final Double salary = e.getSalary();
			return new EmployeeDto(username, salary);
		});

		return map;
	}

	@PostConstruct
	public void initData(){
		Department police = new Department("POLICE");
		deptDataRepository.save(police);

		for(int i=0; i<100; i++){
			final Employee e = new Employee("경찰관 #"+String.valueOf(i), 1000D, police);
			empDataRepository.save(e);
		}
	}
}

```



### URL

> [http://localhost:8080/employees](http://localhost:8080/employees)

  

### 응답결과

아무런 설정을 하지 않고, Controller의 URL Mapping 에서만 @PageableDefault 를 적용한 결과 5개의 데이터만 들고온 것을 확인 가능하다. username으로 sorting 되어있는 것 또한 확인 가능하다.

```json
{
    "content": [
        {
            "username": "경찰관 #0",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #1",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #10",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #11",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #12",
            "salary": 1000.0
        }
    ],
    "pageable": {
        "sort": {
            "unsorted": false,
            "sorted": true,
            "empty": false
        },
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 5,
        "paged": true,
        "unpaged": false
    },
    "totalPages": 20,
    "last": false,
    "totalElements": 100,
    "size": 5,
    "number": 0,
    "first": true,
    "numberOfElements": 5,
    "sort": {
        "unsorted": false,
        "sorted": true,
        "empty": false
    },
    "empty": false
}
```



### SQL

아무런 설정을 하지 않고, Controller의 URL Mapping 에서만 @PageableDefault 를 적용한 결과 5개의 데이터만 들고온 것을 확인 가능하다. username으로 sorting 되어있는 것 또한 확인 가능하다.

```sql
select 
	employee0_.emp_no as emp_no1_1_, 
	employee0_.dept_no as dept_no4_1_, 
	employee0_.salary as salary2_1_, 
	employee0_.username as username3_1_ 
from employee employee0_ 
order by employee0_.username asc 
limit 5;
```



## 예제 7) 접두사 (prefix)

컨트롤러에 요청을 보낼때 요청을 받은 컨트롤러 입장에서 두가지의 DB 테이블에서 데이터를 인출해와야 하는 경우가 있다. 그리고, 두 테이블은 성능이슈로 인해 각 테이블에 대해 서로 다른 페이징 방식으로 데이터를 인출해와야 한다. 이런 경우에 대해 사용하는 방식이다.

```java
	@GetMapping("/employees")
	@ResponseBody
	public Page<GroupDto> list(
    		@Qualifier("employee") Pageable empPageable,
    		@Qualifier("department") Pageable deptPageable
  ){
		Page<Employee> all = empDataRepository.findAll(empPageable);

    // Dto 로 변환 
		Page<EmployeeDto> map1 = all.map(e -> {
			final String username = e.getUsername();
			final Double salary = e.getSalary();
			return new EmployeeDto(username, salary);
		});
    
    Page<DepartmentDto> deptDataRepository.findAll(deptPageable);

    // Dto 로 변환 
		Page<DepartmentDto> map2 = all.map(d -> {
			final String localName = d.getLocalName();
      final String deptName = d.getDeptName();
			return new DepartmentDto(username, salary);
		});

    Page<GroupDto> result = groupService.processEmpAndDept(map1, map2);
    
		return result;
	}
```

자세한 예제는 생략~~!! 절대 귀찮아서는 아니다 ㅋㅋ ㅠㅜ  



## 예제 8) Dto로의 변환 을 조금 더 편하게!!

> repository에서 데이터를 가져올 때 Dto의 생성자로 반환하는 편의메서드를 작성하는 방법을 알아보자.

Dto 측에서 Entity를 바라보는 것은 크게 문제가 되지 않는다. Dto의 생성자를 통해 Entity의 정보를 가져오도록 편의성을 제공할 수 있다.  단, Entity 인스턴스를 필드로 가지고 있지 않아야 하고, Entity를 접근하는  getter/setter가 없어야 한다.

보통 아래와 같이 매핑을 한다.

```java
	@GetMapping("/employees")
	@ResponseBody
	public Page<EmployeeDto> list(@PageableDefault(size = 5, sort = "username") Pageable pageable){
		Page<Employee> all = empDataRepository.findAll(pageable);

		Page<EmployeeDto> map = all.map(e -> {
			final String username = e.getUsername();
			final Double salary = e.getSalary();
			return new EmployeeDto(username, salary);
		});

		return map;
	}
```

그런데 이 방식 외에도 Dto의 생성자를 커스터마이징하면 더 간편하게 편의성을 제공할 수 있다.

```java
	@GetMapping("/v2/employees")
	@ResponseBody
	public Page<EmployeeDto> listV2(@PageableDefault(size = 5, sort = "username") Pageable pageable){
		Page<Employee> all = empDataRepository.findAll(pageable);

		Page<EmployeeDto> map = all.map(e -> {
			return new EmployeeDto(e);
		});

		return map;
	}
```

코드가 더 간결해졌다.  

더 간결하게 해보자

```java
	@GetMapping("/v2/employees")
	@ResponseBody
	public Page<EmployeeDto> listV2(@PageableDefault(size = 5, sort = "username") Pageable pageable){
		Page<Employee> all = empDataRepository.findAll(pageable);
    
		Page<EmployeeDto> map = all.map(e->new EmployeeDto(e));

		return map;
	}
```

이번엔 메서드 레퍼런스를 사용해보자.

```java
	@GetMapping("/v2/employees")
	@ResponseBody
	public Page<EmployeeDto> listV2(@PageableDefault(size = 5, sort = "username") Pageable pageable){
		Page<Employee> all = empDataRepository.findAll(pageable);

		Page<EmployeeDto> map = all.map(EmployeeDto::new);

		return map;
	}
```

더 간단해졌다. 어떤 방식을 사용하던 개인적인 취향이지만 개인적으로 나는 첫 번째 방식을 선호한다. 명확하게 의미를 표시하는 느낌이어서 ... 



### EmpController.java

```java
// ... 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

// ...

@Controller
public class EmpController {

	private final EmpDataRepository empDataRepository;

	private final DeptDataRepository deptDataRepository;

	private final EntityManager em;

	public EmpController(EmpDataRepository empDataRepository,
							DeptDataRepository deptDataRepository,
							EntityManager em){
		this.empDataRepository = empDataRepository;
		this.deptDataRepository = deptDataRepository;
		this.em = em;
	}
  
  // ...

	@GetMapping("/v2/employees")
	@ResponseBody
	public Page<EmployeeDto> listV2(@PageableDefault(size = 5, sort = "username") Pageable pageable){
		Page<Employee> all = empDataRepository.findAll(pageable);

		Page<EmployeeDto> map = all.map(e -> {
			return new EmployeeDto(e);
		});

		return map;
	}

	@PostConstruct
	public void initData(){
		Department police = new Department("POLICE");
		deptDataRepository.save(police);

		for(int i=0; i<100; i++){
			final Employee e = new Employee("경찰관 #"+String.valueOf(i), 1000D, police);
			empDataRepository.save(e);
		}
	}
}
```



### URL

> [http://localhost:8080/v2/employees](http://localhost:8080/v2/employees)

  

### 응답결과

```json
{
    "content": [
        {
            "username": "경찰관 #0",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #1",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #10",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #11",
            "salary": 1000.0
        },
        {
            "username": "경찰관 #12",
            "salary": 1000.0
        }
    ],
    "pageable": {
        "sort": {
            "unsorted": false,
            "sorted": true,
            "empty": false
        },
        "offset": 0,
        "pageNumber": 0,
        "pageSize": 5,
        "paged": true,
        "unpaged": false
    },
    "last": false,
    "totalPages": 20,
    "totalElements": 100,
    "size": 5,
    "number": 0,
    "numberOfElements": 5,
    "first": true,
    "sort": {
        "unsorted": false,
        "sorted": true,
        "empty": false
    },
    "empty": false
}
```



### SQL

```sql
select 
	employee0_.emp_no as emp_no1_1_, 
	employee0_.dept_no as dept_no4_1_, 
	employee0_.salary as salary2_1_, 
	employee0_.username as username3_1_ 
from employee employee0_ 
order by employee0_.username asc 
limit 5;
```



## 별첨) 커스터마이징

> 페이징을 적용할 때 page의 기본 시작 index가 0 이다. page 를 1 부터 시작하도록 하려면 두가지의 해결책이 있다.

- Page, PageRequest, Page 등을 직접 커스터마이징하고, 이것들을 반환하도록 하는 방식이다. 
  - 가장 에러없이 잘 동작하는 해결책이다.
  - 프로젝트 진행시 규칙을 잘 정해놓고 한다면 가능한 방식이다.
- spring.data.web.pageable.one-indexed-parameters 를  true로 설정하는 방식
  - spring.data.web 계층에서 page를 단순 증감(-1) 처리하는 방식이다.
  - 한계가 있다. (pageable 및 나머지 필드들에는 적용이 안된다.)



