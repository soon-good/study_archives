# 벌크 연산과 SQL Function



# 벌크 연산

## 주의할 점

벌크연산은 메모리에 있는 영속성 객체들을 거치지 않고 바로 DB에 SQL을 보내 적용하는 기능이다. 벌크 연산을 수행 후에는 영속성 객체를 flush() 후 clear() 해줘야 한다는 점을 주의해야 한다. (벌크 연산 수행 후 flush, clear를 하지 않으면, 영속성 객체와 DB에 있는 데이터가 서로 맞지 않게 된다. 즉, JPA가 바라보는 데이터와 영속성이 잡고있는 데이터가 맞지 않는 문제.)  

(그림 추가하자)

> 벌크 연산은 영속성 컨텍스트를 무시하고 바로 DB로 직접 변경을 수행한다. 벌크 연산 후 select 쿼리 수행시 벌크연산이 적용된 값이 아닌 영속성 컨텍스트의 값이 보여진다. DB에서 select 해서 가져온 실 데이터 값이 있더라도, 이미 영속성 컨텍스트에 해당 값이 이미 있으면 DB에서 가져온 값을 버린다(무시한다).
>
> flush, clear를 해주지 않았을 경우 벌크 연산 후의 데이터 조회할 때 
>
> - DB 에는 벌크 연산 후의 값들이 적용되어 있지만
> - 영속성 컨텍스트 에는 이전 값들이 적용되어 있다.
>



## 벌크연산을 사용하는 경우

수정하려는 데이터의 양이 많을 경우  

수정하려는 1건의 데이터마다 여러 번의 쿼리 각각의 커밋을 단발성으로 수행하기보다는  

한번에 통으로 수정하는 쿼리를 만들어서 커밋을 하는 것이 효율적일 수 있다.



예를 들어, 

- 나이가 50세 이상
- 흡연자

인 보험가입자들에 대한 보험료를 일괄적으로 인상해야 하는 경우가 있다고 해보자. 해당 보험회사의 가입자는 500만 명이다. 이 경우 500만 건의 데이터를 일일이 수정해주는 것보다 한번에 통으로 업데이트를 하는 것이 나을 수도 있다.



이처럼, 벌크 연산을 하는 경우는 각각의 한 건마다 데이터를 수정하기보다는 대량으로 일괄 업데이트하는 것이 더 정확한 자료일 경우에 적합하다. 또는 batch 작업을 통한 집계성 update 작업에도 적합하다.  



## 예제 1)

모든 멤버들 중 24살 이하의 멤버의 이름을 "미성년자"로 변경  

```java
	@Test
	@Commit
	public void bulkUpdateTest1(){

		System.out.println("===== result before =====");
		List<Member> resultBefore = queryFactory.selectFrom(member)
			.fetch();

		for(Member m : resultBefore){
			System.out.println("member :: " + m);
		}

		System.out.println("");
		System.out.println("");

		System.out.println("===== bulk created... =====");
		long count = queryFactory.update(member)
			.set(member.username, "미성년자")
			.where(member.age.lt(24))
			.execute();
		System.out.println("");
		System.out.println("");

		/** flush(), clear()
		 * flush()를 해줘야 변경된 내용을 확인 가능하고
		 * clear()를 해줘야 남아있는 엔티티 중 깔끔하지 않게 남아있는 녀석들을 모두 지워준다.
		 **/

		em.flush();
		em.clear();

		assertThat(count).isEqualTo(2);

		System.out.println("===== result after =====");
		List<Member> result = queryFactory.selectFrom(member)
			.fetch();

		for(Member m : result){
			System.out.println("member :: " + m);
		}

	}
```



## 예제 2)



## 예제 3)





# SQL Function



# 벌크연산 테스트 코드 (전체코드)

