package com.vn.restaurant.features.auth.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "LoginReq", description = "Thông tin đăng nhập")
public record LoginReq(
        @Schema(description = "Tên đăng nhập", example = "manager")
        @NotBlank(message = "Tên đăng nhập không được để trống") String username,

        @Schema(description = "Mật khẩu", example = "123456")
        @NotBlank(message = "Mật khẩu không được để trống") String password) {
}
