package com.vn.restaurant.features.auth.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "RefreshReq", description = "Yeu cau lam moi access token")
public record RefreshReq(
        @Schema(description = "Refresh token da cap truoc do", example = "eyJhbGciOiJIUzI1NiJ9.refresh...") String refreshToken) {
}
