package danang03.STBackend.domain.member.dto;

import lombok.Getter;

@Getter
public class PasswordChangeRequest {
    private String oldPassword;
    private String newPassword;
}
