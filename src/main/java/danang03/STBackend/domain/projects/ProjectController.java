package danang03.STBackend.domain.projects;

import danang03.STBackend.domain.projects.dto.ProjectAddRequest;
import danang03.STBackend.domain.projects.dto.ProjectAddResponse;
import danang03.STBackend.dto.GlobalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/project")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<GlobalResponse> addProject(@RequestBody ProjectAddRequest request) {
        System.out.println("Project Name: " + request.getName());
        System.out.println("Project Status: " + request.getStatus()); // Enum 값 출력
        Long projectId = projectService.addProject(request);
        ProjectAddResponse response = new ProjectAddResponse(projectId);
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("Project added successfully")
                .data(response).build();

        return ResponseEntity.ok(globalResponse);

    }
}
