package danang03.STBackend.domain.geminiAPI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SessionScope
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    private final GeminiApi geminiApi;
    private final JdbcTemplate jdbcTemplate;
    private final Map<String, List<String>> sessionHistory = new HashMap<>();
    private List<String> schemaCache;

    public GeminiService(GeminiApi geminiApi, JdbcTemplate jdbcTemplate) {
        this.geminiApi = geminiApi;
        this.jdbcTemplate = jdbcTemplate;
    }

    // Add input to session history
    public void addToSessionHistory(String sessionId, String input) {
        sessionHistory.computeIfAbsent(sessionId, k -> new ArrayList<>()).add(input);
    }

    // Retrieve session history
    public List<String> getSessionHistory(String sessionId) {
        return sessionHistory.getOrDefault(sessionId, new ArrayList<>());
    }

    // Clear session history
    public void clearSessionHistory(String sessionId) {
        sessionHistory.remove(sessionId);
    }

    // Retrieve table schema for a given table
    public String getTableSchema(String tableName) {
        String query = "SELECT column_name, data_type FROM information_schema.columns WHERE table_name = ?";
        List<Map<String, Object>> columns = jdbcTemplate.queryForList(query, tableName);

        StringBuilder schemaBuilder = new StringBuilder("CREATE TABLE " + tableName + " (\n");
        for (Map<String, Object> column : columns) {
            schemaBuilder.append("  ")
                    .append(column.get("column_name"))
                    .append(" ")
                    .append(column.get("data_type"))
                    .append(",\n");
        }
        schemaBuilder.setLength(schemaBuilder.length() - 2);
        schemaBuilder.append("\n);");

        return schemaBuilder.toString();
    }

    // Retrieve schemas for all tables in the database
    public List<String> getAllTableSchemas() {
        String query = "SELECT table_name FROM information_schema.tables " +
                "WHERE table_schema = 'public' " +
                "AND table_name NOT IN ('member', 'auth_users')";
        List<Map<String, Object>> tables = jdbcTemplate.queryForList(query);

        List<String> schemas = new ArrayList<>();
        for (Map<String, Object> table : tables) {
            String tableName = (String) table.get("table_name");
            schemas.add(getTableSchema(tableName));
        }
        return schemas;
    }

    private void initializeSchemaCache() {
        if (schemaCache == null) {
            schemaCache = getAllTableSchemas();
        }
    }

    public String generateSQLFromPrompt(String naturalLanguagePrompt, List<String> recentHistory) {
        initializeSchemaCache();
        StringBuilder schemaBuilder = new StringBuilder("Based on the following table schemas:\n\n");
        for (String schema : schemaCache) {
            schemaBuilder.append(schema).append("\n\n");
        }

        StringBuilder historyContext = new StringBuilder("Refer to the session history for additional context:\n");
        for (String historyItem : recentHistory) {
            historyContext.append(historyItem).append("\n");
        }

        String fullPrompt = schemaBuilder.toString() +
                historyContext +
                "Use LOWER() or ILIKE for case-insensitive matching. " +
                "Focus on the most recent history. " +
                "If the query includes a person's name, handle it as an employee. " +
                "If it includes a project name, handle it as a project. " +
                "Otherwise, generate only the SQL query without opinions:\n" +
                naturalLanguagePrompt;

        GeminiRequest request = new GeminiRequest(fullPrompt);
        System.out.println(request);
        GeminiResponse response = geminiApi.generateContent(model, request, apiKey);

        return response.getCandidates().stream()
                .findFirst()
                .map(candidate -> candidate.getContent().getParts().get(0).getText())
                .orElse("No SQL query generated");
    }

    public List<Map<String, Object>> executeSQLQuery(String sqlQuery) {
        try {
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sqlQuery);
            if (results.isEmpty()) {
                throw new RuntimeException("No relevant data found.");
            }
            return results;
        } catch (Exception e) {
            throw new RuntimeException("Failed to find relevant data. Please check your input or restart the chatbot.");

        }
    }

    public String generateNaturalLanguageFromResults(List<Map<String, Object>> queryResults) {
        if (queryResults == null || queryResults.isEmpty()) {
            return "We couldn't find any relevant information based on your request.";
        }

        StringBuilder resultsBuilder = new StringBuilder(
                "Process the provided data as-is and convert it into natural language by listing the values clearly. " +
                        "For example:\n" +
                        "{name=Alexander Jade}\\n{name=Alice Brown} should be transformed into Alexander Jade, Alice Brown.\n" +
                        "{name=Website Revamp, description=Complete redesign of the company website}\\The project 'Website Revamp' involves a complete redesign of the company website.\n" +
                        "While avoiding assumptions or unrelated information, you may adjust the sentence structure. " +
                        "Speak in user-friendly language.\n\n"

        );
        for (Map<String, Object> row : queryResults) {
            resultsBuilder.append(row.toString()).append("\n");
        }

        GeminiRequest request = new GeminiRequest(resultsBuilder.toString());
        GeminiResponse response = geminiApi.generateContent(model, request, apiKey);

        return response.getCandidates().stream()
                .findFirst()
                .map(candidate -> candidate.getContent().getParts().get(0).getText())
                .orElse("No meaningful insights could be generated.");
    }

    public String sanitizeSQLQuery(String sqlQuery) {
        // Remove Markdown-style code block syntax
        return sqlQuery.replaceAll("```sql", "").replaceAll("```", "").trim();
    }

    public String processNaturalLanguageQuery(String naturalLanguagePrompt, String sessionId) {
        // Retrieve session history
        List<String> history = getSessionHistory(sessionId);

        // Focus on the most recent 3 history items
        List<String> recentHistory = history.subList(Math.max(history.size() - 3, 0), history.size());

        // Combine history with the current prompt
        StringBuilder promptBuilder = new StringBuilder("Session History:\n");
        for (String pastInput : recentHistory) {
            promptBuilder.append(pastInput).append("\n");
        }
        promptBuilder.append("Current Query: ").append(naturalLanguagePrompt);

        String rawSQLQuery = generateSQLFromPrompt(naturalLanguagePrompt, recentHistory);
        String sanitizedSQLQuery = sanitizeSQLQuery(rawSQLQuery);
        System.out.println(rawSQLQuery);
        List<Map<String, Object>> queryResults;

        try {
            queryResults = executeSQLQuery(sanitizedSQLQuery);
        } catch (Exception e) {
            return e.getMessage();
        }

        if (queryResults.isEmpty()) {
            return "No data found";
        }

        return generateNaturalLanguageFromResults(queryResults);
    }
    /*

        public String processNaturalLanguageQuery(String naturalLanguagePrompt, String sessionId) {
        // Retrieve session history
        List<String> history = getSessionHistory(sessionId);

        // Combine history with the current prompt
        StringBuilder promptBuilder = new StringBuilder("Session History:\n");
        for (String pastInput : history) {
            promptBuilder.append(pastInput).append("\n");
        }
        promptBuilder.append("Current Query: ").append(naturalLanguagePrompt);

        String fullPrompt = promptBuilder.toString();

        // Existing logic for processing the query...
        String rawSQLQuery = generateSQLFromPrompt(fullPrompt);
        String sanitizedSQLQuery = sanitizeSQLQuery(rawSQLQuery);
        System.out.println(rawSQLQuery);
        List<Map<String, Object>> queryResults;

        try {
            queryResults = executeSQLQuery(sanitizedSQLQuery);
        } catch (Exception e) {
            return e.getMessage();
        }

        if (queryResults.isEmpty()) {
            return "No data found";
        }

        return generateNaturalLanguageFromResults(queryResults);
    }*/
}
