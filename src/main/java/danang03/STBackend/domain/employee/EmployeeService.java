package danang03.STBackend.domain.employee;

import danang03.STBackend.domain.employee.dto.AddEmployeeRequest;
import danang03.STBackend.domain.employee.dto.EmployeeResponse;
import danang03.STBackend.domain.employee.dto.UpdateEmployeeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
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
                .joiningDate(request.getJoiningDate()).build();
        employeeRepository.save(employee);

        return employee.getId();
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
                        employee.getRole()
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
}
