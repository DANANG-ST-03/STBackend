package danang03.STBackend.domain.projects;

import danang03.STBackend.domain.projects.dto.ProjectAddRequest;
import danang03.STBackend.domain.projects.dto.ProjectAddResponse;
import danang03.STBackend.domain.projects.dto.ProjectResponse;
import danang03.STBackend.domain.projects.dto.ProjectUpdateRequest;
import danang03.STBackend.domain.projects.dto.ProjectUpdateResponse;
import danang03.STBackend.dto.GlobalResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/project")
public class ProjectController {

    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);
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


    @GetMapping
    public ResponseEntity<GlobalResponse> getProjectsByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Pageable pageable) {
        Page<ProjectResponse> projects = projectService.getProjects(PageRequest.of(page, size));
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("get projects success")
                .data(projects).build();
        return ResponseEntity.ok(globalResponse);
    }

    @PutMapping("{id}")
    public ResponseEntity<GlobalResponse> updateProject(@PathVariable Long id, @RequestBody ProjectUpdateRequest request) {
        projectService.updateProject(id, request);
        ProjectUpdateResponse projectUpdateResponse = new ProjectUpdateResponse(id);
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("updated project successfully")
                .data(projectUpdateResponse).build();

        return ResponseEntity.ok(globalResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GlobalResponse> deleteProject(@PathVariable Long id) {
        log.info("Delete project successfully");
        projectService.deleteProject(id);
        log.info("Delete project successfully");
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("deleted project successfully")
                .data(null).build();
        log.info("Delete project successfully");
        return ResponseEntity.ok(globalResponse);
    }
}
