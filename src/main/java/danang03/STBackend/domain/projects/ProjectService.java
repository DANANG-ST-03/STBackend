package danang03.STBackend.domain.projects;

import danang03.STBackend.domain.employee.Employee;
import danang03.STBackend.domain.employee.EmployeeRepository;
import danang03.STBackend.domain.projects.dto.ProjectAddRequest;
import danang03.STBackend.domain.projects.dto.ProjectResponse;
import danang03.STBackend.domain.projects.dto.ProjectUpdateRequest;
import danang03.STBackend.domain.projects.dto.EmployeeProjectAssignmentRequest;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeProjectRepository employeeProjectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository, EmployeeRepository employeeRepository, EmployeeProjectRepository employeeProjectRepository) {
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
        this.employeeProjectRepository = employeeProjectRepository;
    }

    public Long addProject(ProjectAddRequest request) {
        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus()).build();
         projectRepository.save(project);

         return project.getId();
    }


    public Page<ProjectResponse> getProjects(Pageable pageable) {
        return projectRepository.findAll(pageable)
                .map(project -> new ProjectResponse(
                        project.getId(),
                        project.getName(),
                        project.getDescription(),
                        project.getStartDate(),
                        project.getEndDate(),
                        project.getStatus()
                ));
    }


    @Transactional
    public void updateProject(Long id, ProjectUpdateRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project with id " + id + "not found"));

        // 업데이트 메서드 호출
        project.update(
                request.getName(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                request.getStatus()
        );
    }

    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new IllegalArgumentException("Project with id " + id + " not found");
        }
        projectRepository.deleteById(id);
    }


    @Transactional
    public List<Long> assignEmployeesToProject(Long projectId, List<EmployeeProjectAssignmentRequest> requests) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        List<Long> employeeProjectIds = new ArrayList<>();

        for (EmployeeProjectAssignmentRequest request : requests) {
            Employee employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

            EmployeeProject employeeProject = EmployeeProject.builder()
                    .project(project)
                    .employee(employee)
                    .role(request.getRole())
                    .contribution(request.getContribution())
                    .build();

            EmployeeProject savedEmployeeProject = employeeProjectRepository.save(employeeProject);
            employeeProjectIds.add(savedEmployeeProject.getId());
        }

        return employeeProjectIds;
    }
}
