package danang03.STBackend.config.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpRequest {
    private String email;
    private String password;
    private String name;
}