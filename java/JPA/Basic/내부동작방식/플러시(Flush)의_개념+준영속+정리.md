# * 참고) 개념 정리 - em ? PersistContext?

- em.detach
- em.clear
- em.close
- em.flush

의 의미를 자세히 보자. em.flush라는 것은 EntityManager 내부를 flush 하겠다는 의미이다. 그런데 ... EntityManager의 경우 1:N 의 형태를 띤 J2EE 에서는하나만 있지 않다. 그러면 영속성 컨텍스트는 여러개인가?...  10명의 사용자가 동시요청을 했을때 10명의 사용자 모두가 각기 다른 em을 사용한다고 한다.

의미를 잘 생각해봐야 한다. PersistContext는 논리적 개념이라고 했다. 이말의 의미는 변경된 부분이 있는지 없는지를 검사를 하는 문맥이라는 것으로 보인다. 함수로 뭔가 장치를 해놓은 것이 아니라 EntityManager 내부마다 하나씩 존재하는 것이 PersistContext이고, 이 PersistContext라는 것은 눈에 띄는 개념이 아니라 변화된 부분이 있는지 없는지 검사하는 조건식이라는 의미로 보인다. 



# 9. 플러시 (Flush)의 개념

> 플러시는 영속성 컨텍스트의 변경 내용을 데이터베이스에 반영하는 역할을 한다.  



## 플러시가 발생했을 때의 내부 동작

> 참고) 플러시는 영속성 컨텍스트의 변경된 내용을 실제 DB에 반영하는 작업이다.  

- Flush를 발생시키면 변경감지를 하게 된다.
- 변경된 내용이 감지되었다면 수정된 엔티티를 쓰기 지연 SQL 저장소에 등록한다.
- 쓰기 지연 저장소의 SQL을 Database로 날린다.
  
  - (DB에 해당 내용을 반영하게 된다.)
- 플러시가 발생한다고 해서 실제 DB로의 커밋이 발생하는 것은 아니다.

  

## 영속성 컨텍스트를 플러시하는 방법들

- em.flush()
  - 직접 호출하는 방식이다.
- transaction.commit()
  - transaction.commit() 을 수행시 flush 도 호출되게 된다.
- JPQL 쿼리 실행
  - 플러시가 자동으로 호출된다.



## 예제) 

### MainApp.java

```java
public class MainApp {
	public static void main(String [] args){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa_basic");
		EntityManager em = emf.createEntityManager();

		EntityTransaction transaction = em.getTransaction();
		transaction.begin();

		try{
			// Employee 객체를 만들어서 저장해보자.
			System.out.println("======= em.persist (emp100) =======");
			Employee emp100 = new Employee(100L, "경찰관#100", 1000.0D, 200.0D);
			em.persist(emp100);

			// Server Console 상 에서는 여기서 insert SQL 이 나가게 된다.
			System.out.println("======= em.flush() =======");
			em.flush();

			// 실제 Transaction 이 DB에 commit 되는 시점이다.
			System.out.println("======= transaction.commit() =======");
			transaction.commit();
		}
		catch (Exception e){
			transaction.rollback();
		}
		finally{
			em.close();
		}
		emf.close();
	}
}
```



### 실행결과

실행 결과를 자세히 보면

- em.persist 
  
  - 엔티티 생성작업을 수행중이다. 이게 영속성 컨텍스트 안에서 생성하는 것인지 알 수는 없다. 
- em.flush
  - 더티체킹을 수행한다.
  - 변경된 것을 감지했는데, Insert 해야하는 것이 하나 있음을 파악했다.
  - 실제 insert SQL을 실행한다.
- transaction.commit()
  - 실제 insert SQL을 커밋하는 과정이다.  

  

