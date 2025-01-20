package danang03.STBackend.domain.employee;

import danang03.STBackend.domain.employee.dto.AddEmployeeRequest;
import danang03.STBackend.domain.employee.dto.EmployeeDetailResponse;
import danang03.STBackend.domain.employee.dto.EmployeeResponse;
import danang03.STBackend.domain.employee.dto.EmployeeSimpleResponse;
import danang03.STBackend.domain.employee.dto.UpdateEmployeeRequest;
import danang03.STBackend.domain.image.S3Service;
import danang03.STBackend.domain.projects.EmployeeProject;
import danang03.STBackend.domain.projects.EmployeeProjectRepository;
import danang03.STBackend.domain.projects.Project;
import danang03.STBackend.domain.projects.dto.EmployeeProjectResponse;
import danang03.STBackend.domain.projects.dto.ProjectResponse;
import danang03.STBackend.domain.projects.dto.ProjectResponseForEmployeeDetail;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeProjectRepository employeeProjectRepository;
    private final S3Service s3Service;


    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, EmployeeProjectRepository employeeProjectRepository, S3Service s3Service) {
        this.employeeRepository = employeeRepository;
        this.employeeProjectRepository = employeeProjectRepository;
        this.s3Service = s3Service;
    }

    public Long createEmployee(AddEmployeeRequest request) {
        // 중복 email 체크
        boolean exists = employeeRepository.existsByEmail(request.getEmail());
        if (exists) {
            throw new IllegalArgumentException("Employee with the same email already exists");
        }

        String name = request.getName();
        String firstName;
        String lastName;
        if (name.contains(" ")) {
            firstName = name.split(" ")[0];
            lastName = name.split(" ")[1];
        } else {
            firstName = name;
            lastName = null;
        }
        Employee employee = Employee.builder()
                .name(name)
                .firstName(firstName)
                .lastName(lastName)
                .email(request.getEmail())
                .contact(request.getContact())
                .skills(request.getSkills())
                .role(request.getRole())
                .joiningDate(request.getJoiningDate()).build();
        employeeRepository.save(employee);

        return employee.getId();
    }

    public EmployeeResponse getEmployee(Long id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee == null) {
            throw new IllegalArgumentException("Employee with id " + id + " does not exist");
        }
        return EmployeeResponse.builder()
                .id(employee.getId())
                .name(employee.getName())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .contact(employee.getContact())
                .skills(employee.getSkills())
                .joiningDate(employee.getJoiningDate())
                .role(employee.getRole())
                .imageUrl(employee.getImageUrl()).build();
    }

    public List<EmployeeSimpleResponse> getAllEmployeesSimple(Long projectId) {
        List<Employee> allEmployees = employeeRepository.findAll();
        if (projectId == null) {
            return allEmployees.stream()
                    .map(employee -> new EmployeeSimpleResponse(
                            employee.getId(),
                            employee.getName(),
                            employee.getImageUrl())
                    ).toList();
        }

        List<Employee> assignedEmployees = employeeProjectRepository.findByProjectId(projectId).stream()
                .map(EmployeeProject::getEmployee).toList();

        List<Employee> notAssignedEmployees = allEmployees.stream()
                .filter(employee -> !assignedEmployees.contains(employee))
                .toList();

        // 프로젝트에 할당되지 않은 유저들만 필터링
        return notAssignedEmployees.stream()
                .map(employee -> new EmployeeSimpleResponse(
                        employee.getId(),
                        employee.getName(),
                        employee.getImageUrl())
                ).toList();
    }

    public EmployeeDetailResponse getEmployeeDetail(Long employeeId) {
        // employeeInfo
        EmployeeResponse employeeResponse = getEmployee(employeeId);

        // projectsInfo
        List<EmployeeProject> employeeProjects = employeeProjectRepository.findByEmployeeId(employeeId);
        List<ProjectResponseForEmployeeDetail> projectResponses = employeeProjects.stream()
                .map(employeeProject -> {
                    Project project = employeeProject.getProject();

                    // projectInfo
                    ProjectResponse projectResponse = ProjectResponse.builder()
                            .id(project.getId())
                            .name(project.getName())
                            .description(project.getDescription())
                            .startDate(project.getStartDate())
                            .endDate(project.getEndDate())
                            .status(project.getStatus()).build();

                    // employeeProjectInfo
                    EmployeeProjectResponse employeeProjectResponse = EmployeeProjectResponse.builder()
                            .roleInProject(employeeProject.getRole())
                            .contribution(employeeProject.getContribution())
                            .joinDate(employeeProject.getJoinDate())
                            .exitDate(employeeProject.getExitDate())
                            .joinStatus(employeeProject.getJoinStatus()).build();


                    return new ProjectResponseForEmployeeDetail(projectResponse, employeeProjectResponse);
                })
                .sorted(Comparator.comparing(
                        element -> element.getProjectInfo().getStartDate(),
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .toList();
        return new EmployeeDetailResponse(employeeResponse, projectResponses);
    }

    public Page<EmployeeResponse> getEmployeesByPage(Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(
                        Sort.Order.desc("joiningDate"), // startDate 기준 내림차순, null은 마지막
                        Sort.Order.asc("id")
                )
        );

        return employeeRepository.findAll(sortedPageable)
                .map(employee -> EmployeeResponse.builder()
                        .id(employee.getId())
                        .name(employee.getName())
                        .firstName(employee.getFirstName())
                        .lastName(employee.getLastName())
                        .email(employee.getEmail())
                        .contact(employee.getContact())
                        .skills(employee.getSkills())
                        .joiningDate(employee.getJoiningDate())
                        .role(employee.getRole())
                        .imageUrl(employee.getImageUrl()).build());
    }


    public Page<EmployeeResponse> searchEmployeesByPage(String keyword, Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(
                        Sort.Order.asc("name"),
                        Sort.Order.desc("id")
                )
        );

        Page<Employee> employees = employeeRepository.searchByKeyword(keyword, sortedPageable);

        List<EmployeeResponse> employeeResponses = employees.getContent().stream()
                .map(employee -> EmployeeResponse.builder()
                        .id(employee.getId())
                        .name(employee.getName())
                        .firstName(employee.getFirstName())
                        .lastName(employee.getLastName())
                        .email(employee.getEmail())
                        .contact(employee.getContact())
                        .skills(employee.getSkills())
                        .joiningDate(employee.getJoiningDate())
                        .role(employee.getRole())
                        .imageUrl(employee.getImageUrl()).build()
                ).toList();

        return new PageImpl<>(employeeResponses, pageable, employees.getTotalElements());
    }


    @Transactional
    public void updateEmployee(Long id, UpdateEmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee with id " + id + "not found"));

        // 업데이트 메서드 호출
        employee.update(
                request.getName(),
                request.getEmail(),
                request.getContact(),
                request.getSkills(),
                request.getJoiningDate(),
                request.getRole()
        );

        employeeRepository.save(employee);
    }



    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new IllegalArgumentException("employee with id " + id + " not found");
        }

        // check employee in project
        boolean hasProjects = employeeProjectRepository.existsByEmployeeId(id);
        if (hasProjects) {
            throw new IllegalStateException("Employee with id " + id + " cannot be deleted because it has associated projects.");
        }
        employeeRepository.deleteById(id);
    }




    /****************
     **** image  ****
     ****************/

    @Transactional
    public String uploadEmployeeImage(Long employeeId, MultipartFile imageFile) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee id " + employeeId + " not found"));

        // 기존 이미지 삭제
        if (employee.getImageUrl() != null) {
            s3Service.deleteFile(employee.getImageUrl());
        }

        // 새로운 이미지 업로드
        String uploadedImageUrl = s3Service.uploadFile(imageFile);
        // employee 엔티티에 url 업데이트
        employee.updateImage(uploadedImageUrl);

        employeeRepository.save(employee);
        return uploadedImageUrl;
    }

    @Transactional
    public void deleteEmployeeImage(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee id " + employeeId + " not found"));

        // 기존 이미지 삭제
        if (employee.getImageUrl() != null) {
            s3Service.deleteFile(employee.getImageUrl());
            employee.updateImage(null);
            employeeRepository.save(employee);
        }
    }

}
