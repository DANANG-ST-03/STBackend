package danang03.STBackend.domain.projects.dto;

import danang03.STBackend.domain.projects.EmployeeProject;
import lombok.Getter;

@Getter
public class EmployeeProjectChangeJoinStatusRequest {
    private EmployeeProject.JoinStatus joinStatus;
}