```java
package com.study.qdsl.advanced.ch04.bulk;

import static com.study.qdsl.entity.QMember.*;
import static org.assertj.core.api.Assertions.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.qdsl.entity.Member;
import com.study.qdsl.entity.QMember;
import com.study.qdsl.entity.Team;
import java.util.List;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

/**
 * Ch03 - item6 :: 수정, 삭제 벌크 연산
 * 쿼리 한번으로 대량의 데이터를 수정하는 것을 보통 벌크 연산이라고 한다.
 *
 * JPA 는 엔티티를 가져온 후 엔티티의 값을 변경 후 트랜잭션을 커밋할 때 flush() 가 일어난다.
 *
 * 변경감지 (더티 체킹)
 * 엔티티의 값이 변경되었음을 감지(더티체킹)해서 엔티티가 변경되었을 때 update 쿼리를 수행한다.
 *
 * 이 변경감지(더티체킹)에 대해서는
 *  - 책을 읽어보거나,
 *  - https://interconnection.tistory.com/121 의 내용을 확인
 * 해서 조금 더 확실하게 정리할 예정.
 *
 * 한번에 대량의 데이터를 일괄적으로 수정할 때에는
 * 		여러번의 쿼리를 통해 커밋을 단발성으로 각각 수행하기 보다
 * 		한번에 수정하려는 쿼리를 만들어서 커밋을 하는 것이 효율적일 수 있다.
 *
 * 대량 벌크 연산으로 하는 연산은 대량으로 일괄 업데이트 하는 것이 더 정확한 자료일 경우에 적합하다.
 * (전직원 임금 50% 인상, 또는 배치(batch) 작업을 통한 집계성 update 연산 등)
 */
@SpringBootTest
@Transactional
public class QdslBulkOperationTest {

	@Autowired
	EntityManager em;

	JPAQueryFactory queryFactory;

	@BeforeEach
	public void before(){
		queryFactory = new JPAQueryFactory(em);

		Team marketingTeam = new Team("Marketing");
		Team analysisTeam = new Team("Analysis");
		Team musicianTeam = new Team("Musician");
		Team nullTeam = new Team("NullTeam");

		em.persist(marketingTeam);
		em.persist(analysisTeam);
		em.persist(musicianTeam);
		em.persist(nullTeam);

		Member john = new Member("John", 23, marketingTeam);
		Member susan = new Member("Becky", 22, marketingTeam);

		Member kyle = new Member("Kyle", 28, analysisTeam);
		Member stacey = new Member("Stacey", 24, analysisTeam);

		Member aladin = new Member("Aladdin", 35, analysisTeam);
		Member genie = new Member("Genie", 41, analysisTeam);

		Member beethoven = new Member("Beethoven", 251, musicianTeam);
		Member chopin = new Member("Chopin", 210, musicianTeam);
		Member genie2 = new Member("Genie", 210, musicianTeam);
		Member nullName = new Member(null, 100, musicianTeam);

		Member ceo = new Member("Jordan", 49, null);

		em.persist(john);
		em.persist(susan);
		em.persist(kyle);
		em.persist(stacey);
		em.persist(aladin);
		em.persist(genie);

		em.persist(beethoven);
		em.persist(chopin);
		em.persist(genie2);
		em.persist(nullName);
		em.persist(ceo);
	}

	/**
	 * 예제1)
	 * 	모든 멤버들 중 24살 이하의 멤버의 이름을 "미성년자" 로 변경
	 * 주의) 벌크 연산 수행 후에는 영속성 컨텍스트를 초기화해주는 것이 좋다.
	 */
	@Test
	@Commit
	public void bulkUpdateTest1(){

		System.out.println("===== result before =====");
		List<Member> resultBefore = queryFactory.selectFrom(member)
			.fetch();

		for(Member m : resultBefore){
			System.out.println("member :: " + m);
		}

		System.out.println("");
		System.out.println("");

		System.out.println("===== bulk created... =====");
		long count = queryFactory.update(member)
			.set(member.username, "미성년자")
			.where(member.age.lt(24))
			.execute();
		System.out.println("");
		System.out.println("");

		/** flush(), clear()
		 * flush()를 해줘야 변경된 내용을 확인 가능하고
		 * clear()를 해줘야 남아있는 엔티티 중 깔끔하지 않게 남아있는 녀석들을 모두 지워준다.
		 *
		 * 벌크 연산은 DB에 강제로 값을 반영하는 것이고
		 * 조회 연산은 영속성 컨텍스트의 값을 우선적으로 조회해온다.
		 * 	만약 select 연산시 DB의 실데이터가 이미 영속성 컨텍스트에 있을 경우 실 데이터는 버리고(무시하고) 영속성 컨텍스트의 값을 살린다.
		 **/

		em.flush();
		em.clear();

		assertThat(count).isEqualTo(2);

		System.out.println("===== result after =====");
		List<Member> result = queryFactory.selectFrom(member)
			.fetch();

		for(Member m : result){
			System.out.println("member :: " + m);
		}


		/** -- 적용 전
			member :: Member(id=5, username=John, age=23)
			member :: Member(id=6, username=Becky, age=22)
			member :: Member(id=7, username=Kyle, age=28)
			member :: Member(id=8, username=Stacey, age=24)
			member :: Member(id=9, username=Aladdin, age=35)
			member :: Member(id=10, username=Genie, age=41)
			member :: Member(id=11, username=Beethoven, age=251)
			member :: Member(id=12, username=Chopin, age=210)
			member :: Member(id=13, username=Genie, age=210)
			member :: Member(id=14, username=null, age=100)
			member :: Member(id=15, username=Jordan, age=49)
		*/
		/** flush, clear를 해주지 않았을 경우 벌크 연산 후의 데이터 조회할 때
		 		DB 에는 벌크 연산 후의 값들이 적용되어 있지만
		 		영속성 컨텍스트 에는 이전 값들이 적용되어 있다.

		 	벌크 연산은 영속성 컨텍스트를 무시하고 바로 DB로 직접 변경을 수행한다.
		 	벌크 연산 후 select 쿼리 수행시 벌크연산이 적용된 값이 아닌 영속성 컨텍스트의 값이 보여진다.
		 		DB에서 select해서 가져온 실 데이터 값이 있더라도, 이미 영속성 컨텍스트에 해당 값이 이미 있으면 DB 에서 가져온 값을 버린다(무시한다).
		 */

		/** -- 적용 후
			member :: Member(id=5, username=미성년자, age=23)
			member :: Member(id=6, username=미성년자, age=22)
			member :: Member(id=7, username=Kyle, age=28)
			member :: Member(id=8, username=Stacey, age=24)
			member :: Member(id=9, username=Aladdin, age=35)
			member :: Member(id=10, username=Genie, age=41)
			member :: Member(id=11, username=Beethoven, age=251)
			member :: Member(id=12, username=Chopin, age=210)
			member :: Member(id=13, username=Genie, age=210)
			member :: Member(id=14, username=null, age=100)
			member :: Member(id=15, username=Jordan, age=49)
		 */
	}

	/**
	 * 모든 멤버들의 나이를 현재 나이에서 100을 곱하기
	 * 그리고 멤버들의 이름 뒤에 +100 이라는 문자열을 더해주기
	 */
	@Test
	public void bulkUpdateTest2(){
		long count = queryFactory.update(member)
			.set(member.age, member.age.multiply(100))
			.set(member.username, member.username.concat("+100"))
			.execute();

		em.flush();
		em.clear();

		List<Member> data = queryFactory.selectFrom(member)
			.fetch();

		for(Member m : data){
			System.out.println("member :: " + m);
		}
	}

	/**
	 * 삭제연산
	 * 모든 멤버 들중 나이가 23살 보다 많은(또는 24살 이상인) 모든 회원들의 데이터를 지워라.
	 */
	@Test
	public void bulkUpdateTest3(){
		long count = queryFactory.delete(member)
			.where(member.age.gt(23))
			.execute();

		em.flush();
		em.clear();

		List<Member> members = queryFactory.selectFrom(member)
			.fetch();

		for(Member m : members){
			System.out.println("member :: " + m);
		}
	}

}


```





