package danang03.STBackend.domain.projects;

import danang03.STBackend.domain.employee.Employee;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "project", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String contribution;


    private LocalDate joinDate;
    @Setter
    private LocalDate exitDate;

    @Setter
    @Enumerated(EnumType.STRING)
    private JoinStatus joinStatus;

    public EmployeeProject(Employee employee, Project project, Role role, String contribution) {
        this.employee = employee;
        this.project = project;
        this.role = role;
        this.contribution = contribution;
        this.joinDate = LocalDate.now();
        this.exitDate = null;
        this.joinStatus = JoinStatus.DOING;
    }

    public enum Role {
        TEAM_LEADER,
        DESIGNER,
        FE_DEVELOPER,
        BE_DEVELOPER,
        AI_ENGINEER,
        TESTER
    }

    public enum JoinStatus {
        DOING,
        EXITED
    }
}