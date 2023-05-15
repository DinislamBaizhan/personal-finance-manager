package com.example.auth.data.entity;

import com.example.auth.data.enums.BudgetCategory;
import com.example.auth.data.enums.BudgetPeriod;
import com.example.auth.data.enums.Currency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Goals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String goalType;
    private Double targetAmount;
    private Double currentAmount;


    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<BudgetCategory> budgetCategories;

    @Enumerated(EnumType.STRING)
    private BudgetPeriod budgetPeriod;

    @Enumerated(EnumType.STRING)
    private Currency currency;

}

