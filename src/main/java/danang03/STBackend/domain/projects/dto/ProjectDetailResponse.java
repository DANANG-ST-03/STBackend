package danang03.STBackend.domain.projects.dto;

import danang03.STBackend.domain.employee.dto.EmployeeResponseForProjectDetail;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectDetailResponse {
    private ProjectResponse projectInfo;
    private List<EmployeeResponseForProjectDetail> employees;
}
