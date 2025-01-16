package danang03.STBackend.domain.member.dto;

import danang03.STBackend.domain.member.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
    private Long id;
    private String name;
    private String username;
    private String email;
    private Role role;
//    private String picture;
}
