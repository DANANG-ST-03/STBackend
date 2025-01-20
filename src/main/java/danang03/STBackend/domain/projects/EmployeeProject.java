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
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

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

    @Setter
    @Enumerated(EnumType.STRING)
    private Role role;

    private String contribution;

    @Setter
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
        this.joinDate = null;
        this.exitDate = null;
        this.joinStatus = JoinStatus.READY;
    }

    @RequiredArgsConstructor
    @Getter
    public enum Role {
        TEAM_LEADER("Team Leader"),
        DESIGNER("Designer"),
        FE_DEVELOPER("Frontend Developer"),
        BE_DEVELOPER("Backend Developer"),
        AI_ENGINEER("AI Engineer"),
        TESTER("Tester");

        private final String displayText;
    }

    public enum JoinStatus {
        READY,
        DOING,
        EXITED
    }
}