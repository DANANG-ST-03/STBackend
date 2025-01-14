package danang03.STBackend.domain.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployeeSimpleResponse {
    private Long employeeId;
    private String name;
    private String imageUrl;
}
