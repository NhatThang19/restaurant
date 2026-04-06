package com.vn.restaurant.features.auth.dto.res;

import com.vn.restaurant.features.common.enums.GenderEnum;
import com.vn.restaurant.features.common.enums.UserStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;

@Builder
@Schema(name = "MeRes", description = "Thông tin người dùng đang đăng nhập")
public record MeRes(
        @Schema(description = "ID người dùng", example = "1") Integer id,
        @Schema(description = "Tên đăng nhập", example = "manager") String username,
        @Schema(description = "Họ tên", example = "Admin Manager") String fullName,
        @Schema(description = "Số điện thoại", example = "0900000001") String phone,
        @Schema(description = "Email", example = "manager@restaurant.local") String email,
        @Schema(description = "Địa chỉ", example = "1 Nguyen Hue, District 1, HCMC") String address,
        @Schema(description = "Ngày sinh", example = "1990-01-15") LocalDate dateOfBirth,
        @Schema(description = "Giới tính") GenderEnum gender,
        @Schema(description = "Ngày vào làm", example = "2021-06-01") LocalDate hireDate,
        @Schema(description = "Số CCCD", example = "079090000001") String citizenId,
        @Schema(description = "Tên role", example = "MANAGER") String role,
        @Schema(description = "Trạng thái tài khoản") UserStatusEnum status) {
}
