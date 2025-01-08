package danang03.STBackend.domain.projects.dto;

import danang03.STBackend.domain.projects.ProjectStatus;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private ProjectStatus status;
}
