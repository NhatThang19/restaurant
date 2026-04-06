package com.vn.restaurant.features.auth.dto.docs;

import com.vn.restaurant.features.auth.dto.res.MeRes;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiResMeRes", description = "Phản hồi thành công chứa thông tin người dùng hiện tại")
public record ApiResMeRes(
        @Schema(example = "200") int statusCode,
        @Schema(example = "Thành công") String message,
        MeRes data,
        @Schema(nullable = true) String error,
        @Schema(nullable = true) Object details) {
}
