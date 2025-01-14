package danang03.STBackend.domain.projects;

import danang03.STBackend.domain.employee.Employee;
import danang03.STBackend.domain.employee.EmployeeRepository;
import danang03.STBackend.domain.employee.dto.EmployeeResponse;
import danang03.STBackend.domain.projects.EmployeeProject.JoinStatus;
import danang03.STBackend.domain.projects.EmployeeProject.Role;
import danang03.STBackend.domain.employee.dto.EmployeeResponseForProjectDetail;
import danang03.STBackend.domain.projects.dto.EmployeeProjectChangeJoinStatusRequest;
import danang03.STBackend.domain.projects.dto.EmployeeProjectChangeRoleRequest;
import danang03.STBackend.domain.projects.dto.EmployeeProjectResponse;
import danang03.STBackend.domain.projects.dto.ProjectAddRequest;
import danang03.STBackend.domain.projects.dto.ProjectDetailResponse;
import danang03.STBackend.domain.projects.dto.ProjectResponse;
import danang03.STBackend.domain.projects.dto.ProjectUpdateRequest;
import danang03.STBackend.domain.projects.dto.EmployeeProjectAssignmentRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        Project project = new Project(request.getName(), request.getDescription(), request.getStatus());
        projectRepository.save(project);

        return project.getId();
    }


    public ProjectResponse getProject(Long id) {
        Project project = projectRepository.findById(id).orElse(null);
        if (project == null) {
            throw new IllegalArgumentException("Project with id " + id + " does not exist");
        }
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .status(project.getStatus()).build();
    }


    public ProjectDetailResponse getProjectDetail(Long projectId) {
        // projectInfo
        ProjectResponse projectResponse = getProject(projectId);

        // employeesInfo
        List<EmployeeProject> employeeProjects = employeeProjectRepository.findByProjectId(projectId);
        List<EmployeeResponseForProjectDetail> employeeResponses = employeeProjects.stream()
                .map(employeeProject -> {
                    Employee employee = employeeProject.getEmployee();

                    EmployeeResponse employeeResponse = EmployeeResponse.builder()
                            .id(employee.getId())
                            .name(employee.getName())
                            .email(employee.getEmail())
                            .contact(employee.getContact())
                            .skills(employee.getSkills())
                            .joiningDate(employee.getJoiningDate())
                            .role(employee.getRole())
                            .imageUrl(employee.getImageUrl()).build();

                    EmployeeProjectResponse employeeProjectResponse = EmployeeProjectResponse.builder()
                            .roleInProject(employeeProject.getRole())
                            .contribution(employeeProject.getContribution())
                            .joinDate(employeeProject.getJoinDate())
                            .exitDate(employeeProject.getExitDate())
                            .joinStatus(employeeProject.getJoinStatus()).build();

                    return new EmployeeResponseForProjectDetail(employeeResponse, employeeProjectResponse);
                })
                .sorted(Comparator.comparing(
                        element -> element.getEmployeeInfo().getName()
                ))
                .toList();

        return new ProjectDetailResponse(projectResponse, employeeResponses);
    }


    public Page<ProjectResponse> getProjectsByPage(Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(
                        Sort.Order.desc("startDate"), // startDate 기준 내림차순
                        Sort.Order.asc("id")                     // id 기준 오름차순
                )
        );

        return projectRepository.findAll(sortedPageable)
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

        project.setName(request.getName());
        project.setDescription(request.getDescription());


        // change status
        ProjectStatus previousStatus = project.getStatus();
        project.setStatus(request.getStatus());

        if (previousStatus == ProjectStatus.PENDING && request.getStatus() == ProjectStatus.WORKING) {
            project.setStartDate(LocalDate.now());
        }
        else if (previousStatus == ProjectStatus.WORKING && request.getStatus() == ProjectStatus.COMPLETE) {
            project.setEndDate(LocalDate.now());
        }

        projectRepository.save(project);
    }


    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new IllegalArgumentException("Project with id " + id + " not found");
        }

        // check employee in project
        boolean hasEmployees = employeeProjectRepository.existsByProjectId(id);
        if (hasEmployees) {
            throw new IllegalStateException("Project with id " + id + " cannot be deleted because it has associated employees.");
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

            // 이미 할당된 직원인지 확인
            boolean exists = employeeProjectRepository.existsByProjectIdAndEmployeeId(projectId, request.getEmployeeId());
            if (exists) {
                continue;
            }

            EmployeeProject employeeProject = new EmployeeProject(
                    employee,
                    project,
                    Role.valueOf(request.getRole()),
                    request.getContribution()
            );

            EmployeeProject savedEmployeeProject = employeeProjectRepository.save(employeeProject);
            employeeProjectIds.add(savedEmployeeProject.getId());
        }

        return employeeProjectIds;
    }

    public void changeEmployeeJoinStatus(Long projectId,
                                         Long employeeId,
                                         EmployeeProjectChangeJoinStatusRequest joinStatusRequest) {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Project with id " + projectId + " not found");
        }
        if (!employeeRepository.existsById(employeeId)) {
            throw new IllegalArgumentException("Employee with id " + employeeId + " not found");
        }
        EmployeeProject employeeProject = employeeProjectRepository.findByProjectIdAndEmployeeId(projectId, employeeId).orElse(null);
        if (employeeProject == null) {
            throw new IllegalArgumentException("Employee with id " + employeeId + " is not assigned for project with id " + projectId);
        }

        JoinStatus previousJoinStatus = employeeProject.getJoinStatus();
        JoinStatus newJoinStatus = joinStatusRequest.getJoinStatus();
        if (previousJoinStatus == newJoinStatus) {
            return;
        }

        if (newJoinStatus == JoinStatus.READY) {
            employeeProject.setJoinStatus(newJoinStatus);
            employeeProject.setJoinDate(null);
            employeeProject.setExitDate(null);
        }
        else if (newJoinStatus == JoinStatus.DOING) {
            employeeProject.setJoinStatus(newJoinStatus);
            employeeProject.setJoinDate(LocalDate.now());
            employeeProject.setExitDate(null);
        }
        else if (newJoinStatus == JoinStatus.EXITED) {
            employeeProject.setJoinStatus(newJoinStatus);
            employeeProject.setExitDate(LocalDate.now());
        }

