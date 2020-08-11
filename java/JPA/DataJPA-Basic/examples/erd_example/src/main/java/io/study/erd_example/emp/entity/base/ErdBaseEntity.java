package io.study.erd_example.emp.entity.base;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class ErdBaseEntity extends ErdBaseTimeEntity{

	@CreatedBy
	private String createdBy;

	@LastModifiedBy
	private String lastModifiedBy;
}
