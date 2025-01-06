package danang03.STBackend.web.dto;

import danang03.STBackend.config.auth.JwtTokenProvider;
import danang03.STBackend.config.auth.dto.SessionUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@Controller
public class indexController {
    private final HttpSession httpSession;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/")
    public String index(Model model) {
//        model.addAttribute("posts", postsService.
//                findAllDesc());
        SessionUser user = (SessionUser) httpSession.
                getAttribute("user");
        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        return "index";
    }

    @GetMapping("/main")
    public String index2(Model model) {
//        model.addAttribute("posts", postsService.
//                findAllDesc());
        SessionUser user = (SessionUser) httpSession.
                getAttribute("user");
        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        return "index";
    }


    @GetMapping("/secret")
    public ResponseEntity<?> getSecret(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Authorization 헤더가 없거나 형식이 잘못되었습니다.");
            }

            String token = authHeader.substring(7).trim();

            // JWT 검증
            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("유효하지 않은 토큰입니다.");
            }

            // 토큰에서 사용자 정보 추출
            String username = jwtTokenProvider.getUsernameFromToken(token);

            return ResponseEntity.ok("Hello, " + username + "! Secret Page에 접근 성공");
        } catch (Exception e) {
            log.error("에러 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 에러가 발생했습니다.");
        }
    }
}
