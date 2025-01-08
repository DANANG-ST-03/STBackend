package danang03.STBackend.domain.employee.dto;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public class EmployeeResponse {
    private Long id;
    private String name;
    private String email;
    //    private String picture;
    private String contact;
    private String skills;
    private LocalDate joiningDate;
}
