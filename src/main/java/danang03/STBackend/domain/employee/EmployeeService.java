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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
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

        Employee employee = Employee.builder()
                .name(request.getName())
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

    public List<EmployeeSimpleResponse> getAllEmployeesSimple() {
        return  employeeRepository.findAll()
                .stream().map(employee -> new EmployeeSimpleResponse(
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
                        element -> element.getProjectInfo().getStartDate()
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
