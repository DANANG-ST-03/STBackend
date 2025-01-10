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

    @PostMapping("/process-query")
    public ResponseEntity<String> processQuery(@RequestBody String naturalLanguagePrompt) {
        String response = geminiService.processNaturalLanguageQuery(naturalLanguagePrompt);
        return ResponseEntity.ok(response);
    }
}

