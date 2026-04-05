package com.vn.restaurant.features.auth.repository;

import com.vn.restaurant.features.auth.model.RefreshToken;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {

    List<RefreshToken> findByUser_Id(Integer userId);

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUser_IdAndRevokedAtIsNull(Integer userId);

    long deleteByUser_Id(Integer userId);
}
