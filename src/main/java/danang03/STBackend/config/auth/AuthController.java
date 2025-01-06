package danang03.STBackend.config.auth;

import danang03.STBackend.config.auth.dto.JwtToken;
import danang03.STBackend.config.auth.dto.SignUpRequest;
import danang03.STBackend.config.auth.dto.SigninRequest;
import danang03.STBackend.domain.user.MemberService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
public class AuthController {

    private final MemberService memberService;

    @Autowired
    public AuthController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignUpRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 유효성 검사 실패
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(errorMessage);
        }

        try {
            memberService.signUp(request);
            return ResponseEntity.ok("회원가입이 성공적으로 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            // 이메일 중복 등 비즈니스 로직 예외 처리
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // 기타 예외 처리
            return ResponseEntity.status(500).body("서버 에러가 발생했습니다.");
        }
    }


    @GetMapping("/signin")
    public String loginPage() {
        return "signin";
    }


    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody SigninRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        try {
            // 로그인 처리 및 JWT 발급
            JwtToken jwtToken = memberService.signIn(email, password);

            log.info("request email = {}, password = {}", email, password);
            log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());

            return ResponseEntity.ok(jwtToken);
        } catch (IllegalArgumentException e) {
            // 로그인 실패 처리
            log.error("로그인 실패: {}", e.getMessage());
            return ResponseEntity.status(401).body("로그인에 실패했습니다. " + e.getMessage());
        } catch (Exception e) {
            // 기타 예외 처리
            log.error("서버 에러 발생: {}", e.getMessage());
            return ResponseEntity.status(500).body("서버 에러가 발생했습니다.");
        }
    }

//    public String login(LoginRequest request) {
//        memberService.signIn(request.getEmail(), request.getPassword());
//        return "redirect:/";
//    }

}
