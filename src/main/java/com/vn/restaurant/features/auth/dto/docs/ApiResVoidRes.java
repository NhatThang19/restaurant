package com.vn.restaurant.features.auth.dto.docs;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ApiResVoidRes", description = "Phản hồi không có dữ liệu trả về")
public record ApiResVoidRes(
        @Schema(example = "200") int statusCode,
        @Schema(example = "Đăng xuất thành công") String message,
        @Schema(nullable = true) Object data,
        @Schema(nullable = true) String error,
        @Schema(nullable = true) Object details) {
}

