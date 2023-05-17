package com.example.auth.data.entity;

import com.example.auth.data.base.Account;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "cash_accounts")
public class CashAccount extends Account {
}

