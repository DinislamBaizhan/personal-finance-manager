package com.example.auth.repository;

import com.example.auth.data.base.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
//    List<Account> findAllByUserAndNameEquals(User user, String name);
}
