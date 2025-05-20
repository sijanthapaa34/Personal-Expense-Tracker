package com.sijan.finance.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseResponseDTO {
    private Long id;
    private Double amount;
    private String description;
    private LocalDate date;
    private String category;
    private String username;

    public ExpenseResponseDTO(Long id, Double amount, String description,
                              LocalDate date, String category, String username) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.category = category;
        this.username = username;
    }

}