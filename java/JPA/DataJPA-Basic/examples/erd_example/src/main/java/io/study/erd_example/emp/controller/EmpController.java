package io.study.erd_example.emp.controller;

import io.study.erd_example.emp.dto.EmployeeDto;
import io.study.erd_example.emp.entity.Department;
import io.study.erd_example.emp.entity.Employee;
import io.study.erd_example.emp.repository.DeptDataRepository;
import io.study.erd_example.emp.repository.EmpDataRepository;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EmpController {

	private final EmpDataRepository empDataRepository;

	private final DeptDataRepository deptDataRepository;

	private final EntityManager em;

	public EmpController(EmpDataRepository empDataRepository,
							DeptDataRepository deptDataRepository,
							EntityManager em){
		this.empDataRepository = empDataRepository;
		this.deptDataRepository = deptDataRepository;
		this.em = em;
	}

	@GetMapping("/employee/v1/{id}")
	public String getEmployeeById(@PathVariable("id") Long id){
		Employee employee = empDataRepository.findById(id).get();
		return employee.getUsername();
	}

	@GetMapping("/employee/v2/{id}")
	public String getEmployeeById2(@PathVariable("id") Employee employee){
		return employee.getUsername();
	}

	// 여기부터...
	@GetMapping("/employees")
	@ResponseBody
	public Page<EmployeeDto> list(@PageableDefault(size = 5, sort = "username") Pageable pageable){
		Page<Employee> all = empDataRepository.findAll(pageable);

		Page<EmployeeDto> map = all.map(e -> {
			final String username = e.getUsername();
			final Double salary = e.getSalary();
			return new EmployeeDto(username, salary);
		});

		return map;
	}

	@GetMapping("/v2/employees")
	@ResponseBody
	public Page<EmployeeDto> listV2(@PageableDefault(size = 5, sort = "username") Pageable pageable){
		Page<Employee> all = empDataRepository.findAll(pageable);

//		Page<EmployeeDto> map = all.map(e -> {
//			return new EmployeeDto(e);
//		});

//		Page<EmployeeDto> map = all.map(e->new EmployeeDto(e));

		Page<EmployeeDto> map = all.map(EmployeeDto::new);

		return map;
	}


	@PostConstruct
	public void initData(){
		Department police = new Department("POLICE");
		deptDataRepository.save(police);

		for(int i=0; i<100; i++){
			final Employee e = new Employee("경찰관 #"+String.valueOf(i), 1000D, police);
			empDataRepository.save(e);
		}
	}
}
