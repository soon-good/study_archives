package io.study.erd_example.emp.repository;

import io.study.erd_example.emp.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeptDataRepository extends JpaRepository<Department, Long> {

}
