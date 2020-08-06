package io.study.erd_example.emp.repository;

import io.study.erd_example.emp.entity.Employee;
import java.util.List;
import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

public interface EmpDataRepository extends JpaRepository<Employee, Long> {

	@Query("select e from Employee e left join fetch e.dept")
	List<Employee> findAllFetchJoin();

	@EntityGraph(attributePaths = {"dept"})
	@Query("select e from Employee e")
	List<Employee> findAllEntityGraph();

	@QueryHints(value= @QueryHint(name="org.hibernate.readOnly", value="true"))
//	@Query("select e from Employee e where e.username = :username")
	Employee findReadOnlyByUsername(@Param("username") String username);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	List<Employee> findLockByUsername(@Param("username") String username);

	Page<Employee> findBySalary(Double salary, Pageable pageable);

	Slice<Employee> findSliceBySalary(Double salary, Pageable pageable);

	List<Employee> findLimitBySalary(Double salary, Pageable pageable);

	@Query("select e from Employee e left join e.dept d")
	Page<Employee> findAllCountJoinBySalary(Double salary, Pageable pageable);

	@Query(
		value = "select e from Employee e left join e.dept d",
		countQuery = "select count(e) from Employee e group by e.empNo")
	Page<Employee> findSpecificCountJoinBySalary(Double salary, Pageable pageable);

	List<Employee> findTop3BySalary(Double salary);

	@Modifying(clearAutomatically = true)
	@Query("update Employee e set e.salary = e.salary + e.salary*:ratio")
	int bulkSalaryUpdate(@Param("ratio") Double ratio);
}
