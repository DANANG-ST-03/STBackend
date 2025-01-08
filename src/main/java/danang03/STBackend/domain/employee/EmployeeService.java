package danang03.STBackend.domain.employee;

import danang03.STBackend.domain.employee.dto.AddEmployeeRequest;
import danang03.STBackend.domain.employee.dto.UpdateEmployeeRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
//                .picture(request.getPicture())
                .contact(request.getContact())
                .skills(request.getSkills())
                .joiningDate(request.getJoiningDate()).build();
        employeeRepository.save(employee);

        return employee.getId();
    }

    @Transactional
    public Long updateEmployee(UpdateEmployeeRequest request) {
        Employee employee = employeeRepository.findById(request.getId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        // 업데이트 메서드 호출
        employee.update(
                request.getName(),
                request.getEmail(),
//                request.getPicture(),
                request.getContact(),
                request.getSkills(),
                request.getJoiningDate()
        );


    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

}
