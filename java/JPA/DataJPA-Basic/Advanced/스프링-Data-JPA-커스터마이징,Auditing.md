# 스프링 Data JPA - 커스터마이징, Auditing


**예제 파일들**

- [github.com/study_archives/java/jpa/DataJPA-Basic/examples/erd_example](https://github.com/soongujung/study_archives/tree/master/java/JPA/DataJPA-Basic/examples/erd_example)
  - 아래에 정리하는 개념들에 대한 DataJPA 관련 모든 예제들  
    
- [github.com/example-querydsl-with-data-jpa](https://github.com/soongujung/example-querydsl-with-data-jpa)
  - QueryDsl 과 DataJPA 를 하나의 리포지터리에 통합적으로 제공하는 방식    

  


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

> 예제 : [github.com/study_archives/java/jpa/DataJPA-Basic/examples/erd_example](https://github.com/soongujung/study_archives/tree/master/java/JPA/DataJPA-Basic/examples/erd_example)

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

 

**예제**

- [github.com/study_archives/java/jpa/DataJPA-Basic/examples/erd_example](https://github.com/soongujung/study_archives/tree/master/java/JPA/DataJPA-Basic/examples/erd_example)

  

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



## 예제 4) 글쓴이, 수정한 사람을 Auditing 하기 - update시에는 null 로 지정

권장되는 방법은 아니다. 

> @EnableJpaAuditing(modifyOnCreate = false)

등록할 때에만 데이터를 입력하고, 수정할 때에는 null 로 지정하고자 할 때가 있다. 설정 파일의 클래스 구문 위에 추가했던 @EnableJpaAuditing 어노테이션에 위와 같이 `modifyOnCreate = false` 옵션을 추가해준다.  
modifyOnCreate 옵션에는 최초 등록시에 수정데이터를 반영할지 여부를 지정할 수 있다.  

- 참고
  - https://www.inflearn.com/questions/92759

- 예제
  - [github.com/study_archives/java/jpa/DataJPA-Basic/examples/erd_example](https://github.com/soongujung/study_archives/tree/master/java/JPA/DataJPA-Basic/examples/erd_example)

  

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

>  생성일, 수정일을 Auditing 하는 **MappedSupperClass**

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

> 생성일, 수정일, 글쓴이, 수정한 사람을 Auditing 하는 구문을 통합하여 제공하는 **MappedSupperClass**

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





