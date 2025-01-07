package danang03.STBackend.domain.employee;

import danang03.STBackend.domain.employee.dto.AddEmployeeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Long createEmployee(AddEmployeeRequest request) {
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


        // save 호출 없이 변경 자동 반영 (Transactional로 관리 중)
        return employee.getId();
    }

}
