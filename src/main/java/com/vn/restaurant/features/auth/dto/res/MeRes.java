package com.vn.restaurant.features.auth.dto.res;

import com.vn.restaurant.features.common.enums.GenderEnum;
import com.vn.restaurant.features.common.enums.UserStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;

@Builder
@Schema(name = "MeRes", description = "Thong tin nguoi dung dang dang nhap")
public record MeRes(
                @Schema(description = "ID nguoi dung", example = "1") Integer id,
                @Schema(description = "Ten dang nhap", example = "manager") String username,
                @Schema(description = "Ho ten", example = "Admin Manager") String fullName,
                @Schema(description = "So dien thoai", example = "0900000001") String phone,
                @Schema(description = "Email", example = "manager@restaurant.local") String email,
                @Schema(description = "Dia chi", example = "1 Nguyen Hue, District 1, HCMC") String address,
                @Schema(description = "Ngay sinh", example = "1990-01-15") LocalDate dateOfBirth,
                @Schema(description = "Gioi tinh") GenderEnum gender,
                @Schema(description = "Ngay vao lam", example = "2021-06-01") LocalDate hireDate,
                @Schema(description = "So CCCD", example = "079090000001") String citizenId,
                @Schema(description = "Ten role", example = "MANAGER") String role,
                @Schema(description = "Trang thai tai khoan") UserStatusEnum status) {
}
