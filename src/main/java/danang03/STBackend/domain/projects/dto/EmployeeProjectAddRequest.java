package danang03.STBackend.domain.projects.dto;

import lombok.Getter;

@Getter
public class EmployeeProjectAddRequest {
    private Long employeeId; // 직원 ID
    private Long projectId;  // 프로젝트 ID
    private String role;     // 역할 (예: 'DEVELOPER', 'MANAGER')
    private String contribution; // 기여 내용
}
