package com.vn.restaurant.features.auth.dto.res;

import com.vn.restaurant.features.common.enums.GenderEnum;
import com.vn.restaurant.features.common.enums.UserStatusEnum;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record MeRes(
        Integer id,
        String username,
        String fullName,
        String phone,
        String email,
        String address,
        LocalDate dateOfBirth,
        GenderEnum gender,
        LocalDate hireDate,
        String citizenId,
        String role,
        UserStatusEnum status) {
}
