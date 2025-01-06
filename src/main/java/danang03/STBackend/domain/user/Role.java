package danang03.STBackend.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    SUPER_ADMIN("ROLE_SUPER_ADMIN", "super_admin"),
    ADMIN("ROLE_ADMIN", "admin");

    private final String key;
    private final String title;
}
