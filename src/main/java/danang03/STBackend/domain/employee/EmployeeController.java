package danang03.STBackend.domain.employee;

import static danang03.STBackend.domain.image.ImageValidation.validateImgage;

import danang03.STBackend.domain.employee.dto.AddEmployeeRequest;
import danang03.STBackend.domain.employee.dto.AddEmployeeResponse;
import danang03.STBackend.domain.employee.dto.EmployeeDetailResponse;
import danang03.STBackend.domain.employee.dto.EmployeeResponse;
import danang03.STBackend.domain.employee.dto.EmployeeSimpleResponse;
import danang03.STBackend.domain.employee.dto.UpdateEmployeeRequest;
import danang03.STBackend.domain.employee.dto.UpdateEmployeeResponse;
import danang03.STBackend.dto.GlobalResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/employee")
public class EmployeeController {
    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeService employeeService;
    private final TemplateEngine templateEngine;


    @Autowired
    public EmployeeController(EmployeeService employeeService, TemplateEngine templateEngine) {
        this.employeeService = employeeService;
        this.templateEngine = templateEngine;
    }

    @PostMapping
    public ResponseEntity<GlobalResponse> addEmployee(@RequestBody AddEmployeeRequest request) {
        Long employeeId = employeeService.createEmployee(request);
        log.info("Add employee with id {}", employeeId);
        AddEmployeeResponse addEmployeeResponse = new AddEmployeeResponse(employeeId);
        log.info("Add employee response: {}", addEmployeeResponse);
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("add employee success")
                .data(addEmployeeResponse).build();
        log.info("Add employee response: {}", globalResponse);
        return ResponseEntity.ok(globalResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GlobalResponse> getOneEmployee(@PathVariable Long id) {
        EmployeeResponse employee = employeeService.getEmployee(id);
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("get Employees id " + id + " success")
                .data(employee).build();
        return ResponseEntity.ok(globalResponse);
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<GlobalResponse> getEmployeeDetail(@PathVariable Long id) {
        EmployeeDetailResponse employeeDetail = employeeService.getEmployeeDetail(id);
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("get Employee id " + id + " detail success")
                .data(employeeDetail).build();
        return ResponseEntity.ok(globalResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<GlobalResponse> getAllEmployeesSimple(@RequestParam(required = false) Long projectId) {
        List<EmployeeSimpleResponse> responses = employeeService.getAllEmployeesSimple(projectId);
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("get Employees success")
                .data(responses).build();
        return ResponseEntity.ok(globalResponse);
    }

    @GetMapping
    public ResponseEntity<GlobalResponse> getEmployeesByPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Pageable pageable) {
        Page<EmployeeResponse> employees = employeeService.getEmployeesByPage(PageRequest.of(page, size));
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("get Employees success")
                .data(employees).build();
        return ResponseEntity.ok(globalResponse);
    }

    @PutMapping("{id}")
    public ResponseEntity<GlobalResponse> updateEmployee(@PathVariable Long id, @RequestBody UpdateEmployeeRequest request) {
        employeeService.updateEmployee(id, request);
//        log.info("Update employee with id {}", id);
        UpdateEmployeeResponse updateEmployeeResponse = new UpdateEmployeeResponse(id);
//        log.info("Update employee response: {}", updateEmployeeResponse);
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("updated employee successfully")
                .data(updateEmployeeResponse).build();
        log.info("Update employee response: {}", globalResponse);

        return ResponseEntity.ok(globalResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GlobalResponse> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("deleted employee successfully")
                .data(null).build();
        return ResponseEntity.ok(globalResponse);
    }



    /****************
     **** image  ****
     ****************/

    @PostMapping("/{id}/image")
    public ResponseEntity<GlobalResponse> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile imageFile) {
        validateImgage(imageFile);

        String uploadedImageUrl = employeeService.uploadEmployeeImage(id, imageFile);

        GlobalResponse response = GlobalResponse.builder()
                .status(200)
                .message("Uploaded image successfully")
                .data(uploadedImageUrl)
                .build();
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{id}/image")
    public ResponseEntity<GlobalResponse> deleteEmployeeImage(@PathVariable Long id) {
        employeeService.deleteEmployeeImage(id);

        GlobalResponse response = GlobalResponse.builder()
                .status(200)
                .message("Deleted image successfully")
                .build();
        return ResponseEntity.ok(response);
    }




    /****************
     ****** CV  *****
     ****************/

    @GetMapping("/{id}/cv")
    public void employeeCV(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
        // 1) 서비스 통해 데이터 가져오기
        EmployeeDetailResponse detailResponse = employeeService.getEmployeeDetail(id);

        // Thymeleaf Context에 데이터 설정
        Context context = new Context();
        context.setVariable("employeeDetail", detailResponse);
        // 필요한 데이터들을 추가로 setVariable() ...

        // 2) Thymeleaf 템플릿 -> HTML 문자열 변환
        String htmlContent = templateEngine.process("resume", context);

        // 3) HTML -> PDF (Flying Saucer iTextRenderer 사용)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(htmlContent);

        // 혹시 템플릿 내에서 이미지나 CSS 파일 등을 상대 경로로 참조한다면
        // renderer.getSharedContext().setBaseURL("file:/템플릿_리소스_경로/");
        // 또는 classpath 기반 경로 설정 등 필요

        renderer.layout();
        renderer.createPDF(baos);

        byte[] pdfBytes = baos.toByteArray();

        // 4) 브라우저로 PDF 내보내기 (다운로드 응답)
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"resume.pdf\"");
        response.setContentLength(pdfBytes.length);

        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }
}
