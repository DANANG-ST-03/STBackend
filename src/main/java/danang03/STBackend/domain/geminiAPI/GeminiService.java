package danang03.STBackend.domain.geminiAPI;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.annotation.SessionScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@SessionScope
@Slf4j
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    private final GeminiApi geminiApi;
    private final JdbcTemplate jdbcTemplate;

    private List<String> schemaCache;
    private final SessionHistoryRepository sessionHistoryRepository;
    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    public GeminiService(GeminiApi geminiApi, JdbcTemplate jdbcTemplate, SessionHistoryRepository sessionHistoryRepository) {
        this.geminiApi = geminiApi;
        this.jdbcTemplate = jdbcTemplate;
        this.sessionHistoryRepository = sessionHistoryRepository;
    }

    // Initializes the schema cache if it has not been populated yet
    private void initializeSchemaCache() {
        if (schemaCache == null) {
            schemaCache = getAllTableSchemas();
        }
    }

    // Add input to session history for a given session ID
    @Transactional
    public void addToSessionHistory(String sessionId, String input) {
        sessionHistoryRepository.save(new SessionHistory(sessionId, input));
    }

    // Retrieve session history for a given session ID
    public List<String> getSessionHistory(String sessionId) {
        return sessionHistoryRepository.findBySessionIdOrderByCreatedAtAsc(sessionId)
                .stream()
                .map(SessionHistory::getMessage)
                .toList();
    }

    // Clear session history for a given session ID
    @Transactional
    public void clearSessionHistory(String sessionId) {
        sessionHistoryRepository.deleteBySessionId(sessionId);
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

    // Processes a natural language query, incorporating session history and generating a SQL query
    public String processNaturalLanguageQuery(String naturalLanguagePrompt, String sessionId) {
        // Retrieve session history
        List<String> history = getSessionHistory(sessionId);

        // Combine history with the current prompt
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Current Query: ").append(naturalLanguagePrompt).append("\n\n");

        promptBuilder.append("Instructions: You are HR Buddy, a virtual assistant to help find information about employees or projects in ST United.\n");
        promptBuilder.append("1. If the input involves a name or project, generate a precise SQL query to retrieve relevant data.\n");
        promptBuilder.append("2. If the input is a general greeting (e.g., 'hello'), respond conversationally without SQL.\n");
        promptBuilder.append("3. Use case-insensitive matching (LOWER or ILIKE) for names or project searches.\n");
        promptBuilder.append("4. Avoid assumptions and respond clearly and concisely, without rephrasing the input unnecessarily.\n");

        String generatedResponse = generateSQLFromPrompt(String.valueOf(promptBuilder), history);

        // If SQL is generated, execute it and return the results
        if (generatedResponse.contains("SELECT")) {
            String rawSQLQuery = sanitizeSQLQuery(generatedResponse);
            System.out.println("rawSQLQuery: \n" +rawSQLQuery);
            try {
                List<Map<String, Object>> queryResults = executeSQLQuery(rawSQLQuery);
                if (queryResults.isEmpty()) {
                    return "No data found.";
                }
                return generateNaturalLanguageFromResults(queryResults);
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        // If no SQL is generated, return the conversational response
        return generatedResponse;
    }

    // Generates an SQL query from a natural language prompt and recent session history
    public String generateSQLFromPrompt(String naturalLanguagePrompt, List<String> history) {
        initializeSchemaCache();
        StringBuilder schemaBuilder = new StringBuilder("Based on the following table schemas:\n\n");
        for (String schema : schemaCache) {
            schemaBuilder.append(schema).append("\n\n");
        }

        StringBuilder historyContext = new StringBuilder("Refer to the session history for additional context:\n");
        for (String historyItem : history) {
            historyContext.append(historyItem).append("\n");
        }

        String fullPrompt = schemaBuilder.toString() +
                historyContext + naturalLanguagePrompt;

        GeminiRequest request = new GeminiRequest(fullPrompt);
        logger.info("Full prompt: {}", fullPrompt);
        GeminiResponse response = geminiApi.generateContent(model, request, apiKey);

        return response.getCandidates().stream()
                .findFirst()
                .map(candidate -> candidate.getContent().getParts().get(0).getText())
                .orElse("");
    }

    // Remove Markdown-style code block syntax from the SQL query
    public String sanitizeSQLQuery(String sqlQuery) {
        return sqlQuery.replaceAll("```sql", "").replaceAll("```", "").trim();
    }

    // Executes the given SQL query and retrieves the results
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

    // Converts the SQL query results into a natural language response
    public String generateNaturalLanguageFromResults(List<Map<String, Object>> queryResults) {
        if (queryResults == null || queryResults.isEmpty()) {
            return "We couldn't find any relevant information based on your request.";
        }

        StringBuilder resultsBuilder = new StringBuilder(
                "Process the provided data as-is and convert it into user-friendly language(ex. don't use word 'query') by listing the values clearly. " +
                        "For example:\n" +
                        "{name=Alexander Jade}\\n{name=Alice Brown} should be transformed into Alexander Jade, Alice Brown.\n" +
                        "{name=Website Revamp, description=Complete redesign of the company website}\\The project 'Website Revamp' involves a complete redesign of the company website.\n" +
                        "While avoiding assumptions or unrelated information, you may adjust the sentence structure. \n"
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
}
