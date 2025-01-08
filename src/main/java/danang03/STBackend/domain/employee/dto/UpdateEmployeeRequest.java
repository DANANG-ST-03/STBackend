package danang03.STBackend.domain.employee.dto;

import java.time.LocalDate;
import lombok.Getter;

@Getter
public class UpdateEmployeeRequest {
    private Long id; // 업데이트할 Employee의 ID

    private String name;
    private String email;
//    private String picture;
    private String contact;
    private String skills;
    private LocalDate joiningDate;
}
