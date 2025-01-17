package danang03.STBackend.domain.member;

import danang03.STBackend.config.auth.JwtTokenProvider;
import danang03.STBackend.config.auth.dto.JwtToken;
import danang03.STBackend.config.auth.dto.SignUpRequest;
import danang03.STBackend.domain.member.dto.PasswordChangeRequest;
import danang03.STBackend.domain.member.dto.MemberResponse;
import danang03.STBackend.domain.member.dto.PasswordResetResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.RandomStringUtils;


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
            throw new IllegalArgumentException("email or password is incorrect.");
        }
    }

    public Page<MemberResponse> getMembersByPage(Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(
                        Sort.Order.asc("id")
                )
        );

        return memberRepository.findAll(sortedPageable)
                .map(member -> new MemberResponse(
                        member.getId(),
                        member.getName(),
                        member.getUsername(),
                        member.getEmail(),
                        member.getRole()
                ));
    }

    @Transactional
    public void changePassword(PasswordChangeRequest request) {
        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();

        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // 현재 로그인한 사용자의 이메일 가져오기

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("cannot find member with email: " + email));

        // 기존 비밀번호 검증
        if (!passwordEncoder.matches(oldPassword, member.getPassword())) {
            throw new IllegalArgumentException("old password is incorrect.");
        }

        // 새 비밀번호 암호화 후 저장
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
    }


    @Transactional
    public PasswordResetResponse resetPassword(Long memberId) {
        // memberId로 사용자 찾기
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("cannot find member with id: " + memberId));

        String newPassword =  RandomStringUtils.randomAlphanumeric(8);
        // 새 비밀번호 암호화 후 저장
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);

        PasswordResetResponse passwordResetResponse = new PasswordResetResponse(
                newPassword,
                memberId,
                member.getName(),
                member.getEmail()
        );
        return passwordResetResponse;
    }
}
