package jpabasic.entity;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class EmployeeDevice {

	// 주의할 점)
	// PK,FK 쌍인 emp:empDeivce, dev:empDevice가 있다고 해서
	// EmployeeDevice 의 식별키(id)가 불필요하다고 생각해 만들지 않을 수도 있다.

	// 하지만, 항상 고유하게 식별할 수 있는 값은 항상 고유하게 생성하는 것이 좋다.
	// 테이블 설계시 그대로 생성하는 것이 운영상에 어려움을 초래하지 않는다.
	// 유지보수시 새로운 요구사항이 발생할 때 대응하기에 유연해지기 때문.

	@Id @GeneratedValue
	private Long id;

	@ManyToOne
	@JoinColumn(name = "EMPLOYEE_ID")
	private Employee employee;

	@ManyToOne
	@JoinColumn(name = "DEVICE_ID")
	private Device device;

	private int count;
	private int price;

	private LocalDateTime orderDateTime;
}
