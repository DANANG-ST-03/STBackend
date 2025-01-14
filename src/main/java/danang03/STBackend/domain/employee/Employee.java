package danang03.STBackend.domain.employee;

import danang03.STBackend.domain.projects.EmployeeProject;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private String first_name;
    private String last_name;

    @Column(nullable = false, unique = true)
    private String email;
    private String contact;
    private String skills;
    private LocalDate joiningDate;
    private String role;
    private String imageUrl;

    @OneToMany(mappedBy = "employee")
    private List<EmployeeProject> employeeProjects = new ArrayList<>();


    // 업데이트를 위한 메서드
    public void update(String name, String email, String contact, String skills, LocalDate joiningDate, String role) {
        this.name = name != null ? name : this.name;
        this.email = email != null ? email : this.email;
        this.contact = contact != null ? contact : this.contact;
        this.skills = skills != null ? skills : this.skills;
        this.joiningDate = joiningDate != null ? joiningDate : this.joiningDate;
        this.role = role != null ? role : this.role;
    }

    public void updateImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
