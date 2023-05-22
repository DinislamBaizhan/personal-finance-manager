package com.example.finance.data.entity;

import com.example.finance.data.enums.Currency;
import com.example.finance.data.enums.DebtType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Description;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Description("if debitType == LOAN: I am being paid for my debts" +
        " if debitType == CREDIT: I pay debts to banks")
public class Debt {

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    public User user;
    protected boolean active = true;
    @Column(nullable = false)
    protected BigDecimal indebtedness;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private DebtType debtType;
    @Column(name = "created_at", nullable = false,
            updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(nullable = false)
    @Size(min = 3, max = 34, message = "account name must be between 3 and 34 characters long")
    private String name;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @PostLoad
    @PostUpdate
    @PostPersist
    public void checkIndebtedness() {
        if (indebtedness.signum() <= 0) {
            active = false;
        }
    }

    public void addMoney(BigDecimal money) {
        indebtedness = indebtedness.add(money);
    }

    public void subtractMoney(BigDecimal money) {
        indebtedness = indebtedness.subtract(money);
    }
}