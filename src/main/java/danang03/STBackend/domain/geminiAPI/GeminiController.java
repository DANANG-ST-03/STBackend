package danang03.STBackend.domain.geminiAPI;

import danang03.STBackend.dto.GlobalResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/gemini")
public class GeminiController {
    private final GeminiService geminiService;

    @Autowired
    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }
    @PostMapping("/process-query")
    public ResponseEntity<GlobalResponse> processQuery(
            @RequestHeader("Session-ID") String sessionId,
            @RequestBody String naturalLanguagePrompt) {
        // Add input to session history
        geminiService.addToSessionHistory(sessionId, naturalLanguagePrompt);

        // Process the query
        String response = geminiService.processNaturalLanguageQuery(naturalLanguagePrompt, sessionId);
        geminiService.addToSessionHistory(sessionId, response);

        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("successfully got answer from gemini")
                .data(response).build();
        return ResponseEntity.ok(globalResponse);
    }

    @GetMapping("/session-history")
    public ResponseEntity<GlobalResponse> getSessionHistory(@RequestHeader("Session-ID") String sessionId) {
        List<String> sessionHistory = geminiService.getSessionHistory(sessionId);
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("successfully got session history")
                .data(sessionHistory).build();
        return ResponseEntity.ok(globalResponse);
    }

    @DeleteMapping("/session-history")
    public ResponseEntity<GlobalResponse> clearSessionHistory(@RequestHeader("Session-ID") String sessionId) {
        geminiService.clearSessionHistory(sessionId);
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("deleted session history")
                .data(null).build();
        return ResponseEntity.ok(globalResponse);
    }
}

