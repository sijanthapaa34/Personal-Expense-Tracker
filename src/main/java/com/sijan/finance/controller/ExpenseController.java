package com.sijan.finance.controller;

import com.sijan.finance.dto.ExpenseDTO;
import com.sijan.finance.dto.ExpenseResponseDTO;
import com.sijan.finance.dto.ParsedExpense;
import com.sijan.finance.model.Category;
import com.sijan.finance.model.Lion;
import com.sijan.finance.model.LionPrincipal;
import com.sijan.finance.service.CohereService;
import com.sijan.finance.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private CohereService cohereService;

    // Add new expense manually
    @PostMapping
    public ResponseEntity<ExpenseResponseDTO> addExpense(@RequestBody ExpenseDTO expenseDTO,
                                                         @AuthenticationPrincipal LionPrincipal lionPrincipal) {
        Lion lion = lionPrincipal.getLion();
        ExpenseResponseDTO savedExpense = expenseService.addExpense(expenseDTO, lion);
        return ResponseEntity.ok(savedExpense);
    }
    // Get all expenses for authenticated user
    @GetMapping
    public ResponseEntity<List<ExpenseResponseDTO>> getAllExpenses(@AuthenticationPrincipal LionPrincipal lionPrincipal) {

        Lion lion = lionPrincipal.getLion();
        List<ExpenseResponseDTO> expenses = expenseService.getExpensesByUser(lion);
        return ResponseEntity.ok(expenses);
    }

    // Parse expense from natural language input
    @PostMapping("/parse")
    public ResponseEntity<ExpenseResponseDTO> parseAndAddExpense(
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal LionPrincipal lionPrincipal) throws Exception {

        Lion lion = lionPrincipal.getLion();
        String inputText = payload.get("input");
        if (inputText == null || inputText.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        ParsedExpense parsed = cohereService.parseExpenseFromText(inputText);

        ExpenseDTO dto = new ExpenseDTO();
        dto.setAmount(parsed.getAmount());
        dto.setDescription(parsed.getDescription());

        ExpenseResponseDTO saved = expenseService.addExpense(dto, lion);
        return ResponseEntity.ok(saved);
    }

    // Generate monthly spending summary using AI
    @GetMapping("/summary")
    public ResponseEntity<String> getMonthlySummary(@AuthenticationPrincipal LionPrincipal lionPrincipal) throws Exception {
        Lion lion = lionPrincipal.getLion();
        List<ExpenseResponseDTO> expenses = expenseService.getExpensesByUser(lion);
        String summary = cohereService.generateFormattedMonthlySummary(expenses);
        return ResponseEntity.ok(summary);
    }

    // Check if the budget is exceeded and get an AI-generated alert
    @GetMapping("/budget-alert")
    public ResponseEntity<String> checkBudgetStatus(@AuthenticationPrincipal LionPrincipal lionPrincipal) throws Exception {
        Lion lion = lionPrincipal.getLion();
        List<ExpenseResponseDTO> expenses = expenseService.getExpensesByUser(lion);
        Map<Category, Double> categoryTotals = expenses.stream()
                .filter(e -> e.getCategory() != null)
                .collect(Collectors.groupingBy(
                        e -> Category.fromDisplayName(e.getCategory()),
                        Collectors.summingDouble(ExpenseResponseDTO::getAmount)
                ));


        String alert = cohereService.checkFormattedBudgetAlert(categoryTotals);
        return ResponseEntity.ok(alert);
    }
}