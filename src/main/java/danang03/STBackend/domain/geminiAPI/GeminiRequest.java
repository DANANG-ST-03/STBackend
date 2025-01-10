package danang03.STBackend.domain.geminiAPI;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GeminiRequest {
    private Content[] contents;

    public GeminiRequest(String text) {
        this.contents = new Content[] { new Content(text) };
    }

    @Setter
    @Getter
    public static class Content {
        private Part[] parts;

        public Content(String text) {
            this.parts = new Part[] { new Part(text) };
        }
    }

    @Setter
    @Getter
    public static class Part {
        private String text;

        public Part(String text) {
            this.text = text;
        }
    }
}
//Example of JSON file
/*
{
        "contents": [{
        "parts":[{"text": "Write a story about a magic backpack."}]
        }]
}
*/
