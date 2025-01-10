package danang03.STBackend.domain.employee;

import danang03.STBackend.config.auth.dto.SigninRequest;
import danang03.STBackend.domain.employee.dto.AddEmployeeRequest;
import danang03.STBackend.domain.employee.dto.EmployeeResponse;
import danang03.STBackend.domain.employee.dto.UpdateEmployeeRequest;
import danang03.STBackend.domain.image.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final S3Service s3Service;


    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, S3Service s3Service) {
        this.employeeRepository = employeeRepository;
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

    public Page<EmployeeResponse> getEmployees(Pageable pageable) {
        return employeeRepository.findAll(pageable)
                .map(employee -> new EmployeeResponse(
                        employee.getId(),
                        employee.getName(),
                        employee.getEmail(),
                        employee.getContact(),
                        employee.getSkills(),
                        employee.getJoiningDate(),
                        employee.getRole(),
                        employee.getImageUrl()
                ));
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
        employeeRepository.deleteById(id);
    }




    /****************
     **** image  ****
     ****************/

    @Transactional
    public String updateEmployeeImage(Long employeeId, MultipartFile imageFile) {
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
