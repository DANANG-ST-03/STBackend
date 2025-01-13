package danang03.STBackend.domain.employee.dto;

import danang03.STBackend.domain.projects.dto.ProjectResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployeeDetailResponse {
    private EmployeeResponse employeeInfo;
    private List<ProjectResponse> projects;
}
