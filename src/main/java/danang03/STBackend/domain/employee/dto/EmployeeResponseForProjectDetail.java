package danang03.STBackend.domain.employee.dto;

import danang03.STBackend.domain.projects.dto.EmployeeProjectResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployeeResponseForProjectDetail {
    private EmployeeResponse employeeInfo;
    private EmployeeProjectResponse employeeProjectInfo;
}
