package danang03.STBackend.config.auth;

import danang03.STBackend.config.auth.dto.JwtToken;
import danang03.STBackend.config.auth.dto.SignUpRequest;
import danang03.STBackend.config.auth.dto.SignUpResponse;
import danang03.STBackend.config.auth.dto.SigninRequest;
import danang03.STBackend.domain.member.MemberService;
import danang03.STBackend.dto.GlobalResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
        log.info("signup 유효성검사 굿");

        try {
            Long memberId = memberService.signUp(request);
            SignUpResponse signUpResponse = new SignUpResponse(memberId);
            GlobalResponse globalResponse = GlobalResponse.builder()
                    .status(200)
                    .message("signup success")
                    .data(signUpResponse).build();
            return ResponseEntity.ok(globalResponse);
        } catch (IllegalArgumentException e) {
            // 이메일 중복 등 비즈니스 로직 예외 처리
            GlobalResponse globalResponse = GlobalResponse.builder()
                    .status(400)
                    .message("This is a duplicate email.")
                    .data(null).build();
            return ResponseEntity.badRequest().body(globalResponse);
        } catch (Exception e) {
            // 기타 예외 처리
            GlobalResponse globalResponse = GlobalResponse.builder()
                    .status(500)
                    .message("server error occurred")
                    .data(null).build();
            return ResponseEntity.status(500).body(globalResponse);
        }
    }


    @GetMapping("/signin")
    public String loginPage() {
        return "signin";
    }


    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody SigninRequest request, HttpServletResponse response) {
        String email = request.getEmail();
        String password = request.getPassword();

        try {
            // 로그인 처리 및 JWT 발급
            JwtToken jwtToken = memberService.signIn(email, password);

            log.info("request email = {}, password = {}", email, password);
            log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());

            // Access Token을 헤더에 추가
            response.setHeader("Authorization", "Bearer " + jwtToken.getAccessToken());

            // Refresh Token을 헤더에 추가 (선택 사항)
            response.setHeader("Refresh-Token", jwtToken.getRefreshToken());

            // 응답 바디를 구성하여 클라이언트에 추가 데이터 반환
            GlobalResponse globalResponse = GlobalResponse.builder()
                    .status(200)
                    .message("signin success")
                    .data(jwtToken)
                    .build();

            return ResponseEntity.ok(globalResponse);

        } catch (IllegalArgumentException e) {
            // 로그인 실패 처리
            log.error("로그인 실패: {}", e.getMessage());
            GlobalResponse globalResponse = GlobalResponse.builder()
                    .status(401)
                    .message("로그인에 실패했습니다. " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(401).body(globalResponse);
        } catch (Exception e) {
            // 기타 예외 처리
            log.error("서버 에러 발생: {}", e.getMessage());
            GlobalResponse globalResponse = GlobalResponse.builder()
                    .status(500)
                    .message("서버 에러가 발생했습니다.")
                    .data(null)
                    .build();
            return ResponseEntity.status(500).body(globalResponse);
        }
    }

//    public String login(LoginRequest request) {
//        memberService.signIn(request.getEmail(), request.getPassword());
//        return "redirect:/";
//    }

}
