package danang03.STBackend.domain.member.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordResetRequest {
    private String newPassword;
}