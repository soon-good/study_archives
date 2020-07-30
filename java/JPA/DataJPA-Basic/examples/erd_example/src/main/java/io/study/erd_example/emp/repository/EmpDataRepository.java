package io.study.erd_example.emp.repository;

import io.study.erd_example.emp.entity.Employee;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpDataRepository extends JpaRepository<Employee, Long> {

}
