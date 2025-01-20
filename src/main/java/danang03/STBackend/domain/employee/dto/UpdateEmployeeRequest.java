package danang03.STBackend.domain.employee.dto;

import danang03.STBackend.domain.employee.Skill;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UpdateEmployeeRequest {
    private String name;
    private String email;
    private String contact;
    private List<Skill> skills;
    private LocalDate joiningDate;
    private String role;
}
