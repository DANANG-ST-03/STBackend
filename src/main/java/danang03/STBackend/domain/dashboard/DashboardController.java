package danang03.STBackend.domain.dashboard;

import danang03.STBackend.domain.dashboard.dto.DashboardResponse;
import danang03.STBackend.dto.GlobalResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);
    private final DashboardService dashboardService;
    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<GlobalResponse> getDashboard() {
        log.info("get dashboard controller");
        DashboardResponse response = dashboardService.getDashboard();

        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("got dashboard info")
                .data(response).build();
        return ResponseEntity.ok(globalResponse);
    }
}
