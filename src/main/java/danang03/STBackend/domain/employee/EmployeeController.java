package danang03.STBackend.domain.employee;

import danang03.STBackend.domain.employee.dto.AddEmployeeRequest;
import danang03.STBackend.domain.employee.dto.AddEmployeeResponse;
import danang03.STBackend.domain.employee.dto.UpdateEmployeeRequest;
import danang03.STBackend.domain.employee.dto.UpdateEmployeeResponse;
import danang03.STBackend.dto.GlobalResponse;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/employee")
public class EmployeeController {
    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @CrossOrigin(origins = "http://localhost:5173")
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

    @PutMapping("{id}")
    public ResponseEntity<GlobalResponse> updateEmployee(@PathVariable Long id, @RequestBody UpdateEmployeeRequest request) {
        employeeService.updateEmployee(id, request);
        log.info("Update employee with id {}", id);
        UpdateEmployeeResponse updateEmployeeResponse = new UpdateEmployeeResponse(id);
        log.info("Update employee response: {}", updateEmployeeResponse);
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
}
