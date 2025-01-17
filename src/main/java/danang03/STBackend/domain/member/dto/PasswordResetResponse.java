package danang03.STBackend.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PasswordResetResponse {
    private String newPassword;
    private Long memberId;
    private String name;
    private String email;
}
