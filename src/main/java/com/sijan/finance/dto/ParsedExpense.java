package com.sijan.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParsedExpense {
    private Double amount;
    private String description;
    private LocalDate date;
}
