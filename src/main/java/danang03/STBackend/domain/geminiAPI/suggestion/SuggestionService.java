package danang03.STBackend.domain.geminiAPI.suggestion;

import danang03.STBackend.domain.employee.EmployeeRepository;
import danang03.STBackend.domain.projects.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SuggestionService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    // Define relevant keywords for specific fields
    private static final List<String> PHONE_KEYWORDS = List.of("phone", "number", "contact");
    private static final List<String> EMAIL_KEYWORDS = List.of("email", "mail");
    private static final List<String> SKILLS_KEYWORDS = List.of("skill", "experience");
    private static final List<String> PROJECT_KEYWORDS = List.of("project", "projects", "working on");

    public List<String> getSuggestions(String input) {
        List<String> suggestions = new ArrayList<>();
        Pageable limit = PageRequest.of(0, 5);

        // Convert input to lowercase for consistent processing
        String normalizedInput = input.toLowerCase();

        // Split input into words to separate name and keywords
        String[] words = normalizedInput.split("\\s+");
        String namePart = Arrays.stream(words)
                .filter(word -> !PHONE_KEYWORDS.contains(word) &&
                        !EMAIL_KEYWORDS.contains(word) &&
                        !SKILLS_KEYWORDS.contains(word) &&
                        !PROJECT_KEYWORDS.contains(word))
                .reduce((a, b) -> a + " " + b) // Rejoin name parts
                .orElse("");

        String keywordPart = Arrays.stream(words)
                .filter(word -> PHONE_KEYWORDS.contains(word) ||
                        EMAIL_KEYWORDS.contains(word) ||
                        SKILLS_KEYWORDS.contains(word) ||
                        PROJECT_KEYWORDS.contains(word))
                .reduce((a, b) -> a + " " + b) // Rejoin keyword parts
                .orElse("");

        // Detect keywords for filtering suggestions
        boolean isPhoneQuery = containsKeyword(keywordPart, PHONE_KEYWORDS);
        boolean isEmailQuery = containsKeyword(keywordPart, EMAIL_KEYWORDS);
        boolean isSkillsQuery = containsKeyword(keywordPart, SKILLS_KEYWORDS);
        boolean isProjectQuery = containsKeyword(keywordPart, PROJECT_KEYWORDS);

        // Name-based suggestions
        List<String> nameSuggestions = employeeRepository.findNamesStartingWith(namePart, limit);
        for (String name : nameSuggestions) {
            if (isPhoneQuery) {
                suggestions.add("What is " + name + "'s phone number?");
                suggestions.add("Retrieve contact details of " + name + ".");
            } else if (isEmailQuery) {
                suggestions.add("What is " + name + "'s email address?");
            } else if (isSkillsQuery) {
                suggestions.add("What skills does " + name + " possess?");
                suggestions.add("Does " + name + " have experience in ");
            } else if (isProjectQuery) {
                suggestions.add("What projects is " + name + " working on?");
                suggestions.add("List all projects " + name + " has worked on.");
            } else {
                // General name-based suggestions
                suggestions.add("What is " + name + "'s role?");
                suggestions.add("What position does " + name + " hold in the company?");
                suggestions.add("When did " + name + " join the company?");
            }
        }

        // Project-based suggestions
        List<String> projectSuggestions = projectRepository.findNamesStartingWith(normalizedInput, limit);
        for (String project : projectSuggestions) {
            suggestions.add("Show details for project: " + project + ".");
            suggestions.add("What is the status of the " + project + "project?");
            suggestions.add("What is the start and end date of " + project + "?");

            suggestions.add("Who is managing " + project + "?");
            suggestions.add("Who is currently working on " + project + "?");
            suggestions.add("What roles are assigned in " + project + "?");

            suggestions.add("Which projects are currently pending " + project + "?");
            suggestions.add("What are the recently completed " + project + "?");

            suggestions.add("What skills are required for " + project + "?");
            suggestions.add("What contributions have employees made to " + project + "?");

        }

        // Simple keyword-based suggestions
        if (nameSuggestions.isEmpty() && projectSuggestions.isEmpty()) {
            if (!normalizedInput.isBlank() && 2 < normalizedInput.length() && normalizedInput.split("\\s+").length <= 3) {
                if (normalizedInput.startsWith("what")) {
                    suggestions.add("What is the phone number of ");
                    suggestions.add("What is the email of ");
                    suggestions.add("What projects are currently pending?");
                } else if (normalizedInput.startsWith("who")) {
                    suggestions.add("Who is managing the project?");
                    suggestions.add("Who has skills in ");
                    suggestions.add("Who is currently assigned to the project?");
                } else {
                    suggestions.add("Search for '" + input + "' in all employee records.");
                    suggestions.add("List all projects containing '" + input + "'.");
                    suggestions.add("Find people with the skill of '" + input + "'.");
                }
            }
        }
        return suggestions.stream().limit(5).collect(Collectors.toList());
    }


    // Helper method to check if input contains any keyword from the list
    private boolean containsKeyword(String input, List<String> keywords) {
        for (String keyword : keywords) {
            if (input.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
