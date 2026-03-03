package com.blog.blog.repository.AuthRepository;

import com.blog.blog.entity.AuthEntity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    @Query("SELECT r from RefreshToken r")
    void revokeAllTokens(Long userId);
    void deleteByToken(String refreshToken);
}