```text
17:53:49.336 [main] DEBUG org.hibernate.engine.transaction.internal.TransactionImpl - begin
======= em.persist (emp100) =======
### 엔티티를 생성해냈다.
17:53:49.341 [main] DEBUG org.hibernate.event.internal.AbstractSaveEventListener - Generated identifier: 100, using strategy: org.hibernate.id.Assigned

======= em.flush() =======
17:53:49.350 [main] DEBUG org.hibernate.event.internal.AbstractFlushingEventListener - Processing flush-time cascades
### 더티체킹 
17:53:49.350 [main] DEBUG org.hibernate.event.internal.AbstractFlushingEventListener - Dirty checking collections
### INSERT 사항 감지
17:53:49.351 [main] DEBUG org.hibernate.event.internal.AbstractFlushingEventListener - Flushed: 1 insertions, 0 updates, 0 deletions to 1 objects
17:53:49.351 [main] DEBUG org.hibernate.event.internal.AbstractFlushingEventListener - Flushed: 0 (re)creations, 0 updates, 0 removals to 0 collections


17:53:49.352 [main] DEBUG org.hibernate.internal.util.EntityPrinter - Listing entities:
17:53:49.352 [main] DEBUG org.hibernate.internal.util.EntityPrinter - jpabasic.entity.Employee{bonus=200.0, name=경찰관#100, id=100, salary=1000.0}

### INSERT 수행
17:53:49.356 [main] DEBUG org.hibernate.SQL - 
    /* insert jpabasic.entity.Employee
        */ insert 
        into
            EMP
            (d_bonus, v_name, d_salary, id) 
        values
            (?, ?, ?, ?)
Hibernate: 
    /* insert jpabasic.entity.Employee
        */ insert 
        into
            EMP
            (d_bonus, v_name, d_salary, id) 
        values
            (?, ?, ?, ?)
            
### INSERT SQL을 커밋하는 과정이다.
======= transaction.commit() =======
### transaction.commit 
17:53:49.385 [main] DEBUG org.hibernate.engine.transaction.internal.TransactionImpl - committing
...
```



## JPQL 실행시 flush 가 자동으로 호출되는 이유??

> 생각해보면 간단하다. em.find(...) 구문으로 데이터를 조회할 때에는 
>
> - 이미 메모리 상에 해당 엔티티 데이터가 있거나
> - 메모리상에 데이터가 없어서 SELECT SQL을 호출하게 된다. 
>
> 그런데 JPQL의 경우는 단순 SQL을 static 하게 컴파일 타임에 컴파일이 된 쿼리 객체이다. 이 JPQL이라는  친구는 
>
> - 메모리 상에 엔티티 데이터가 있는지, 없는지를 
>
> 호출 시점에 판단할 수 가 없다. (원리상으로 보면 JDBC의  PreparedStatement 등과 유사한 역할인것으로 보인다.) 호출해서 데이터를 들고온 후에는 JPA가 영속성컨텍스트를 구성할 때 판단을 할 수는 있다.  
>
> 이렇게 생각해보면... JPQL은 단순 쿼리 문자열이기 때문에 변경감지 또는 데이터 유무를 판단하지 못한다. 따라서 그 건에 미리 em.flush()를 해놓아야 한다. 이런 이유 때문인지 JPQL 쿼리를 실행하는 시점에 em.flush()가 호출된다고 한다. 

### 예제

```java
em.persist(emp1);
em.persist(emp2);
em.persist(emp3);

// 중간에 JPQL 실행
query = em.createQuery("select e from Employee e", Employee.class);
List<Employee> employees = query.getResultList();
```



- em.persist() 로 emp1, emp2, emp3 를 영속화 했다. 그런데 아직 flush는 하지 않았다. 
- 이후 JPQL 쿼리를 날린다.
  - 이때 flush가 발생한다.

이렇게 JPQL 실행시 자동으로 flush가 발생하는 이유는 persist 해놓고 SQL로 생성되지 않고, DB에 반영되지 않은 값들이 있을 경우 데이터 조회시 문제가 생기는 경우가 있을 수 있기 때문에 이를 미연에 방지하기 위해... JPQL 실행시에는 아예 flush를 해놓고 시작한다고 한다. (데이터의 애매모호함이 발생할 수 있는 요인을 미연에 차단)  

  

## 플러시 모드 옵션

> 실제로 사용하게 될 일은 없다고 한다.  

ex) 

```java
em.setFlushMode(FlushModeType.COMMIT);
```



- FlushModeType.AUTO
  - JPA의 기본값.
  - 커밋/쿼리 실행시 플러시를 하도록 하는 것
- FlushModeType.COMMIT
  - 커밋할 경우에만 플러시를 하도록 구성



## 자주 혼동하는 개념

### flush 를 수행하고 나면 1차 캐시에 있는 내용들이 모두 지워지나요??

아니다. 단순히 쓰기 지연 저장소에 있는 SQL 들이 실 DB에 반영이 되기만 한다.



### em.find를 해서 데이터를 가져올 때

em.find 를 해서 데이터를 가져왔다. 그런데 이 데이터가 1차 캐시에 없다. 이렇게 조회를 해서 데이터를 가져왔는데 1차캐시에 데이터가 없을 경우에는 1차캐시에 해당 데이터를 저장한다. 1차 캐시에 올린 상태가 영속화 상태이다. JPA가 관리하는 상태가 된 것을 영속화 상태라 한다.

  

## 정리

- 플러시는 영속성 컨텍스트를 비우지 않는다.
- 영속성 컨텍스트의 변경내용을 데이터베이스에 동기화하는 작업이다.
- transaction 이라고 하는 작업단위가 중요하다. 
  - 커밋 직전에만 동기화하면 된다.



