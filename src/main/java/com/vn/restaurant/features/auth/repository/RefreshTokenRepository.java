package com.vn.restaurant.features.auth.repository;

import com.vn.restaurant.features.auth.model.RefreshToken;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    List<RefreshToken> findByUser_Id(Integer userId);

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByTokenAndRevokedAtIsNull(String token);

    List<RefreshToken> findByUser_IdAndRevokedAtIsNull(Integer userId);

    long deleteByUser_Id(Integer userId);

    @Modifying
    @Query("delete from RefreshToken rt where rt.expiresAt < :now")
    int deleteAllExpiredBefore(Instant now);
}
