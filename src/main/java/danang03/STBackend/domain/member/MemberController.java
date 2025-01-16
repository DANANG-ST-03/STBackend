package danang03.STBackend.domain.member;

import danang03.STBackend.domain.employee.dto.EmployeeResponse;
import danang03.STBackend.domain.member.dto.MemberResponse;
import danang03.STBackend.dto.GlobalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("admin")
public class MemberController {
    private final MemberService memberService;
    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<GlobalResponse> getEmployeesByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Pageable pageable) {
        Page<MemberResponse> members = memberService.getMembersByPage(PageRequest.of(page, size));
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("get Admins success")
                .data(members).build();
        return ResponseEntity.ok(globalResponse);
    }

//    @PatchMapping("")
}
