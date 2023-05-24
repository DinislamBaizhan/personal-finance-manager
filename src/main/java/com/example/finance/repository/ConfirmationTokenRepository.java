package com.example.finance.repository;

import com.example.finance.data.entity.ConfirmationToken;
import com.example.finance.data.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ConfirmationTokenRepository
        extends JpaRepository<ConfirmationToken, Long> {

    Optional<ConfirmationToken> findByToken(String token);

    Optional<ConfirmationToken> findByConfirmedIsFalseAndUserAndExpiresAtAfter(User user, LocalDateTime localDateTime);

    List<ConfirmationToken> findAllByUser(User user);

    @Transactional
    @Modifying
    @Query("UPDATE ConfirmationToken c " +
            "SET c.confirmedAt = :confirmedAt, c.confirmed = true " +
            "WHERE c.token = :token")
    void updateConfirmedAt(@Param("token") String token,
                           @Param("confirmedAt") LocalDateTime confirmedAt);
}