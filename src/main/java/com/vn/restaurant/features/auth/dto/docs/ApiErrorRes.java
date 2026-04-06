package com.vn.restaurant.features.auth.dto.docs;

import io.swagger.v3.oas.annotations.media.Schema;

// Sửa lỗi tiếng Việt
@Schema(name = "ApiErrorRes", description = "Phản hồi lỗi chuẩn của hệ thống")
public record ApiErrorRes(
        @Schema(example = "400") int statusCode,
        @Schema(example = "Dữ liệu không hợp lệ") String message,
        @Schema(nullable = true) Object data,
        @Schema(example = "Yêu cầu không hợp lệ") String error,
        @Schema(nullable = true) Object details) {
}

