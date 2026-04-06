package com.vn.restaurant.features.auth.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "RefreshReq", description = "Yêu cầu làm mới access token")
public record RefreshReq(
        @Schema(description = "Refresh token đã cấp trước đó", example = "eyJhbGciOiJIUzI1NiJ9.refresh...") String refreshToken) {
}
