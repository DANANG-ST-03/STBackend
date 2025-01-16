package danang03.STBackend.domain.member;


import danang03.STBackend.domain.member.dto.ChangePasswordRequest;
import danang03.STBackend.domain.member.dto.MemberResponse;
import danang03.STBackend.dto.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;


    @GetMapping
    public ResponseEntity<GlobalResponse> getMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Pageable pageable) {
        Page<MemberResponse> members = memberService.getMembersByPage(PageRequest.of(page, size));
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("get admins success")
                .data(members).build();
        return ResponseEntity.ok(globalResponse);
    }

    @PatchMapping("/password")
    public ResponseEntity<GlobalResponse> changePassword(@RequestBody ChangePasswordRequest request) {
        memberService.changePassword(request);

        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("change password success")
                .data(null).build();
        return ResponseEntity.ok(globalResponse);
    }
}
