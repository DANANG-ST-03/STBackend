package danang03.STBackend.domain.employee.dto;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public class UpdateEmployeeRequest {
    private String name;
    private String email;
    private String contact;
    private String skills;
    private LocalDate joiningDate;
    private String role;
}
