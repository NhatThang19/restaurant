package com.vn.restaurant.features.auth.dto.docs;

import com.vn.restaurant.features.auth.dto.res.LoginRes;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiResLoginRes", description = "Phản hồi thành công chứa token đăng nhập")
public record ApiResLoginRes(
        @Schema(example = "200") int statusCode,
        @Schema(example = "Đăng nhập thành công") String message,
        LoginRes data,
        @Schema(nullable = true) String error,
        @Schema(nullable = true) Object details) {
}
