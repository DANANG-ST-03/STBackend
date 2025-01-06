package danang03.STBackend.web.dto;

import danang03.STBackend.config.auth.dto.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class indexController {
    private final HttpSession httpSession;

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
    public String getSecret(Model model) {
        SessionUser user = (SessionUser) httpSession.
                getAttribute("user");
        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        return "secret";
    }
}
