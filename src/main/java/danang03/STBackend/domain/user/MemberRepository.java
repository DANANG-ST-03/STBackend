package danang03.STBackend.domain.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByEmail(String email);

    // 이메일 기준으로 중복 여부를 확인하기 위해
    boolean existsByEmail(String email);
}
