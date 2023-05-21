package com.example.auth.data.base;

import com.example.auth.data.entity.User;
import com.example.auth.data.enums.AccountType;
import com.example.auth.data.enums.Currency;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public abstract class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false,
            updatable = false)
    private LocalDate createdAt = LocalDate.now();

    @Column(nullable = false)
    @Size(min = 3, max = 34, message = "account name must be between 3 and 34 characters long")
    private String name;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String icon;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @Column(name = "money_limit")
    private BigDecimal moneyLimit;

    public void addMoney(BigDecimal money) {
        balance = balance.add(money);
    }

    public void subtractMoney(BigDecimal money) {
        balance = balance.subtract(money);
    }
}