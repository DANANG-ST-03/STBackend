package danang03.STBackend.domain.projects.dto;

import danang03.STBackend.domain.projects.EmployeeProject;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EmployeeProjectChangeJoinStatusRequest {
    private EmployeeProject.JoinStatus joinStatus;
}
