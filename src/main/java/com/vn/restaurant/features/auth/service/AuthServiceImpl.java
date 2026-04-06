package com.vn.restaurant.features.auth.service;

import com.vn.restaurant.exception.InvalidTokenException;
import com.vn.restaurant.features.auth.dto.req.LoginReq;
import com.vn.restaurant.features.auth.dto.res.LoginRes;
import com.vn.restaurant.features.auth.dto.res.MeRes;
import com.vn.restaurant.features.auth.model.RefreshToken;
import com.vn.restaurant.features.auth.repository.RefreshTokenRepository;
import com.vn.restaurant.features.identity.model.User;
import com.vn.restaurant.features.identity.service.UserService;

import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final UserService userService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    @Override
    @Transactional
    public LoginRes login(LoginReq request, String userAgent, String ipAddress) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password()));

        User user = userService.getByUsernameOrThrow(request.username());

        String accessToken = generateAccessToken(authentication.getName(), user);
        String refreshToken = generateRefreshToken(authentication.getName(), user);
        saveRefreshToken(refreshToken, user, userAgent, ipAddress);

        return new LoginRes(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public LoginRes refresh(String rawRefreshToken, String userAgent, String ipAddress) {
        validateRefreshToken(rawRefreshToken);
        String tokenHash = hashToken(rawRefreshToken);

        RefreshToken storedToken = refreshTokenRepository.findByTokenAndRevokedAtIsNull(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Refresh token không hợp lệ"));

        if (storedToken.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidTokenException("Refresh token đã hết hạn");
        }

        storedToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(storedToken);

        User user = storedToken.getUser();
        String username = user.getUsername();

        String accessToken = generateAccessToken(username, user);
        String refreshToken = generateRefreshToken(username, user);
        saveRefreshToken(refreshToken, user, userAgent, ipAddress);

        return new LoginRes(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public void logout(String rawRefreshToken) {
        validateRefreshToken(rawRefreshToken);
        String tokenHash = hashToken(rawRefreshToken);

        RefreshToken storedToken = refreshTokenRepository.findByTokenAndRevokedAtIsNull(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Refresh token không hợp lệ"));

        if (storedToken.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidTokenException("Refresh token đã hết hạn");
        }

        storedToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(storedToken);
    }

    @Override
    @Transactional(readOnly = true)
    public MeRes getMe(String username) {
        User user = userService.getByUsernameOrThrow(username);

        return MeRes.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .address(user.getAddress())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .hireDate(user.getHireDate())
                .citizenId(user.getCitizenId())
                .role(user.getRole().getName().name())
                .status(user.getStatus())
                .build();
    }

    private String generateAccessToken(String username, User user) {
        Instant now = Instant.now();
        List<String> roles = List.of(user.getRole().getName().name());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(username)
                .issuedAt(now)
                .expiresAt(now.plusMillis(accessTokenExpiration))
                .claim("userId", user.getId())
                .claim("roles", roles)
                .claim("type", "access")
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    private String generateRefreshToken(String username, User user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .id(UUID.randomUUID().toString())
                .subject(username)
                .issuedAt(now)
                .expiresAt(now.plusMillis(refreshTokenExpiration))
                .claim("userId", user.getId())
                .claim("type", "refresh")
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    private void saveRefreshToken(String rawToken, User user, String userAgent, String ipAddress) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(hashToken(rawToken))
                .user(user)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusMillis(refreshTokenExpiration))
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .build();
        refreshTokenRepository.save(refreshToken);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Không thể hash refresh token", ex);
        }
    }

    private void validateRefreshToken(String rawRefreshToken) {
        try {
            String tokenType = jwtDecoder.decode(rawRefreshToken).getClaimAsString("type");
            if (!"refresh".equals(tokenType)) {
                throw new InvalidTokenException("Token không phải là refresh token");
            }
        } catch (JwtException ex) {
            throw new InvalidTokenException("Refresh token không hợp lệ");
        }
    }
}