package io.study.erd_example.onetoone_twoway_master_fk.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
@Entity(name = "IDCARD")
public class IDCard {

	@Id @GeneratedValue
	@Column(name = "ID_CARD_NO")
	private Long id;

	@Column(name = "MANUFACTURER")
	private String manufacturer;

	@Column(name = "PRICE")
	private Double price;

	@OneToOne(mappedBy = "idCard")
	private Employee employee;
}
