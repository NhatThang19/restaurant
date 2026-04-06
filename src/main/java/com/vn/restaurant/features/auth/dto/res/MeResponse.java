package com.vn.restaurant.features.auth.dto.res;

import com.vn.restaurant.features.common.enums.UserStatusEnum;

public record MeResponse(
        Integer id,
        String username,
        String fullName,
        String role,
        UserStatusEnum status) {
}
