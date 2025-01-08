package danang03.STBackend.domain.employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
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

    @Column(nullable = false, unique = true)
    private String email;
    private String contact;
    private String skills;
    private LocalDate joiningDate;
    private String role;

    // 업데이트를 위한 메서드
    public void update(String name, String email, String contact, String skills, LocalDate joiningDate, String role) {
        this.name = name != null ? name : this.name;
        this.email = email != null ? email : this.email;
        this.contact = contact != null ? contact : this.contact;
        this.skills = skills != null ? skills : this.skills;
        this.joiningDate = joiningDate != null ? joiningDate : this.joiningDate;
        this.role = role != null ? role : this.role;
    }
}
