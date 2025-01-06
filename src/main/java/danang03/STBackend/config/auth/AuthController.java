package danang03.STBackend.config.auth;

import danang03.STBackend.config.auth.dto.JwtToken;
import danang03.STBackend.config.auth.dto.SignUpRequest;
import danang03.STBackend.config.auth.dto.SigninRequest;
import danang03.STBackend.domain.user.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
    public String signup(SignUpRequest request) {
        memberService.signUp(request);
        // 회원가입 후 로그인 페이지로 이동 or 메인 페이지 등
        return "redirect:/login";
    }


    @GetMapping("/signin")
    public String loginPage() {
        return "signin";
    }


    @PostMapping("/signin")
    public String login(@RequestBody SigninRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        // 로그인 처리 및 JWT 발급
        JwtToken jwtToken = memberService.signIn(email, password);

        log.info("request email = {}, password = {}", email, password);
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());

        // 클라이언트 측에서 JWT를 사용하도록 하기 위해 세션이나 쿠키에 저장
        // 아래는 쿠키에 저장하는 예시
        return "redirect:/?accessToken=" + jwtToken.getAccessToken() + "&refreshToken=" + jwtToken.getRefreshToken();
    }

//    public String login(LoginRequest request) {
//        memberService.signIn(request.getEmail(), request.getPassword());
//        return "redirect:/";
//    }

}
