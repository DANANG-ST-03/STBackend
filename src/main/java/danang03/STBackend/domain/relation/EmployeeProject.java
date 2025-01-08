package danang03.STBackend.domain.relation;

import danang03.STBackend.domain.employee.Employee;
import danang03.STBackend.domain.member.Member;
import danang03.STBackend.domain.projects.Project;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Lob
    private String contribution;

    public enum Role {
        MANAGER, DEVELOPER, DESIGNER
    }
}