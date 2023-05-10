package com.example.auth.repository;

import com.example.auth.data.base.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface AccountRepository extends JpaRepository<Account, Long> {
}
