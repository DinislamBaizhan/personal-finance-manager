package com.example.auth.data.entity;

import com.example.auth.data.base.Transaction;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Income extends Transaction {
}
