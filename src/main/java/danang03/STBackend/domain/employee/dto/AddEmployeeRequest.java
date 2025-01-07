package danang03.STBackend.domain.employee.dto;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public class AddEmployeeRequest {
    private String name;
    private String email;
    private String picture;
    private String contact;
    private String skills;
    private LocalDate joiningDate;
}