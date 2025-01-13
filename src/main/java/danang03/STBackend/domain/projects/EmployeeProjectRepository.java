package danang03.STBackend.domain.projects;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeProjectRepository extends JpaRepository<EmployeeProject, Long> {
    List<EmployeeProject> findByProjectIdAndEmployeeIdIn(Long projectId, List<Long> employeeIds);
    List<EmployeeProject> findByEmployeeId(Long employeeId);
    List<EmployeeProject> findByProjectId(Long employeeId);


    boolean existsByProjectIdAndEmployeeId(Long projectId, Long employeeId);
    boolean existsByProjectId(Long id);
    boolean existsByEmployeeId(Long id);

}
