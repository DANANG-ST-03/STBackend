package danang03.STBackend.domain.user;

import danang03.STBackend.config.auth.JwtTokenProvider;
import danang03.STBackend.config.auth.dto.JwtToken;
import danang03.STBackend.config.auth.dto.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {
    private static final Logger log = LoggerFactory.getLogger(MemberService.class);
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    public Long signUp(SignUpRequest request) {
        // 이메일 중복 체크
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 이름, 이메일, 비밀번호가 비어 있지 않은지 확인
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new IllegalArgumentException("이름은 필수 입력값입니다.");
        }

        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 필수 입력값입니다.");
        }

        // 이메일 중복 체크
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 엔티티 생성 및 저장
        Member user = Member.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encodedPassword)
                .role(Role.ADMIN)
                .build();

        memberRepository.save(user);

        return user.getId();
    }

    @Transactional
    public JwtToken signIn(String email, String password) {
        try {
            // 1. email + password 를 기반으로 Authentication 객체 생성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(email, password);
            log.info("로그인 1단계 완료");
            // 2. 실제 검증. authenticate() 메서드 실행 시 인증 실패 시 예외 발생
            Authentication authentication =
                    authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            log.info("로그인 2단계 완료");

            // 3. 인증 정보를 기반으로 JWT 토큰 생성
            JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
            log.info("로그인 3단계 완료");


            return jwtToken;

        } catch (Exception e) {
            // 인증 실패 시 명시적으로 예외 발생
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
    }
}
