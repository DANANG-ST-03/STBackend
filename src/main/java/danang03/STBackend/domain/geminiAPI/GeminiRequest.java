package danang03.STBackend.domain.geminiAPI;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class GeminiRequest {
    private List<Content> contents; // 배열 대신 List 사용

    public GeminiRequest(String text) {
        this.contents = List.of(new Content(text));
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Content {
        private List<Part> parts; // 배열 대신 List 사용

        public Content(String text) {
            this.parts = List.of(new Part(text));
        }
    }

    @Setter
    @Getter
    @NoArgsConstructor
    public static class Part {
        private String text;

        public Part(String text) {
            this.text = text;
        }
    }
}