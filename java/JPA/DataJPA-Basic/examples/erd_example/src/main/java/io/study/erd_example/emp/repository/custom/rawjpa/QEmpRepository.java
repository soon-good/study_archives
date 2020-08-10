package io.study.erd_example.emp.repository.custom.rawjpa;

import io.study.erd_example.emp.entity.Employee;
import java.util.List;

public interface QEmpRepository {
	List<Employee> selectAllEmployees();
}
