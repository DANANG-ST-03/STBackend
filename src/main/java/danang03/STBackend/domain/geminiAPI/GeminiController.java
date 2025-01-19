package danang03.STBackend.domain.geminiAPI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import danang03.STBackend.dto.GlobalResponse;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.instrument.classloading.glassfish.GlassFishLoadTimeWeaver;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
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
            @RequestBody String rawJson) throws JsonProcessingException { // JSON 요청을 String으로 받아보기 (디버깅)

        log.info("Session-ID (Raw): '{}' (Type: {})", sessionId, sessionId.getClass().getSimpleName());

        log.info("Received raw JSON: {}", rawJson); // JSON 구조 확인

        // JSON을 수동으로 객체로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        GeminiRequest naturalLanguagePrompt;
        naturalLanguagePrompt = objectMapper.readValue(rawJson, GeminiRequest.class);

//        // 입력값 검증
//        if (naturalLanguagePrompt == null ||
//                naturalLanguagePrompt.getContents() == null ||
//                naturalLanguagePrompt.getContents().isEmpty() ||
//                naturalLanguagePrompt.getContents().get(0).getParts() == null ||
//                naturalLanguagePrompt.getContents().get(0).getParts().isEmpty() ||
//                naturalLanguagePrompt.getContents().get(0).getParts().get(0).getText() == null) {
//            throw new IllegalArgumentException("Invalid JSON structure: missing required fields");
//        }


        String userInput = naturalLanguagePrompt.getContents().get(0).getParts().get(0).getText();
        log.info("Extracted User Input: {}", userInput);
        geminiService.addToSessionHistory(sessionId, userInput);

        // AI 처리
        String response = geminiService.processNaturalLanguageQuery(userInput, sessionId);
        geminiService.addToSessionHistory(sessionId, response);

        System.out.println("@@@@@@@Session history: " + geminiService.getSessionHistory(sessionId));
        return ResponseEntity.ok(
                GlobalResponse.builder()
                        .status(200)
                        .message("process query success")
                        .data(response)
                        .build()
        );
    }







    @GetMapping("/session-history")
    public ResponseEntity<GlobalResponse> getSessionHistory(@RequestHeader("Session-ID") String sessionId) {
        log.info("Session-ID (Raw): '{}' (Type: {})", sessionId, sessionId.getClass().getSimpleName());

        List<String> result = geminiService.getSessionHistory(sessionId);
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("history get success")
                .data(result).build();
        return ResponseEntity.ok(globalResponse);
    }

    @DeleteMapping("/session-history")
    public ResponseEntity<GlobalResponse> clearSessionHistory(@RequestHeader("Session-ID") String sessionId) {
        geminiService.clearSessionHistory(sessionId);
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(200)
                .message("history delete success")
                .data(null).build();
        return ResponseEntity.ok(globalResponse);
    }
}

