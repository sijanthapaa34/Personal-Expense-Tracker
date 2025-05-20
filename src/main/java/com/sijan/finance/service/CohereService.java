package com.sijan.finance.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sijan.finance.dto.ExpenseResponseDTO;
import com.sijan.finance.dto.ParsedExpense;
import com.sijan.finance.model.Category;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@Service
public class CohereService {

    private final RestTemplate restTemplate;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Value("${cohere.api.key}")
    private String cohereApiKey;

    public CohereService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Category classifyExpense(String description) {
        String prompt = String.format("""
            You are a financial assistant.
            Categorize the following expense into one of these categories:
            - FOODANDBEVERAGES
            - RENT
            - UTILITIES
            - ENTERTAINMENT
            - TRAVEL
            - OTHER

            Description: '%s'

            Only return the category name in uppercase. No extra text.
            """, description);

        try {
            String response = sendCohereRequest(prompt);
            return Category.valueOf(response.trim().toUpperCase());
        } catch (Exception e) {
            return Category.OTHER;
        }
    }

    public ParsedExpense parseExpenseFromText(String input) throws Exception {
        String prompt = String.format("""
            Extract amount, description, and date from this sentence:
            "%s"

            Return strictly in this format:
            {"amount": 25.99, "description": "coffee", "date": "2025-05-06"}

            If no date is given, use today's date.
            Only return the JSON object — no explanation.
            """, input);

        String responseBody = sendCohereRequest(prompt);
        return extractJsonFromResponse(responseBody);
    }

    public String generateFormattedMonthlySummary(List<ExpenseResponseDTO> expenses) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append("\uD83D\uDCDC Monthly Spending Summary:\n\n");
        sb.append(String.format("%-30s %-10s %-20s\n", "Description", "Amount", "Category"));
        sb.append("=".repeat(65)).append("\n");

        double total = 0;
        Map<String, Double> categoryTotals = new HashMap<>();

        for (ExpenseResponseDTO e : expenses) {
            sb.append(String.format("%-30s $%-9.2f %-20s\n", e.getDescription(), e.getAmount(), e.getCategory()));
            total += e.getAmount();
            categoryTotals.merge(e.getCategory(), e.getAmount(), Double::sum);
        }

        sb.append("\n");
        sb.append(String.format("\uD83D\uDCCA Total Spent: $%.2f\n", total));

        String topCategory = categoryTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Other");

        sb.append("\n\uD83D\uDCCC Top Spending Category: ").append(topCategory).append("\n");
        sb.append("\uD83D\uDCAC Tip: Try reducing expenses in this category next month.\n\n");

        String paragraphSummary = summarizeTableWithCohere(sb.toString());
        return sb.append("\n\uD83D\uDCDD Summary:\n").append(paragraphSummary).toString();
    }

    public String checkFormattedBudgetAlert(Map<Category, Double> categoryTotals) {
        StringBuilder sb = new StringBuilder();
        sb.append("💰 Monthly Category-wise Budget Alert:\n\n");

        for (Map.Entry<Category, Double> entry : categoryTotals.entrySet()) {
            Category category = entry.getKey();
            double spent = entry.getValue();
            double budgetLimit = category.getMaxBudget();
            double remaining = budgetLimit - spent;
            double usage = (spent / budgetLimit) * 100;

            sb.append(String.format(" %s\n", category.getDisplayName()));
            sb.append(String.format("Budget Limit: NPR %.2f\n", budgetLimit));
            sb.append(String.format("Amount Spent: NPR %.2f\n", spent));
            sb.append(String.format("Remaining: NPR %.2f\n", remaining));

            if (usage >= 100) {
                sb.append(" You've exceeded your budget!\n");
                sb.append(" Review your spending and adjust accordingly.\n");
            } else if (usage >= 90) {
                sb.append("️ You are nearing your budget limit.\n");
                sb.append(" Consider cutting back on non-essential expenses.\n");
            } else {
                sb.append(" You're within your budget. Keep it up!\n");
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    private String summarizeTableWithCohere(String prompt) {
        prompt += "\n\nSummarize this in one paragraph.";
        return sendCohereRequest(prompt);
    }

    private String sendCohereRequest(String prompt) {
        try {
            Map<String, Object> request = new HashMap<>();
            request.put("model", "command");
            request.put("prompt", prompt);
            request.put("max_tokens", 150);
            request.put("temperature", 0.7);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + cohereApiKey);
            headers.set("Content-Type", "application/json");

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            String apiUrl = "https://api.cohere.ai/v1/generate";

            String response = restTemplate.postForObject(apiUrl, entity, String.class);
            JsonNode node = OBJECT_MAPPER.readTree(response);

            return node.path("generations").get(0).path("text").asText().trim();
        } catch (Exception e) {
            throw new RuntimeException("Error calling Cohere API: " + e.getMessage(), e);
        }
    }

    private ParsedExpense extractJsonFromResponse(String responseBody) throws Exception {
        JsonNode root = OBJECT_MAPPER.readTree(responseBody);

        String description = root.path("description").asText("");
        double amount = root.path("amount").asDouble(0.0);
        String dateText = root.path("date").asText("");
        LocalDate date = dateText.isEmpty() ? LocalDate.now() : LocalDate.parse(dateText);

        return new ParsedExpense(amount, description, date);
    }
}
