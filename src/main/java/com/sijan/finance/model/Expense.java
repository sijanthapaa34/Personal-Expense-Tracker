package com.sijan.finance.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double amount;
    private String description;
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    private  Category category;
    @ManyToOne(fetch = FetchType.EAGER)
    private Lion lion;
}
