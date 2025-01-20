package danang03.STBackend.domain.employee;

import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    boolean existsByEmail(String email);

    @Query("SELECT e.name FROM Employee e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :input, '%'))")
    List<String> findNamesStartingWith(@Param("input") String input, Pageable limit);


    // for search
    @Query("SELECT e FROM Employee e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.role) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(e.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Employee> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);


    // for dashboard
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.joiningDate <= :endDate")
    Integer countEmployeesJoinedBefore(@Param("endDate") LocalDate endDate);

    @Query("SELECT es, COUNT(e) FROM Employee e JOIN e.skills es GROUP BY es")
    List<Object[]> countSkills();
}
