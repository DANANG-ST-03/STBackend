package danang03.STBackend.domain.employee.dto;
import danang03.STBackend.domain.employee.Skill;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class EmployeeResponse {
    private Long id;
    private String name;
    private String firstName;
    private String lastName;
    private String email;
    private String contact;
    private List<Skill> skills;
    private LocalDate joiningDate;
    private String role;
    private String imageUrl;
}
