package danang03.STBackend.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Entity
@NoArgsConstructor
public class Member implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String picture;

    @Column
    private String password;

    private String contact;

    private String skills;

    private LocalDate joiningDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    public Member(String name, String email, String picture, String password, String contact, String skills, LocalDate joiningDate, Role role) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.password = password;
        this.contact = contact;
        this.skills = skills;
        this.joiningDate = joiningDate;
        this.role = role;
    }

//    @Builder
//    public Member(String name, String password, String email, String picture, Role role) {
//        this.name = name;
//        this.password = password;
//        this.email = email;
//        this.picture = picture;
//        this.role = role;
//    }

    // 소셜로그인으로 구글에서 데이터 받아와서 업데이트하는 함수
    public Member update(String name, String picture) {
        this.name = name;
        this.picture = picture;
        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.email; // email을 username 대용으로 사용
    }
}