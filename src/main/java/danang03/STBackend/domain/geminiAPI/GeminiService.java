package danang03.STBackend.domain.geminiAPI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    private final GeminiApi geminiApi;
    private final JdbcTemplate jdbcTemplate;

    public GeminiService(GeminiApi geminiApi, JdbcTemplate jdbcTemplate) {
        this.geminiApi = geminiApi;
        this.jdbcTemplate = jdbcTemplate;
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
        String query = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'";
        List<Map<String, Object>> tables = jdbcTemplate.queryForList(query);

        List<String> schemas = new ArrayList<>();
        for (Map<String, Object> table : tables) {
            String tableName = (String) table.get("table_name");
            schemas.add(getTableSchema(tableName));
        }

        return schemas;
    }

    public String generateSQLFromPrompt(String naturalLanguagePrompt) {
        List<String> schemas = getAllTableSchemas();
        StringBuilder schemaBuilder = new StringBuilder("Based on the following table schemas:\n\n");
        for (String schema : schemas) {
            schemaBuilder.append(schema).append("\n\n");
        }

        String fullPrompt = schemaBuilder.toString() + "Convert the following natural language prompt into an SQL query:\n" + naturalLanguagePrompt;
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
            throw new RuntimeException("Failed to find relevant data. Please check your input.");
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
                        "{name=Elijah Copper, email=elijah.copper@example.com}\\n{name=Sofia Ruby, email=sofia.ruby@example.com} should be transformed into Elijah Copper, whose email is elijah.copper@example.com. Sofia Ruby, whose email is sofia.ruby@example.com.\n" +
                        "{name=Emma Green, role=Manager}\\n{name=Oliver Lime, role=Engineer} should be transformed into Emma Green is a Manager. Oliver Lime is an Engineer.\n" +
                        "Avoid adding assumptions or unrelated information.\n\n"
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

    public String processNaturalLanguageQuery(String naturalLanguagePrompt) {
        // Step 1: Convert natural language to SQL query
        String rawSQLQuery = generateSQLFromPrompt(naturalLanguagePrompt);

        // Sanitize the SQL query to remove Markdown syntax
        String sanitizedSQLQuery = sanitizeSQLQuery(rawSQLQuery);
        System.out.println("Generated SQL Query: " + sanitizedSQLQuery);

        // Step 2: Execute the SQL query
        List<Map<String, Object>> queryResults;
        try {
            queryResults = executeSQLQuery(sanitizedSQLQuery);
        } catch (Exception e) {
            return e.getMessage();
        }

        // Handle empty results
        if (queryResults.isEmpty()) {
            return "No data found";
        }

        // Step 3: Convert query results to natural language
        return generateNaturalLanguageFromResults(queryResults);
    }
}
