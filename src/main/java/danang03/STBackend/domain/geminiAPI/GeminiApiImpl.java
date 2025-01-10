package danang03.STBackend.domain.geminiAPI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class GeminiApiImpl implements GeminiApi {

    private final RestTemplate restTemplate;

    @Autowired
    public GeminiApiImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public GeminiResponse generateContent(String model, GeminiRequest request, String apiKey) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                + model + ":generateContent?key=" + apiKey;

        try {
            // 요청 JSON 로그 출력
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonRequest = objectMapper.writeValueAsString(request);
            System.out.println("Generated JSON: " + jsonRequest);

            HttpEntity<GeminiRequest> entity = new HttpEntity<>(request);

            // RestTemplate 요청
            return restTemplate.postForObject(
                    url,
                    entity,
                    GeminiResponse.class
            );
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Failed to call Gemini API: " + e.getMessage(), e);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to process JSON: " + e.getMessage(), e);
        }
    }
}