# 10. 준영속 상태

영속상태의 엔티티가 영속성 컨텍스트에서 분리(detached)된 상태를 이야기한다. 준영속 상태에서는 영속성 컨텍스트가 제공하는 기능을 사용할 수 없다.  

## 준영속 상태로 만드는 방법

- em.detach(emp1)
  - 영속성 컨텍스트에서 emp1 객체만 떼어내서 분리한다.
- em.clear()
  - 영속성 컨텍스트 전체를 비운다.
- em.close()
  - 영속성 컨텍스트를 종료



## 예제) em.detach() 로 준영속상태 만들어보기

### MainApp.java

```java
package jpabasic;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import jpabasic.entity.Employee;

public class MainApp {
	public static void main(String [] args){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa_basic");
		EntityManager em = emf.createEntityManager();

		EntityTransaction transaction = em.getTransaction();
		transaction.begin();

		try{
			// @Id가 100L 인 데이터를 가져와 보자.
			System.out.println("======= em.find (Employee.class, 100L) =======");
			Employee emp100 = em.find(Employee.class, 100L);

			// emp100의 이름을 변경해보자
			System.out.println("======= emp100.setName... 엔티티 내부 값 변경 =======");
			emp100.setName("소방관#100");

			// 영속성 컨텍스트에서 해제해보자.
			System.out.println("======= em.detach() =======");
			em.detach(emp100);

			System.out.println("======= transaction.commit() =======");
			transaction.commit();
		}
		catch (Exception e){
			transaction.rollback();
		}
		finally{
			em.close();
		}
		emf.close();
	}
}
```

- Employee emp100 = em.find(Employee.class, 100L);
  - Id 가 100인 엔티티를 꺼내왔다.
- emp100.setName("소방관#100");
  - 엔티티의 name 필드를 변경했다.
  - 이어지는 뒤의 구문들에서 별다른 행동만 안한다면 변경감지가 일어날것이라 기대될 수 있다.
- em.detach(emp100)
  - emp100을 영속성 컨텍스트에서 지웠다.
  - 영속성 컨텍스트에서 emp100을 분리해냈다.
  - 즉, 트래킹하지 않겠다는 의미.
- transaction.commit()
  - 트랜잭션을 커밋한다.
  - 이때 준영속 상태로 만든 emp100은 변경감지가 되지 않아 커밋될 데이터도 아닐 뿐더러
  - 변경감지의 대상도 아니다.
  - 따라서 아무일도 일어나지 않는다.



### 출력결과

```text
9:11:28.927 [main] DEBUG org.hibernate.engine.transaction.internal.TransactionImpl - begin
======= em.find (Employee.class, 100L) =======
...
Hibernate: 
    select
        employee0_.id as id1_0_0_,
        employee0_.d_bonus as d_bonus2_0_0_,
        employee0_.v_name as v_name3_0_0_,
        employee0_.d_salary as d_salary4_0_0_ 
    from
        EMP employee0_ 
    where
        employee0_.id=?
...
org.hibernate.loader.entity.plan.AbstractLoadPlanBasedEntityLoader - Done entity load : jpabasic.entity.Employee#100
======= emp100.setName... 엔티티 내부 값 변경 =======
======= em.detach() =======
======= transaction.commit() =======
19:11:28.955 [main] DEBUG org.hibernate.engine.transaction.internal.TransactionImpl - committing
```



## 예제) em.clear() 로 준영속상태 만들어보기

### MainApp.java

```java
public class MainApp {
	public static void main(String [] args){
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpa_basic");
		EntityManager em = emf.createEntityManager();

		EntityTransaction transaction = em.getTransaction();
		transaction.begin();

		try{
			// @Id가 100L 인 데이터를 가져와 보자.
			System.out.println("======= em.find (Employee.class, 100L) =======");
			Employee emp100 = em.find(Employee.class, 100L);

			// emp100의 이름을 변경해보자
			System.out.println("======= emp100.setName... 엔티티 내부 값 변경 =======");
			emp100.setName("소방관#100");

			// 현재 영속성 컨텍스트인 em 을 비워보자.
			System.out.println("======= em.clear() =======");
			em.clear();

			System.out.println("======= transaction.commit() =======");
			transaction.commit();
		}
		catch (Exception e){
			transaction.rollback();
		}
		finally{
			em.close();
		}
		emf.close();
	}
}
```

- Employee emp100 = em.find(Employee.class, 100L);
  - Id 가 100인 엔티티를 꺼내왔다.
- emp100.setName("소방관#100");
  - 엔티티의 name 필드를 변경했다.
  - 이어지는 뒤의 구문들에서 별다른 행동만 안한다면 변경감지가 일어날것이라 기대될 수 있다.
