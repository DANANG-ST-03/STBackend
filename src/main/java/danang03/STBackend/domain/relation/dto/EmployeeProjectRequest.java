package danang03.STBackend.domain.relation.dto;

import lombok.Getter;
import org.springframework.web.service.annotation.GetExchange;

@Getter
public class EmployeeProjectRequest {
    private Long employeeId; // 직원 ID
    private Long projectId;  // 프로젝트 ID
    private String role;     // 역할 (예: 'DEVELOPER', 'MANAGER')
    private String contribution; // 기여 내용
}
