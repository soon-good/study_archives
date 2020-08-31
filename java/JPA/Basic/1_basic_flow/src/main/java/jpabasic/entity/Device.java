package jpabasic.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Device {

	@Id @GeneratedValue
	private Long id;

	private String name;

	@OneToMany(mappedBy = "device")
	private List<EmployeeDevice> employeeDevices = new ArrayList<>();

}
