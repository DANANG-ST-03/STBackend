package danang03.STBackend.domain.member;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer>, PagingAndSortingRepository<Member, Integer> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByUsername(String username);

    // 이메일 기준으로 중복 여부를 확인하기 위해
    boolean existsByEmail(String email);
}
