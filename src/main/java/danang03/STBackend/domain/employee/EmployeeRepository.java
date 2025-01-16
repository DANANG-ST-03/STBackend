package danang03.STBackend.domain.employee;

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
}
