package danang03.STBackend.domain.employee;

import danang03.STBackend.domain.employee.dto.AddEmployeeRequest;
import danang03.STBackend.domain.employee.dto.AddEmployeeResponse;
import danang03.STBackend.domain.employee.dto.UpdateEmployeeRequest;
import danang03.STBackend.domain.employee.dto.UpdateEmployeeResponse;
import danang03.STBackend.dto.GlobalResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<GlobalResponse> addEmployee(@RequestBody AddEmployeeRequest request) {
        Long employeeId = employeeService.createEmployee(request);
        AddEmployeeResponse addEmployeeResponse = new AddEmployeeResponse(employeeId);
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("add employee success")
                .data(addEmployeeResponse).build();

        return ResponseEntity.ok(globalResponse);
    }

    @PutMapping("{id}")
    public ResponseEntity<GlobalResponse> updateEmployee(@PathVariable Long id, @RequestBody UpdateEmployeeRequest request) {
        employeeService.updateEmployee(id, request);
        UpdateEmployeeResponse updateEmployeeResponse = new UpdateEmployeeResponse(id);
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("updated employee successfully")
                .data(updateEmployeeResponse).build();

        return ResponseEntity.ok(globalResponse);
    }

    // delete api
    @DeleteMapping("/{id}")
    public ResponseEntity<GlobalResponse> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("deleted employee successfully")
                .data(null).build();
        return ResponseEntity.ok(globalResponse);
    }
}
