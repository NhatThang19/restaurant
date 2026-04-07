package com.vn.restaurant.features.identity.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "ChangeUserPasswordReq", description = "Yeu cau thay doi mat khau tai khoan")
public record ChangeUserPasswordReq(
                @Schema(description = "Mat khau moi", example = "654321") @NotBlank(message = "Mat khau moi khong duoc de trong") @Size(min = 6, max = 255, message = "Mat khau moi phai tu 6 den 255 ky tu") String newPassword) {
}