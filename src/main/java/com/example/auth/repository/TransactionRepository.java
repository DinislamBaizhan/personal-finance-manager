package com.example.auth.repository;

import com.example.auth.data.base.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllByUserIdAndCardId(Long userId, Long carId);

}