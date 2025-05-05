package com.sijan.finance.repository;

import com.sijan.finance.dto.ExpenseResponseDTO;
import com.sijan.finance.model.Expense;
import com.sijan.finance.model.Lion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByLion(Lion lion);
}
