package danang03.STBackend.domain.geminiAPI.suggestion;

import danang03.STBackend.domain.employee.EmployeeRepository;
import danang03.STBackend.domain.projects.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SuggestionService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    public List<String> getSuggestions(String input) {
        List<String> suggestions = new ArrayList<>();
        Pageable limit = PageRequest.of(0, 5);

        // Name-based suggestions
        List<String> nameSuggestions = employeeRepository.findNamesStartingWith(input,limit);
        for (String name : nameSuggestions) {
            suggestions.add("What is " + name + "'s role?");
            suggestions.add("Retrieve contact details of " + name + ".");
            suggestions.add("What projects is " + name + " working on?");
        }

        // Project-based suggestions
        List<String> projectSuggestions = projectRepository.findNamesStartingWith(input, limit);
        for (String project : projectSuggestions) {
            suggestions.add("Show details for project: " + project + ".");
            suggestions.add("Who is working on " + project + "?");
        }

        // Simple keyword-based suggestions
        if (!input.isBlank()) {
            suggestions.add("Search for '" + input + "' in all employee records.");
            suggestions.add("List all projects containing '" + input + "'.");
            suggestions.add("Find people with the skill of '" + input + "'.");
        }

        return suggestions;
    }
}
