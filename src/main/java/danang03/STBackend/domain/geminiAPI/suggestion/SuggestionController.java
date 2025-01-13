package danang03.STBackend.domain.geminiAPI.suggestion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SuggestionController {

    @Autowired
    private SuggestionService suggestionService;

    @PostMapping("/suggest")
    public ResponseEntity<List<String>> getSuggestions(@RequestBody Map<String, String> input) {
        String query = input.get("input");
        List<String> suggestions = suggestionService.getSuggestions(query);
        return ResponseEntity.ok(suggestions);
    }
}
