package danang03.STBackend.domain.image;

import org.springframework.web.multipart.MultipartFile;

public class ImageValidation {
    public static void validateImgage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("Invalid file type");
        }
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB 제한
            throw new IllegalArgumentException("File size exceeds limit");
        }
    }
}
