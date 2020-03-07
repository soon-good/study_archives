package com.study.qdsl.advanced.ch04.projections.item03;

import static com.study.qdsl.entity.QMember.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.study.qdsl.dto.MemberDto;
import com.study.qdsl.dto.QMemberDto;
import com.study.qdsl.entity.Member;
import com.study.qdsl.entity.QMember;
import com.study.qdsl.entity.Team;
import java.util.List;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Ch03 - item3
 * @QueryProjection
 * 	: 어떻게 보면 궁극의 방법이지만, 단점 또한 가지고 있다.
 *
 * 	QType 생성
 * 	  1) DTO 의 생성자에 @QueryProjection 을 놓는다.
 * 	  2) Gradle 빌드 Task 중 other > compileQuerydsl 을 실행시켜야 한다.
 * 	  3) build 에 QMemberDto 가 생성되어 있는지 확인한다.
 *
 * 	QType 사용
 * 	  단순히 new Q타입생성자(....) 을 select() 메서드 안에 두면 된다.
 *
 * 	보완)
 * 	  근데 또 생각해보면 클래스 설계 시에 상속구조 또는 다른 방식으로 조금 다르게 분기하도록 하면될듯 하다.
 *
 * 	  지금 당장에 생각나는 방법 중 하나는
 * 	  MemberDto를 상속(확장)하는 하나의 클래스를 생성한다.
 * 	  	- 하나는 상속받은후 생성자에 @QueryProjection 을 걸면된다.
 */
@SpringBootTest
@Transactional
public class QdslQueryProjectionTest {

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
	 * Projections.constructor, fields, bean 과의 차이점
	 *  예)
	 *  	Projections 을 사용할 때
	 *  		: Projections.constructor (UserDto.class, member.username, member.age, member.id)
	 *  		  와 같이 UserDto 에 없는 필드인 member.salary같은 항목을 넣었을 때 런타임에 에러를 찾게 된다.
	 *  		: 실행할 때에야 에러를 찾을 수 있다는 점이 단점이다.
	 *
	 *  	QueryProjection 을 사용할 때
	 *  		: QMemberDto를 사용하기 때문에 컴파일 타임에 미리 에러를 잡을 수 있다.
	 *  		: QMemberDto(member.username, member.age, member.id) 와 같이 할 경우에 컴파일 타임에 미리 에러를 낸다.
	 *  		: 코딩시에 소스코드에서 Cmd + p 를 눌러서 바로 QType 생성자의 인자를 확인 가능하다.
	 *
	 *  단점)
	 *  	DTO 에 @QueryProjection 이 들어간다는 점이 아쉽다. compileQuerydsl을 적용해주어야 한다.
	 *		MemberDto 클래스가 QueryDsl 의존성을 가지게 된다.
	 *		만약 QueryDsl 라이브러리의 업그레이드 및 @QueryProjection의 Deprecated 가 될 경우 모든 소스코드에서 QueryProjection관련 코드를 제거해야 한다.
	 *
	 *		DTO의 경우
	 *			리포지터리에서도 조회한 후 사용하고
	 *			서비스에서도 사용하고
	 *			컨트롤러에서도 사용하고
	 *			심지어 API로 반환하기도 한다.
	 *
	 *			여러 레이어에 걸쳐서 이 Dto가 흘러다니게 된다.
	 *
	 *			이렇게 흘러다니는 Dto 안에 QueryProjection이 들어있다는것. QueryProjection에 의존적이다.
	 *
	 *
	 *	이런 이유로
	 *	DTO를 깔끔하게 가져가고 싶다는 생각으로 QueryProjection 을 사용하지 않고
	 *		Projections.fields, constructor, bean 을 사용한다.
	 *
	 *  애플케이션 전반적으로 Query에 강하게 의존적으로 되어 있고, QueryDsl의 하위 기술/스펙이 크게 바뀔것 같지 않다고 생각될때에는
	 * 		@QueryProjection을 사용하는 편이다.
	 */
	@Test
	public void dtoProjectionByQueryProjection(){
		List<MemberDto> data = queryFactory.select(
			new QMemberDto(member.username, member.age)
		)
		.from(member)
		.fetch();

		for(MemberDto m : data){
			System.out.println("memberDto :: " + m);
		}
	}
}

