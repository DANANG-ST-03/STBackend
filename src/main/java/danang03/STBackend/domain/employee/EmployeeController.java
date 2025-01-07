package danang03.STBackend.domain.employee;

import danang03.STBackend.domain.employee.dto.AddEmployeeRequest;
import danang03.STBackend.domain.employee.dto.AddEmployeeResponse;
import danang03.STBackend.dto.GlobalResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<GlobalResponse> addEmployee(AddEmployeeRequest request) {
        Long employeeId = employeeService.createEmployee(request);
        AddEmployeeResponse addEmployeeResponse = new AddEmployeeResponse(employeeId);
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("add employee success")
                .data(addEmployeeResponse).build();

        return ResponseEntity.ok(globalResponse);
    }
}
