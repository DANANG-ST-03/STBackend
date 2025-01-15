package danang03.STBackend.domain.geminiAPI.suggestion;

import danang03.STBackend.dto.GlobalResponse;
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
    public ResponseEntity<GlobalResponse> getSuggestions(@RequestBody Map<String, String> input) {
        String query = input.get("input");
        List<String> suggestions = suggestionService.getSuggestions(query);
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("successfully got text suggestion")
                .data(suggestions).build();
        return ResponseEntity.ok(globalResponse);
    }
}
