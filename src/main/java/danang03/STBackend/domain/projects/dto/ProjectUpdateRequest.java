package danang03.STBackend.domain.projects.dto;

import danang03.STBackend.domain.projects.Project;
import danang03.STBackend.domain.projects.ProjectCategory;
import danang03.STBackend.domain.projects.ProjectStatus;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class ProjectUpdateRequest {
    private String name;
    private String description;
    private ProjectCategory category;
//    private LocalDate startDate;
//    private LocalDate endDate;
    private ProjectStatus status;
}