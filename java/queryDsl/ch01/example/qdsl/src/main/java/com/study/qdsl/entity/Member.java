package com.study.qdsl.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @NoArgsConstructor
 * 	jpa는 기본 생성자가 있어야 한다.
 * 	jpa 기본스펙에서는 기본 생성자를 protected 레벨까지는 허용해준다.
 *
 * @ToString
 * 	ToString 에서는 연관관계를 가지는 필드는 포함하지 않아야 한다.
 * 	Team 처럼 Mapping 되어 있는 것을 ToString에 넣어주면 Team에 갔다가 Member갔다가 하면서 무한루프에 빠지게 되므로
 * 	되도록 연관관계 매핑 필드에는 ToString을 사용하지 않도록 주의해야 한다.
 */
@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of={"id", "username", "age"})
public class Member {

	@Id @GeneratedValue
	@Column(name = "member_id")
	private Long id;
	private String username;
	private int age;

	// Team <-> Member 연관관계의 주인
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")	// 외래키 컬럼 명
	private Team team;

	public Member(String username){
		this(username, 0);
	}

	public Member(String username, int age){
		this(username, age, null);
	}
	
	public Member(String username, int age, Team team){
		this.username = username;
		this.age = age;
		if(team != null){
			changeTeam(team);
		}
	}

	public void changeTeam(Team team) {
		this.team = team;
		team.getMembers().add(this);
	}
}
