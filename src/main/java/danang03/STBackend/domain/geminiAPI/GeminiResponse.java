package danang03.STBackend.domain.geminiAPI;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter

public class GeminiResponse {
    private List<Candidate> candidates;

    @Setter
    @Getter
    public static class Candidate {
        private Content content;
    }

    @Setter
    @Getter
    public static class Content {
        private List<TextPart> parts;
    }

    @Setter
    @Getter
    public static class TextPart {
        private String text;
    }
}
