package danang03.STBackend.domain.geminiAPI;

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
    public ResponseEntity<String> processQuery(
            @RequestHeader("Session-ID") String sessionId,
            @RequestBody String naturalLanguagePrompt) {
        // Add input to session history
        geminiService.addToSessionHistory(sessionId, naturalLanguagePrompt);

        // Process the query
        String response = geminiService.processNaturalLanguageQuery(naturalLanguagePrompt, sessionId);
        geminiService.addToSessionHistory(sessionId, response);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/session-history")
    public ResponseEntity<List<String>> getSessionHistory(@RequestHeader("Session-ID") String sessionId) {
        return ResponseEntity.ok(geminiService.getSessionHistory(sessionId));
    }

    @DeleteMapping("/session-history")
    public ResponseEntity<Void> clearSessionHistory(@RequestHeader("Session-ID") String sessionId) {
        geminiService.clearSessionHistory(sessionId);
        return ResponseEntity.noContent().build();
    }
}

