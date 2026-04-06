package com.vn.restaurant.features.auth.dto.req;

import jakarta.validation.constraints.NotBlank;

public record LoginReq(
        @NotBlank(message = "Tên đăng nhập không được để trống") String username,
        @NotBlank(message = "Mật khẩu không được để trống") String password) {
}
