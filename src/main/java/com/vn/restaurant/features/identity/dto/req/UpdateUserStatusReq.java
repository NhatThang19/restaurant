package com.vn.restaurant.features.identity.dto.req;

import com.vn.restaurant.features.common.enums.UserStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "UpdateUserStatusReq", description = "Yeu cau khoa hoac mo khoa tai khoan nguoi dung")
public record UpdateUserStatusReq(
                @Schema(description = "Trang thai tai khoan nguoi dung", example = "LOCKED") @NotNull(message = "Trang thai khong duoc de trong") UserStatusEnum status) {
}