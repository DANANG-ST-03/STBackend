package danang03.STBackend.domain.geminiAPI;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/v1beta/models/")
public interface GeminiApi {
    @PostExchange("{model}:generateContent")
    GeminiResponse generateContent(
            @PathVariable String model,
            @RequestBody GeminiRequest request,
            @RequestHeader("Authorization") String apiKey
    );
}
