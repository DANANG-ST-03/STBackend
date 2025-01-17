package danang03.STBackend.domain.projects;

import danang03.STBackend.domain.projects.dto.EmployeeProjectAssignmentResponse;
import danang03.STBackend.domain.projects.dto.EmployeeProjectChangeJoinStatusRequest;
import danang03.STBackend.domain.projects.dto.EmployeeProjectChangeRoleRequest;
import danang03.STBackend.domain.projects.dto.ProjectAddRequest;
import danang03.STBackend.domain.projects.dto.ProjectAddResponse;
import danang03.STBackend.domain.projects.dto.ProjectDetailResponse;
import danang03.STBackend.domain.projects.dto.ProjectResponse;
import danang03.STBackend.domain.projects.dto.ProjectUpdateRequest;
import danang03.STBackend.domain.projects.dto.ProjectUpdateResponse;
import danang03.STBackend.domain.projects.dto.EmployeeProjectAssignmentRequest;
import danang03.STBackend.dto.GlobalResponse;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = "http://localhost:5173")
public class ProjectController {

    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);
    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<GlobalResponse> addProject(@RequestBody ProjectAddRequest request) {
        Long projectId = projectService.addProject(request);
        ProjectAddResponse response = new ProjectAddResponse(projectId);
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("Project added successfully")
                .data(response).build();

        return ResponseEntity.ok(globalResponse);
    }



    @GetMapping("/{id}/detail")
    public ResponseEntity<GlobalResponse> getProjectDetail(@PathVariable Long id) {
        ProjectDetailResponse projectDetail = projectService.getProjectDetail(id);
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("get Project id " + id + " detail success")
                .data(projectDetail).build();
        return ResponseEntity.ok(globalResponse);
    }


    @GetMapping
    public ResponseEntity<GlobalResponse> getProjectsByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Pageable pageable) {
        Page<ProjectResponse> projects = projectService.getProjectsByPage(PageRequest.of(page, size));
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("get projects success")
                .data(projects).build();
        return ResponseEntity.ok(globalResponse);
    }


    @CrossOrigin(origins = "http://localhost:5173")
    @GetMapping("/search")
    public ResponseEntity<GlobalResponse> searchProjectsByPage(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ProjectResponse> searchedProjects = projectService.searchProjectsByPage(keyword, PageRequest.of(page, size));
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("searched projects success")
                .data(searchedProjects).build();
        return ResponseEntity.ok(globalResponse);
    }



    @PutMapping("/{id}")
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





    // 프로젝트에 직원을 배정
    @PostMapping("{projectId}/employee")
    public ResponseEntity<GlobalResponse> assignEmployeesToProject(
            @PathVariable Long projectId,
            @RequestBody List<EmployeeProjectAssignmentRequest> request) {

        // 서비스 호출
        List<Long> employeeProjectIds = projectService.assignEmployeesToProject(projectId, request);
        List<EmployeeProjectAssignmentResponse> responses = new ArrayList<>();
        for (Long id : employeeProjectIds) {
            responses.add(new EmployeeProjectAssignmentResponse(id));
        }

        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("Employee assigned to project successfully")
                .data(responses).build();
        return ResponseEntity.ok(globalResponse);
    }

    @PutMapping("{projectId}/employee/{employeeId}")
    public ResponseEntity<GlobalResponse> changeEmployeeJoinStatus(
            @PathVariable Long projectId,
            @PathVariable Long employeeId,
            @RequestBody EmployeeProjectChangeJoinStatusRequest joinStatusRequest
    ) {
        projectService.changeEmployeeJoinStatus(projectId, employeeId, joinStatusRequest);
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("Employee changed joinStatus to " + joinStatusRequest.getJoinStatus() + " successfully")
                .data(null).build();
        return ResponseEntity.ok(globalResponse);
    }

    @PutMapping("/{projectId}/employee/{employeeId}/role")
    public ResponseEntity<GlobalResponse> changeEmployeeRole(
            @PathVariable Long projectId,
            @PathVariable Long employeeId,
            @RequestBody EmployeeProjectChangeRoleRequest request
    ) {
        projectService.changeEmployeeProjectRole(projectId, employeeId, request);
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("EmployeeRole changed to " + request.getRole().toString() + " successfully")
                .data(null).build();
        return ResponseEntity.ok(globalResponse);
    }

    @DeleteMapping("/{projectId}/employee/{employeeId}")
    public ResponseEntity<GlobalResponse> removeEmployeesFromProject(
            @PathVariable Long projectId,
            @PathVariable Long employeeId) {

        projectService.removeEmployeesFromProject(projectId, employeeId);
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("Employee removed from project successfully")
                .data(null).build();
        return ResponseEntity.ok(globalResponse);
    }
}
