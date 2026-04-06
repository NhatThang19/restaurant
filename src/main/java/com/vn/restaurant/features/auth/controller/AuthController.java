package com.vn.restaurant.features.auth.controller;

import com.vn.restaurant.dto.ApiRes;
import com.vn.restaurant.exception.InvalidTokenException;
import com.vn.restaurant.features.auth.dto.req.LoginReq;
import com.vn.restaurant.features.auth.dto.req.RefreshReq;
import com.vn.restaurant.features.auth.dto.res.LoginRes;
import com.vn.restaurant.features.auth.dto.res.MeRes;
import com.vn.restaurant.features.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiRes<LoginRes>> login(
            @Valid @RequestBody LoginReq request,
            HttpServletRequest httpRequest) {

        String userAgent = httpRequest.getHeader("User-Agent");
        String ipAddress = extractClientIp(httpRequest);

        LoginRes loginRes = authService.login(request, userAgent, ipAddress);
        return ResponseEntity.ok(ApiRes.success("Đăng nhập thành công", loginRes));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiRes<LoginRes>> refresh(
            @RequestBody(required = false) RefreshReq body,
            HttpServletRequest httpRequest) {

        String rawRefreshToken = extractRefreshToken(body);
        if (rawRefreshToken == null) {
            throw new InvalidTokenException("Refresh token không được cung cấp");
        }

        String userAgent = httpRequest.getHeader("User-Agent");
        String ipAddress = extractClientIp(httpRequest);

        LoginRes loginRes = authService.refresh(rawRefreshToken, userAgent, ipAddress);
        return ResponseEntity.ok(ApiRes.success("Token đã được làm mới", loginRes));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiRes<Void>> logout(@RequestBody(required = false) RefreshReq body) {
        String rawRefreshToken = extractRefreshToken(body);
        if (rawRefreshToken == null) {
            throw new InvalidTokenException("Refresh token không được cung cấp");
        }

        authService.logout(rawRefreshToken);
        return ResponseEntity.ok(ApiRes.success("Đăng xuất thành công", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiRes<MeRes>> getMe(@AuthenticationPrincipal Jwt jwt) {
        String tokenType = jwt.getClaimAsString("type");
        if (!"access".equals(tokenType)) {
            throw new InvalidTokenException("Token không phải access token");
        }

        MeRes response = authService.getMe(jwt.getSubject());
        return ResponseEntity.ok(ApiRes.success(response));
    }

    private String extractRefreshToken(RefreshReq body) {
        if (body != null && body.refreshToken() != null && !body.refreshToken().isBlank()) {
            return body.refreshToken();
        }
        return null;
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
