package danang03.STBackend.domain.projects.dto;

import danang03.STBackend.domain.projects.EmployeeProject;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class EmployeeProjectResponse {
    private EmployeeProject.Role roleInProject;
    private String contribution;
    private LocalDate joinDate;
    private LocalDate exitDate;
    private EmployeeProject.JoinStatus joinStatus;
}
