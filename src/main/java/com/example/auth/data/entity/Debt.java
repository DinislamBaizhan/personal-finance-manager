package com.example.auth.data.entity;

import com.example.auth.data.enums.Currency;
import com.example.auth.data.enums.DebtType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Description;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Description("if debitType == LOAN: I am being paid for my debts" +
        " if debitType == CREDIT: I pay debts to banks")
public class Debt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DebtType debtType;

    @Column(name = "created_at", nullable = false,
            updatable = false)
    @CreatedDate
    private LocalDate createdAt = LocalDate.now();

    protected boolean active = true;

    @Column(nullable = false)
    @Size(min = 3, max = 34, message = "account name must be between 3 and 34 characters long")
    private String name;

    @Column(nullable = false)
    protected BigDecimal indebtedness;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    public User user;

    @PostLoad
    @PostUpdate
    @PostPersist
    public void checkIndebtedness() {
        if (indebtedness.compareTo(BigDecimal.ZERO) == 0) {
            active = false;
        }
    }
}