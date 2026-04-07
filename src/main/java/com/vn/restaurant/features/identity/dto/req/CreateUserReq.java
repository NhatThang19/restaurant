package com.vn.restaurant.features.identity.dto.req;

import com.vn.restaurant.features.common.enums.GenderEnum;
import com.vn.restaurant.features.common.enums.RoleNameEnum;
import com.vn.restaurant.features.common.enums.UserStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Schema(name = "CreateUserReq", description = "Yeu cau tao nguoi dung moi")
public record CreateUserReq(
                @Schema(description = "Ten dang nhap", example = "staff01") @NotBlank(message = "Ten dang nhap khong duoc de trong") @Size(max = 100, message = "Ten dang nhap khong duoc vuot qua 100 ky tu") String username,

                @Schema(description = "Mat khau", example = "123456") @NotBlank(message = "Mat khau khong duoc de trong") @Size(min = 6, max = 255, message = "Mat khau phai tu 6 den 255 ky tu") String password,

                @Schema(description = "Ho ten", example = "Nguyen Van A") @NotBlank(message = "Ho ten khong duoc de trong") @Size(max = 150, message = "Ho ten khong duoc vuot qua 150 ky tu") String fullName,

                @Schema(description = "So dien thoai", example = "0900000009") @Size(max = 20, message = "So dien thoai khong duoc vuot qua 20 ky tu") String phone,

                @Schema(description = "Email", example = "staff01@restaurant.local") @Email(message = "Email khong hop le") @Size(max = 150, message = "Email khong duoc vuot qua 150 ky tu") String email,

                @Schema(description = "Dia chi", example = "10 Le Loi, HCMC") @Size(max = 255, message = "Dia chi khong duoc vuot qua 255 ky tu") String address,

                @Schema(description = "Ngay sinh", example = "1999-01-20") LocalDate dateOfBirth,

                @Schema(description = "Gioi tinh") GenderEnum gender,

                @Schema(description = "Ngay vao lam", example = "2024-02-01") LocalDate hireDate,

                @Schema(description = "So CCCD", example = "079090000009") @Size(max = 20, message = "So CCCD khong duoc vuot qua 20 ky tu") String citizenId,

                @Schema(description = "Quyen (Role)", example = "WAITER") @NotNull(message = "Quyen khong duoc de trong") RoleNameEnum role,

                @Schema(description = "Trang thai tai khoan", example = "ACTIVE") UserStatusEnum status) {
}