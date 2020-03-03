package com.study.qdsl.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
public class Team {

	@Id @GeneratedValue
	private Long id;
	private String name;

	// Team <-> Member 의 연관관계를 당하는 입장 (거울...)
	// 연관관계의 주인이 아니기 때문에 외래키 값을 입력하지 않는다.
	@OneToMany(mappedBy = "team")
	private List<Member> members = new ArrayList<>();

	public Team(String name){
		this.name = name;
	}
}
