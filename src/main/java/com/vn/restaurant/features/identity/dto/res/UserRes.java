package com.vn.restaurant.features.identity.dto.res;

import com.vn.restaurant.features.common.enums.GenderEnum;
import com.vn.restaurant.features.common.enums.RoleNameEnum;
import com.vn.restaurant.features.common.enums.UserStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.time.LocalDate;

@Schema(name = "UserRes", description = "User information")
public record UserRes(
        @Schema(description = "User ID", example = "1") Integer id,
        @Schema(description = "Username", example = "manager") String username,
        @Schema(description = "Full name", example = "Admin Manager") String fullName,
        @Schema(description = "Phone", example = "0900000001") String phone,
        @Schema(description = "Email", example = "manager@restaurant.local") String email,
        @Schema(description = "Address", example = "1 Nguyen Hue, District 1, HCMC") String address,
        @Schema(description = "Date of birth", example = "1990-01-15") LocalDate dateOfBirth,
        @Schema(description = "Gender") GenderEnum gender,
        @Schema(description = "Hire date", example = "2021-06-01") LocalDate hireDate,
        @Schema(description = "Citizen ID", example = "079090000001") String citizenId,
        @Schema(description = "Role") RoleNameEnum role,
        @Schema(description = "Status") UserStatusEnum status,
        @Schema(description = "Created time", example = "2026-04-07T09:00:00Z") Instant createdAt,
        @Schema(description = "Updated time", example = "2026-04-07T09:30:00Z") Instant updatedAt) {
}
