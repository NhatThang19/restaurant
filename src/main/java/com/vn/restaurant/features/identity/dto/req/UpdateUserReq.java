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

@Schema(name = "UpdateUserReq", description = "Yeu cau cap nhat thong tin nguoi dung")
public record UpdateUserReq(
                @Schema(description = "Ho ten", example = "Nguyen Van B") @NotBlank(message = "Ho ten khong duoc de trong") @Size(max = 150, message = "Ho ten khong duoc vuot qua 150 ky tu") String fullName,

                @Schema(description = "So dien thoai", example = "0900000010") @Size(max = 20, message = "So dien thoai khong duoc vuot qua 20 ky tu") String phone,

                @Schema(description = "Email", example = "staff01.updated@restaurant.local") @Email(message = "Email khong hop le") @Size(max = 150, message = "Email khong duoc vuot qua 150 ky tu") String email,

                @Schema(description = "Dia chi", example = "20 Nguyen Hue, HCMC") @Size(max = 255, message = "Dia chi khong duoc vuot qua 255 ky tu") String address,

                @Schema(description = "Ngay sinh", example = "1998-12-12") LocalDate dateOfBirth,

                @Schema(description = "Gioi tinh") GenderEnum gender,

                @Schema(description = "Ngay vao lam", example = "2023-03-10") LocalDate hireDate,

                @Schema(description = "So CCCD", example = "079090000010") @Size(max = 20, message = "So CCCD khong duoc vuot qua 20 ky tu") String citizenId,

                @Schema(description = "Quyen (Role)", example = "CASHIER") @NotNull(message = "Quyen khong duoc de trong") RoleNameEnum role,

                @Schema(description = "Trang thai tai khoan", example = "ACTIVE") @NotNull(message = "Trang thai khong duoc de trong") UserStatusEnum status) {
}