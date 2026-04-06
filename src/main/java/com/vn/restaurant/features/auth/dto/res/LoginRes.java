package com.vn.restaurant.features.auth.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoginRes", description = "Thông tin token sau khi đăng nhập/làm mới")
public record LoginRes(
        @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiJ9.access...") String accessToken,
        @Schema(description = "JWT refresh token", example = "eyJhbGciOiJIUzI1NiJ9.refresh...") String refreshToken) {
}
