package danang03.STBackend.domain.projects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Setter
    @Column(nullable = false)
    private String name;

    @Setter
    private String description;

    @Enumerated(EnumType.STRING)  // Enum 값을 문자열로 저장
    @Column(nullable = false)
    @Setter
    private ProjectCategory category;

    @Setter
    private LocalDate startDate;

    @Setter
    private LocalDate endDate;

    @Setter
    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    @OneToMany(mappedBy = "project")
    private List<EmployeeProject> employeeProjects = new ArrayList<>();


    public Project(String name, String description, ProjectCategory category) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.startDate = null;
        this.endDate = null;
        this.status = ProjectStatus.PENDING;
    }

    // 프로젝트 생성 시 자동으로 createdAt 설정
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
