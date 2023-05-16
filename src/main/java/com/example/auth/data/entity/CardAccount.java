package com.example.auth.data.entity;

import com.example.auth.data.base.Account;
import com.example.auth.data.enums.PaymentSystem;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "card_accounts")
public class CardAccount extends Account {

    @Column(name = "card_holder_name")
    @Size(min = 3, max = 34, message = "your name must be between 3 and 34 characters long")
    @ColumnTransformer(write = "UPPER(?)")
    private String cardHolderName;

    @Column(name = "bank_name")
    @ColumnTransformer(write = "UPPER(?)")
    private String bankName = "BANK";

    @Column(name = "card_number")
    private String cardNumber = "XXXX-XXXX-XXXX-XXXX";

    @Column(name = "CVV/CVC")
    @Size(min = 3, max = 3, message = "CVV/CVC code must be 3 characters long")
    private String CVV = "XXX";

    @Column(name = "expired_date")
    @JsonFormat(shape = JsonFormat.
            Shape.STRING,
            pattern = "MM/yy")
    private LocalDate expiredDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    private PaymentSystem paymentSystem;
}
