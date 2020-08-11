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



# 2. Auditing

엔티티의 생성/변경 시점에 

- 등록일
- 수정일
- 등록자
- 수정자

를 기록으로 남겨놓을 수 있다. 엔티티마다 이러한 내용들을 남겨놓으면 추후 운영단계에서 번거로움을 많이 덜어낼 수 있다.  

상용서비스의 경우 가끔 과거 이력을 추적하고, 이력으로 남겨야만 법적인 문제가 되지 않는 경우도 있다. 뭐라는거지? 일단 정리 고고싱  



## 예제1) 순수 JPA - 등록일/수정일

### JpaBaseEntity

```
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
  private LocalDateTime updatedDate;

  @PrePersist
  public void prePersist(){
    LocalDateTime now = LocalDateTime.now();
    createdDate = now;  // 데이터 초기 생성시 현재 시점의 시간을 지정
    updatedDate = now;  // 데이터 초기 생성시 현재 시점의 시간을 지정
//    updatedDate = null;
//    디폴트 데이터를 null 로 지정하게 되면 추후 불편해지기도 한다.
  }

  @PreUpdate
  public void preUpdate(){
    updatedDate = LocalDateTime.now();
  }
}
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

//  public Employee(){}

  public Employee (String username, Double salary, Department dept){
    this.username = username;
    this.salary = salary;
    this.dept = dept;
    dept.getEmployees().add(this);
  }

}
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
    empDataRepository.save(employee);   // save 메서드는 순수 JPA를 사용하기에는 시간이 많지 않아서... Data JPA를 썼다...
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
createDate  :: 2020-08-10T23:24:03
updatedDate :: 2020-08-10T23:24:04
```

- 순수 JPA를 사용할 때

  - @PrePersise @PreUpdate 에서 프로그래머가 직접 작성하게 될때 부주의하게 실수할 수 있는 부분이 있다.

