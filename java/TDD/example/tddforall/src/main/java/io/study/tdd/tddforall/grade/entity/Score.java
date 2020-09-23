package io.study.tdd.tddforall.grade.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Builder
@Table(name = "SCORE")
public class Score {
	@Id @GeneratedValue
	@Column(name = "SCORE_ID")
	private Long id;

	private String subject;
	private Double score;

	@Column(name = "EMP_NO")
	private Long empNo;
}
