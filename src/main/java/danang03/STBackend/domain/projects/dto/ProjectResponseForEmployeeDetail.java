package danang03.STBackend.domain.projects.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectResponseForEmployeeDetail {
    private ProjectResponse projectInfo;
    private EmployeeProjectResponse employeeProjectInfo;

}
