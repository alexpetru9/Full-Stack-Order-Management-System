package com.alex.demo.repository;

import com.alex.demo.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenAndEmail(String token, String email);
    void deleteByEmail(String email);
}