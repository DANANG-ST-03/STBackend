package danang03.STBackend.domain.geminiAPI;


import danang03.STBackend.domain.employee.EmployeeService;
import danang03.STBackend.domain.employee.dto.EmployeeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    private final GeminiApi geminiApi;
    private final EmployeeService employeeService;

    public GeminiService(GeminiApi geminiApi, EmployeeService employeeService) {
        this.geminiApi = geminiApi;
        this.employeeService = employeeService;
    }


    public String generateResponse(String promptText) {
        GeminiRequest request = new GeminiRequest(promptText);
        GeminiResponse response = geminiApi.generateContent(model, request, apiKey);

        return response.getCandidates().stream()
                .findFirst()
                .map(candidate -> candidate.getContent().getParts().get(0).getText())
                .orElse("No response received from Gemini API");
    }

    public String generateEmployeeReport(String promptText) {
        GeminiRequest request = new GeminiRequest(promptText);
        // 전체 Employee DB 조회
        List<EmployeeResponse> employees = employeeService.getEmployeesByPage(Pageable.unpaged()).getContent();

        // 직원 정보를 요청 본문으로 변환
        StringBuilder promptBuilder = new StringBuilder("Generate a report about the following employees:\n");
        for (EmployeeResponse employee : employees) {
            promptBuilder.append("- Name: ").append(employee.getName())
                    .append(", Email: ").append(employee.getEmail())
                    .append(", Role: ").append(employee.getRole())
                    .append(", Skills: ").append(employee.getSkills())
                    .append("\n");
        }

        // Gemini API 요청 생성
        GeminiResponse response = geminiApi.generateContent(model, request, apiKey);

        // 응답 반환
        return response.getCandidates().stream()
                .findFirst()
                .map(candidate -> candidate.getContent().getParts().get(0).getText())
                .orElse("No response received from Gemini API");
    }

//
//    public String generateEmployeeReport() {
//
//        // 데이터베이스에서 직원 정보 조회
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<EmployeeResponse> employees = employeeService.getEmployees(pageable);
//        // 직원 정보를 요청 본문으로 변환
//        StringBuilder promptBuilder = new StringBuilder("Generate a report about the following employees:\n");
//        for (EmployeeResponse employee : employees) {
//            promptBuilder.append("- Name: ").append(employee.getName())
//                    .append(", Email: ").append(employee.getEmail())
//                    .append(", Role: ").append(employee.getRole())
//                    .append(", Skills: ").append(employee.getSkills())
//                    .append("\n");
//        }
//
//        // Gemini API 요청 생성
//        GeminiRequest request = new GeminiRequest(promptBuilder.toString());
//        GeminiResponse response = geminiApi.generateContent(model, request, apiKey);
//
//        // 응답 반환
//        return response.getCandidates().stream()
//                .findFirst()
//                .map(candidate -> candidate.getContent().getParts().get(0).getText())
//                .orElse("No response received from Gemini API");
//    }


//    public String generateSql(String promptText) {
//        GeminiRequest request = new GeminiRequest(promptText);
//        GeminiResponse response = geminiApi.generateContent(model, request, apiKey);
//
//        return response.getCandidates().stream()
//                .findFirst()
//                .map(candidate -> candidate.getContent().getParts().get(0).getText())
//                .orElse("No response received from Gemini API");
//    }
}
