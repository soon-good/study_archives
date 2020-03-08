package com.study.qdsl.config.local;

import com.study.qdsl.entity.Member;
import com.study.qdsl.entity.Team;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
	Ch05 - item4 :: 조회 API 컨트롤러 개발
	 : profile 이 local, test 일 경우에 대해 환경 분리
	   (application.yml 따로 위치시킴)
	 : local 프로필 일 경우에 대해 샘플 데이터 넣도록 설정 추가
	 : 컨트롤러와 repository 연동
 */
@Profile("local")
@Component
@RequiredArgsConstructor
public class SampleDataComponent {

	private final InitSampleData initSampleData;

	@PostConstruct
	public void init(){
		initSampleData.init();
	}

	@Component
	static class InitSampleData{

//		@PersistenceContext
		@Autowired
		EntityManager entityManager;

		@Transactional
		public void init(){
			Team marketingTeam = new Team("Marketing");
			Team analysisTeam = new Team("Analysis");
			Team musicianTeam = new Team("Musician");
			Team nullTeam = new Team("NullTeam");

			entityManager.persist(marketingTeam);
			entityManager.persist(analysisTeam);
			entityManager.persist(musicianTeam);
			entityManager.persist(nullTeam);

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

			entityManager.persist(john);
			entityManager.persist(susan);
			entityManager.persist(kyle);
			entityManager.persist(stacey);
			entityManager.persist(aladin);
			entityManager.persist(genie);

			entityManager.persist(beethoven);
			entityManager.persist(chopin);
			entityManager.persist(genie2);
			entityManager.persist(nullName);
			entityManager.persist(ceo);
		}
	}
}
