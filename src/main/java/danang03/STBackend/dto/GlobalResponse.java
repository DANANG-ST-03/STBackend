package danang03.STBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GlobalResponse {
    private int status;
    private String message;
    private Object data;

    public String toString() {
        return "(status=" + status + ", message=" + message + ", data=" + data.toString() + ")";
    }
}
