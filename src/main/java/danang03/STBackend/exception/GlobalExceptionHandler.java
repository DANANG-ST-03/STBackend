package danang03.STBackend.exception;

import danang03.STBackend.dto.GlobalResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.View;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final View error;

    public GlobalExceptionHandler(View error) {
        this.error = error;
    }

    // 잘못된 JSON 형식 처리
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<GlobalResponse> handleInvalidJson(HttpMessageNotReadableException ex) {
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(400)
                .message("wrong json form.")
                .data(null).build();
        return ResponseEntity.badRequest().body(globalResponse);
    }

    // 유효성 검사 실패 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<GlobalResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(400)
                .message(errorMessage)
                .data(null).build();
        return ResponseEntity.badRequest().body(globalResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<GlobalResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(409)
                .message(ex.getMessage())
                .data(null)
                .build();
        return ResponseEntity.status(409).body(globalResponse);
    }


    // 기타 예외 처리
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<GlobalResponse> handleAllExceptions(Exception ex) {
        GlobalResponse globalResponse = GlobalResponse.builder()
                .status(500)
                .message(ex.getMessage())
                .data(null).build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(globalResponse);
    }
}