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
		createdDate = now;	// 데이터 초기 생성시 현재 시점의 시간을 지정
		updatedDate = now;	// 데이터 초기 생성시 현재 시점의 시간을 지정
//		updatedDate = null;
//		디폴트 데이터를 null 로 지정하게 되면 추후 불편해지기도 한다.
	}

	@PreUpdate
	public void preUpdate(){
		updatedDate = LocalDateTime.now();
	}
}
