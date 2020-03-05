package com.study.qdsl.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

/**
 * @Transactional
 *  : Test에 적용하면 테스트 시작시에 트랜잭션을 시작하기도 하지만, 추가로 테스트가 끝나고 트랜잭션을 자동으로 롤백해주는 기능을 수행한다.
 *  : @Transactional을 테스트 코드에 사용하면 해당 테스트가 끝나고, DB의 데이터를 롤백해서 깔끔하게 초기화 해준다.
 *  : 다른 테스트에 영향을 주지 않고, 다음 테스트를 계속 어이갈 수 있는 장점이 있다.
 *
 * @Commit
 *  : DB에 Commit을 하게되면 다른 테스트들과 꼬일수 있기 때문에 git에 커밋/푸시 시에는 주석처리
 *  : H2 Database Console에서는 당연히 안보이긴 하지만, 로그나, 콘솔에서만 확인하고, 다른 테스트와 꼬이지 않기 위해 주석처리
 */
@SpringBootTest
@Transactional
//@Commit
class MemberTest {

	@Autowired
	EntityManager em;

	@Test
	public void testEntity(){
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");

		em.persist(teamA);
		em.persist(teamB);

		Member member1 = new Member("member1", 10, teamA);
		Member member2 = new Member("member2", 20, teamA);

		Member member3 = new Member("member3", 30, teamB);
		Member member4 = new Member("member4", 40, teamB);

		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);

		// 초기화
		em.flush(); // 영속성 컨텍스트에 있는 객체들을 직접 쿼리를 만들어 DB에 적용하는 단계
		em.clear(); // 영속성 컨텍스트(메모리)에 있는 캐시들을 지운다. 그 다음 쿼리를 수행시 깔끔한 상태로 진행될 수 있도록 해주는 작업

		// 영속성 컨텍스트의 데이터 확인
		List<Member> members = em.createQuery("select m from Member m", Member.class)
			.getResultList();

		for (Member member : members){
			System.out.println("member = " + member);
			System.out.println("-> member.team" + member.getTeam());
		}
	}
}