package danang03.STBackend.config.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInResponse {
    private Long memberId;
    private JwtToken jwtToken;
}