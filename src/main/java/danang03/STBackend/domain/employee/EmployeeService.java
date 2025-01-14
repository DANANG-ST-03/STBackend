package danang03.STBackend.domain.employee;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.aspectj.weaver.ast.Literal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
                }).toList();
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



    /****************
     ****** CV  *****
     ****************/

    public File makeCV(Long employeeId) throws IOException {
        final String templatePath = "src/main/resources/cv/cvTemplate.pdf"; // Canva에서 다운로드한 PDF 경로
        final String outputPath = "src/main/resources/cv/output.pdf"; // 데이터가 삽입된 결과 PDF 경로

        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee == null) {
            throw new IllegalArgumentException("Employee with id " + employeeId + " does not exist");
        }

        // 기존 PDF 템플릿 읽기
        PdfReader pdfReader = new PdfReader(templatePath);
        PdfWriter pdfWriter = new PdfWriter(outputPath);
        PdfDocument pdfDoc = new PdfDocument(pdfReader, pdfWriter);
        Document document = new Document(pdfDoc);

        // 데이터 삽입
        PdfPage page = pdfDoc.getPage(1); // 첫 번째 페이지 선택

        // 이름 추가
        document.showTextAligned(new Paragraph("Stefano Accorsi"),
                100, 700, 1, TextAlignment.LEFT, VerticalAlignment.TOP, 0);

        // 연락처 추가
        document.showTextAligned(new Paragraph("Phone: +123-456-7890"),
                100, 680, 1, TextAlignment.LEFT, VerticalAlignment.TOP, 0);

        // 작업 경험 추가
        document.showTextAligned(new Paragraph("Human Resource Project | 2037.07-2037.10"),
                100, 640, 1, TextAlignment.LEFT, VerticalAlignment.TOP, 0);

        // 문서 닫기
        document.close();
        System.out.println("PDF에 데이터 삽입 완료: " + outputPath);

        return new File(outputPath);
    }
}
