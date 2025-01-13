package danang03.STBackend.domain.projects.dto;

import danang03.STBackend.domain.projects.EmployeeProject;
import danang03.STBackend.domain.projects.ProjectStatus;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProjectResponseForEmployeeDetail {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;

    private EmployeeProject.Role roleInProject;
    private String contribution;
}
