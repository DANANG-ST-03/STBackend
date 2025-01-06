//package danang03.STBackend.domain.member2;
//
//import java.util.Optional;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//@Controller
//public class MemberController {
//    private final MemberService memberService;
//
//    @Autowired
//    public MemberController(MemberService memberService) {
//        this.memberService = memberService;
//    }
//
//
//    @GetMapping("/test/member/{memberId}")
//    public Optional<Member> getMember(@PathVariable Long memberId) {
//        return memberService.getMemberById(memberId);
//    }
//}