- em.clear()
  - 현재 영속성 컨텍스트인 em을 비웠다.
  - 현재 프로그램에서는 EntityManager를 Pool로 관리하는 등의 정교한 기법을 사용하지 않고 있다. EntityManager em 객체 하나가 영속성 컨텍스트의 역할도 모두 담당하고 있다. 이런 이유로 em.clear()는 영속성 컨텍스트 전부를 비우는 작업이다.

- transaction.commit()
  - 트랜잭션을 커밋한다.
  - JPA 는 1차 캐시를 비웠기 때문에 변경할 데이터가 아무것도 없는 것으로 판단한다.
  - 변경감지의 대상도 아니다.
  - 따라서 아무일도 일어나지 않는다.
  - 영속성 컨텍스트는 다음번 find 구문 등에 의해 새롭게 다시 채워질 것이다.

### 출력결과

```text
19:29:32.693 [main] DEBUG org.hibernate.engine.transaction.internal.TransactionImpl - begin
======= em.find (Employee.class, 100L) =======
...
Hibernate: 
    select
        employee0_.id as id1_0_0_,
        employee0_.d_bonus as d_bonus2_0_0_,
        employee0_.v_name as v_name3_0_0_,
        employee0_.d_salary as d_salary4_0_0_ 
    from
        EMP employee0_ 
    where
        employee0_.id=?
...
19:29:32.716 [main] DEBUG org.hibernate.loader.entity.plan.AbstractLoadPlanBasedEntityLoader - Done entity load : jpabasic.entity.Employee#100
======= emp100.setName... 엔티티 내부 값 변경 =======
======= em.clear() =======
======= transaction.commit() =======
```



# 11. 정리

## JPA 에서 가장 중요한 2가지

- 객체와 관계형 데이터베이스 매핑하기 (Object Relational Mapping)
- 영속성 컨텍스트



## 영속성 컨텍스트란??

엔티티를 persist(영속화)하는 환경 이라는 뜻.

변경감지를 해서 데이터 상태에 대한 문맥판단을 하는 버퍼역할



## 엔티티 매니저?? 영속성 컨텍스트??

- 영속성 컨텍스트는 논리적인 개념
- 엔티티 매니저로 영속성 컨텍스트에 접근한다.



### J2SE 환경

> EntityManager : PersistContext = 1 : 1

Main 문에 예제로 작성했던 우리의 예제는 EntityManager와 PersistContext가 1:1 대응 관계였다.  



### J2EE, 스프링 프레임워크 같은 컨테이너 환경

> EntityManager : PersistContext = N : 1  

하나의 PersistContext에 대해 여러 개의 EntityManager가 대응된다.



## 영속성 컨텍스트의 이점

- 1차 캐시
  - 한번 조회한 것을 또 조회하면 DB에 쿼리가 나가지 않는다. 
  - 만약 10명의 고객이 접근할 경우를 예로 들면, 이 경우 모두 별도의 em을 가지고 있는다. 
  - 따라서 성능상으로 얻을수 잇점이 그리 크지 않다. 
- 동일성(identity) 보장
- 트랜잭션을 지원하는 쓰기 지연 (transactional write-behind)
- 변경 감지 (Dirty Checking)
- 지연 로딩 (Lazy Loading)
  - 1:N 등의 매핑에서 N에 대한 것을 나중에 로딩하는 것 
  - (조인이 엮일 수 밖에 없는 엔티티 매핑일 경우에 대한 이야기)



## 플러시 모드 옵션

> em.setFlushMode(FlushModeType.COMMIT)

### FlushModeType.AUTO

- JPA 기본값

### FlushModeType.COMMIT

- 왠만하면 하지말자. 
- 커밋할 때만 플러시를 하는 기능이다.
  - 즉, em.clear(), JPQL을 통한 객체 조회 등에서 flush()가 안먹는 것으로 보인다.
  - 확인되지 않은 기능이기 때문에 고급 속성을 거드릴 이유가 없다.



## 플러시

- 영속성 컨텍스트를 비우는 것이 아니다.

- 영속성 컨텍스트를 비우는 역할은 em.clear()

- 영속성 컨텍스트의 변경내용을 데이터베이스에 동기화

- 트랜잭션이라는 작업단위가 중요하다. 

  - 커밋 직전에만 동기화하면 된다.
  - 영속성컨텍스트와 트랜잭션의 주기를 맞춰서 설계가 되어야 한다.
  - 트랜잭션을 시작해서 끝날 때에 영속성 컨텍스트도 거기서 끝나도록 설계가 되어야한다.

  

## 준영속 상태

- 영속상태의 엔티티를 영속성 컨텍스트에서 꺼내서 분리(detach)해내는 것
- em.detach
- em.clear
- em.close

