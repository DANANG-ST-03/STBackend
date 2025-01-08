package danang03.STBackend.domain.relation;

import danang03.STBackend.domain.relation.dto.EmployeeProjectRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class EmployeeProjectController {
    private final EmployeeProjectService employeeProjectService;

    public EmployeeProjectController(EmployeeProjectService employeeProjectService) {
        this.employeeProjectService = employeeProjectService;
    }

    // 프로젝트에 직원을 배정
    @PostMapping("/assign")
    public ResponseEntity<String> assignEmployeeToProject(@RequestBody EmployeeProjectRequest request) {
        employeeProjectService.assignEmployeeToProject(request);
        return ResponseEntity.ok("Employee assigned to project successfully");
    }
}
