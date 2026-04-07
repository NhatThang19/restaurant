package com.vn.restaurant.features.auth.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "LoginReq", description = "Thong tin dang nhap")
public record LoginReq(
        @Schema(description = "Ten dang nhap", example = "manager")
        @NotBlank(message = "Ten dang nhap khong duoc de trong") String username,

        @Schema(description = "Mat khau", example = "123456")
        @NotBlank(message = "Mat khau khong duoc de trong") String password) {
}
