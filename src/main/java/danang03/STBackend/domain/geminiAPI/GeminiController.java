package danang03.STBackend.domain.geminiAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {
    private final GeminiService geminiService;

    @Autowired
    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/generate-sql")
    public ResponseEntity<String> generateSql(@RequestBody String prompt) {
        String res;
        res = geminiService.generateResponse(prompt);
        return ResponseEntity.ok(res);
        //String sql;
        //sql = geminiService.generateSql(prompt);
        //return ResponseEntity.ok(sql);
    }



    @PostMapping("/generate-report")
    public ResponseEntity<String> generateEmployeeReport(@RequestBody String prompt) {
        String response = geminiService.generateEmployeeReport(prompt);
        return ResponseEntity.ok(response);
    }
}

