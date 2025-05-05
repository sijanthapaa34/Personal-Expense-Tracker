package com.sijan.finance.service;

import com.sijan.finance.dto.ExpenseDTO;
import com.sijan.finance.dto.ExpenseResponseDTO;
import com.sijan.finance.model.Category;
import com.sijan.finance.model.Expense;
import com.sijan.finance.model.Lion;
import com.sijan.finance.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepo;

    @Autowired
    private CohereService cohereService;

    public ExpenseResponseDTO addExpense(ExpenseDTO dto, Lion lion) {
        Category category = cohereService.classifyExpense(dto.getDescription());

        Expense expense = new Expense();
        expense.setAmount(dto.getAmount());
        expense.setDescription(dto.getDescription());
        expense.setDate(LocalDate.now());
        System.out.println(category);
        expense.setCategory(category);
        expense.setLion(lion);

        Expense saved = expenseRepo.save(expense);

        return new ExpenseResponseDTO(
                saved.getId(),
                saved.getAmount(),
                saved.getDescription(),
                saved.getDate(),
                saved.getCategory().getDisplayName(),
                saved.getLion().getUsername()
        );
    }

    public List<ExpenseResponseDTO> getExpensesByUser(Lion lion) {
        // Fetch expenses from the repository
        List<Expense> expenses = expenseRepo.findByLion(lion);

        // Map each Expense to an ExpenseResponseDTO
        return expenses.stream()
                .map(exp -> new ExpenseResponseDTO(
                        exp.getId(),
                        exp.getAmount(),
                        exp.getDescription(),
                        exp.getDate(),
                        exp.getCategory().getDisplayName(),  // Assuming Category is an enum
                        exp.getLion().getUsername()           // Get the username of the associated Lion
                ))
                .collect(Collectors.toList());
    }
}