- Data JPA를 사용할 때

  - 내부에 내재화 되어 있다는 것이 다른 점으로 보인다.  

  - 아래와 같은 중복될 수밖에 없는 코드들을 공통화한 @CreatedDate, @LastModifiedDate 어노테이션을 사용한다.

    ```
    LocalDateTime now = LocalDateTime.now();
    createdDate = now;
    ```

    

  Spring Data JPA 기반으로 Auditing 을 사용하는 한가지 장점이 있다.

  - 등록자, 수정자를 AuditorAware 와 연동하여 @CreatedBy, @LastModifiedBy 등으로 지정해줄 수 있다는 점이다.

    

  Spring Data JPA 기반으로 Auditing 엔티티를 작성할 때 사용하는 어노테이션을 정리해보면 아래와 같다.

  - @EnableJpaAuditing
    - 스프링 부트 설정 클래스에 적용
  - @MappedSuperclass
    - 엔티티에 적용
  - @EntityListeners(AuditingEntityListener.class)
    - 엔티티에 적용
  - @CreatedDate
    - 엔티티 생성 시점 
    - org.springframework.data.annotation
  - @LastModifiedDate
    - 엔티티 수정 시점
    - org.springframework.data.annotation

  

  ### DataJpaBaseEntity.java

  ```
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

  ```
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
  
  //  public Employee(){}
  
    public Employee (String username, Double salary, Department dept){
      this.username = username;
      this.salary = salary;
      this.dept = dept;
      dept.getEmployees().add(this);
    }
  
  }
  ```

  ```
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

  ```
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

  ```
  createDate  :: 2020-08-11T21:11:50
  updatedDate :: 2020-08-11T21:11:51
  ```

  ```
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
  
    // 샘플 예제를 위한 fake 데이터 생성
    // UUID를 이용해 난수를 생성했다.
    // 보통은 SecurityContextHolder 를 이용해 현재 세션의 사용자를 얻어내는 편이다.
    // SecurityContextHolder 를 이용해 현재 세션의 사용자를 얻어냈을 때 아래의 람다 구문의
    // Optional.of( ... ) 메서드 내에 해당 세션에 대한 요청 ID를 넣어주면 된다.
    @Bean
    public AuditorAware<String> auditorAware(){
      return () -> Optional.of(UUID.randomUUID().toString());
    }
  
  }
  
  ```

  ```
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

  ```
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

  ```
  createDate  :: 2020-08-11T21:29:05
  updatedDate :: 2020-08-11T21:29:06
  createdBy :: 8566f8cf-7d63-4172-b7c2-7cbd9ebb62f6
  updatedBy :: e19fbedc-1a39-4241-83ea-2c440e3c29c0
  ```

  ```
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

  ```
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

  ```
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

  ```
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

  ```
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
  
  //  public Employee(){}
  
    public Employee (String username, Double salary, Department dept){
      this.username = username;
      this.salary = salary;
      this.dept = dept;
      dept.getEmployees().add(this);
    }
  
  }
  ```

  

  ### Employee.java

  ### ErdBaseEntity.java

  ### ErdBaseTimeEntity.java

  로 분리하는 방식을 용도에 맞게 각 Entity 클래스에서 상속받아 사용하면 분리가 간편해진다. 

  - ErdBaseTimeEntity
    - 생성일, 수정일을 관리하는 클래스
  - ErdBaseEntity
    - 생성일, 수정일
    - 작성자, 수정자
    - 네가지 필드 모두를 관리하는 클래스
    - ErdBaseTimeEntity 클래스를 상속받은 클래스

  ## 예제 6) 상속관계로 용도에 따라 분리

  

  @EntityListeners(AuditingListener.class) 를 BaseEntity 마다 지정해주기는 조금 번거로울 경우가 있다. 이 경우 src/resources/META-INF/ 밑에 아래와 같이 orm.xml 파일을 작성해준다.

  ## 예제 5) @EntityListeners 대신 전역적으로 Auditing을 강제로 지정할 경우

  

  ### 예제

  위와 같은 어노테이션을 추가해준다.

  > @EnableJpaAuditing(modifyOnCreate = false)

  권장되는 방법은 아니다. 등록할 때에만 데이터를 입력하고, 수정할 때에는 null 로 지정하고자 할 때가 있다. 설정 파일위에 작성한 @EnableJpaAuditing 어노테이션에 아래와 같이 추가해준다. (이번 예제에서는 ErdApplication.java, 보통 @EnableJpaAuditing 은 설정 클래스에 따로 분리해놓는 편이다. 명심하자.)

  ## 예제 4) Spring Data JPA - 등록자, 수정자 (2) :: update시에는 null 로 지정

  

  ### 출력결과

  

  ### EmpDataRepositoryTest.java

  

  - @CreatedBy

    - 등록자
    - 보통은 SecurityContextHolder에서 가져온 현재 요청을 보낸 사용자에 대한 아이디를 얻어와 지정해준다.
    - 아이디를 얻어와 지정해주는 로직은 설정으로 따로 분리해놓았는데, 바로 앞전에서 본 AuditingAware 함수를 @Bean으로 등록하는 구문에서 등록하고 있다.
    - 이번 예제에서는 등록자에 단순히 난수를 등록해주고 있다.

  - @LastModifiedBy

    - 수정자
    - 보통은 SecurityContextHolder에서 가져온 현재 요청을 보낸 사용자에 대한 아이디를 얻어와 지정해준다.
    - 아이디를 얻어와 지정해주는 로직은 설정(Configuration 또는 Bean)으로 따로 분리해놓았는데, 바로 앞전에서 본 AuditingAware 함수를 @Bean으로 등록하는 구문에서 등록하고 있다.
    - 이번 예제에서는 수정자에 단순히 난수를 등록해주고 있다.

    

  ### DataJpaBaseEntity.java

  

  - 이번 예제에서는 UUID를 이용해 난수를 생성했다.
    - 스프링 시큐리티 설정까지 정리하기엔 예제가 너무 TMI가 되어 한눈에 보기 어려워질것 같다는 생각에 이렇게 정리.
  - 보통은 SecurityContextHolder 를 이용해 현재 세션의 사용자를 얻어내는 편이다.
  - SecurityContextHolder 를 이용해 현재 세션의 사용자를 얻어냈을 때 아래의 람다 구문의 Optional.of( ... ) 메서드 내에 해당 세션에 대한 요청 ID를 넣어주면 된다.
  - 아이디를 얻어와 지정해주는 로직은 설정(Configuration 또는 Bean)으로 따로 분리해놓았는데, 여기서는 @Bean으로 등록했다.

  ### ErdApplication.java

  ## 예제3) Spring Data JPA - 등록자, 수정자 (1)

  

  ### 출력결과

  

  ### Test :: EmpDataRepositoryTest.java

  

  왠만하면 깔끔하게 @Configuration 어노테이션과 함께 @EnableJpaAudting을 추가해서 설정 파일을 만들자. 여기서는 단순예제를 빨리 만들어보기 위해 main() 문을 가지고 있는 Spring Boot Application 클래스에 해당 설정 어노테이션을 추가해주었다. (귀찮다는 핑계이긴 하다.)

  ### ErdApplication.java

  

  순수 JPA 기반 Auditing 예제에서는 Employee 클래스가 JpaBaseEntity를 상속하도록 했었다. Data JPA 기반 Auditing 예제에서는 DataJpaBaseEntity 를 상속받도록 하자.

  ### Employee.java

  

  - @EntityListeners(AuditingEntityListener.class)
    - 정리하자. 정리열매~!!!
  - @CreatedDate
    - 엔티티 생성 시점 지정
  - @LastModifiedDate
    - 엔티티 수정 시점 지정

  Data JPA를 사용할 때 사용하는 어노테이션은 아래와 같다.

순수 JPA를 사용할 때에 비해 코드가 짧아지거나 간결해지진 않는다. 작성하는 코드의 양은 비슷하다. 다만, 차이점은 아래와 같다.

## 예제2) Spring Data JPA 를 사용할 때



출력결과

### Test :: EmpRepositoryTest



Employee 엔티티는 방금 작성한  JpaBaseEntity 클래스를 상속받은 것 외에는 따로 해준 것이 없다.

### Employee.java

- @MappedSuperclass
  - Entity 클래스 (e.g. Employee, Department 등등) 가 상속받아서 사용한다.
  - 우리가 작성한 JpaBaseEntity라는 이름의 클래스는 Employee 엔티티에서 사용하기 위해서는 @MappedSuperclass 라는 어노테이션을 사용해야만 엔티티가 JpaBaseEntity 타입의 클래스를 인식가능하다.
  - 자세한 원리는 JPA 기본편에서 자세하게 설명해준다.
- @Column(updatable = false)
  - 데이터의 초기 생성시점은 변경되면 안된다는 조건을 걸고 싶을 때 사용한다.
  - 과거 이력에 대해서 수정을 하는 것이 조작이라고 판단되는 법적인 문제?도 있지 않은가... 또 헛소리를 했다. 얼ㄹ른 자야할 듯.
  - 초기 생성 시점에 대한 기록이 자주 변경된다면 조회상으로도 모호함을 낳기 때문에 좋지 않기 때문에 createdDate와 같은 필드에는 가급적 @Column(updatable = false)를 사용하는 것을 권장하는 편이다.
  - @Column(updatable = false, insertable = true) 와 같이 지정할 수 도 있다.
- @PrePersist
  - Persist, 즉 DB에 저장하기 직전에 호출되도록 하고 싶은 메서드에 @PrePersist 를 지정해준다.
- @PreUpdate
  - Entity 에 대해 Update 동작이 발생할 때에 수행할 동작을 기술한 메서드에 @PreUpdate 를 지정해준다.



# 3. 도메인 클래스 컨버터

  



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

