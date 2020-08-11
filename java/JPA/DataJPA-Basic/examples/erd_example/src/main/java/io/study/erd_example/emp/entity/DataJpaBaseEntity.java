package io.study.erd_example.emp.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class DataJpaBaseEntity {

	@CreatedDate
	@Column(updatable = false)
	LocalDateTime createdDate;

	@LastModifiedDate
	LocalDateTime lastModifiedDate;

	@CreatedBy
	@Column(updatable = false)
	String createdBy;

	@LastModifiedBy
	String lastModifiedBy;
}
