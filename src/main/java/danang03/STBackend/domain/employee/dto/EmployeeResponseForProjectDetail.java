package danang03.STBackend.domain.employee.dto;

import danang03.STBackend.domain.projects.EmployeeProject;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EmployeeResponseForProjectDetail {
    private Long id;
    private String name;
    private String email;
    private String contact;
    private String skills;
    private LocalDate joiningDate;
    private String roleOfEmployee;
    private String imageUrl;
    private EmployeeProject.Role roleInProject;
    private String contribution;
}