//        if (previousJoinStatus == JoinStatus.READY && newJoinStatus == JoinStatus.DOING) {
//            employeeProject.setJoinStatus(newJoinStatus);
//            employeeProject.setJoinDate(LocalDate.now());
//        }
//        else if (previousJoinStatus == JoinStatus.DOING && newJoinStatus == JoinStatus.EXITED) {
//            employeeProject.setJoinStatus(newJoinStatus);
//            employeeProject.setExitDate(LocalDate.now());
//        }
//
//        else if (previousJoinStatus == JoinStatus.EXITED && newJoinStatus == JoinStatus.DOING) {
//            employeeProject.setJoinStatus(newJoinStatus);
//            employeeProject.setExitDate(null);
//        }
//
//        // 추가 논의 필요
//        else if (previousJoinStatus == JoinStatus.DOING && newJoinStatus == JoinStatus.READY) {
//            employeeProject.setJoinStatus(newJoinStatus);
//            employeeProject.setJoinDate(null);
//        }
//        else if (previousJoinStatus == JoinStatus.READY && newJoinStatus == JoinStatus.EXITED) {
//            employeeProject.setJoinStatus(newJoinStatus);
//            employeeProject.setExitDate(null);
//        }



        employeeProjectRepository.save(employeeProject);
    }


    public void changeEmployeeProjectRole(Long projectId, Long employeeId, EmployeeProjectChangeRoleRequest request) {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Project with id " + projectId + " not found");
        }
        if (!employeeRepository.existsById(employeeId)) {
            throw new IllegalArgumentException("Employee with id " + employeeId + " not found");
        }
        EmployeeProject employeeProject = employeeProjectRepository.findByProjectIdAndEmployeeId(projectId, employeeId).orElse(null);
        if (employeeProject == null) {
            throw new IllegalArgumentException("Employee with id " + employeeId + " is not assigned for project with id " + projectId);
        }

        employeeProject.setRole(request.getRole());
        employeeProjectRepository.save(employeeProject);
    }


    @Transactional
    public void removeEmployeesFromProject(Long projectId, Long employeeId) {
        if (!projectRepository.existsById(projectId)) {
            throw new IllegalArgumentException("Project with id " + projectId + " not found");
        }
        if (!employeeRepository.existsById(employeeId)) {
            throw new IllegalArgumentException("Employee with id " + employeeId + " not found");
        }

        EmployeeProject employeeProject = employeeProjectRepository.findByProjectIdAndEmployeeId(projectId, employeeId).orElse(null);
        if (employeeProject == null) {
            throw new IllegalArgumentException("Employee with id " + employeeId + " is not assigned for project with id " + projectId);
        }

        if (employeeProject.getJoinStatus() == JoinStatus.DOING) {
            throw new IllegalStateException(
                    "Only READY, EXITED status employee can be removed from project.");
        }


        employeeProjectRepository.delete(employeeProject);
    }
}
