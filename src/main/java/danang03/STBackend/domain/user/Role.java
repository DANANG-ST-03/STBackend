package danang03.STBackend.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    GUEST("ROLE_GUEST", "guest"),
    ADMIN("ROLE_ADMIN", "admin");

    private final String key;
    private final String title;
}
