package com.vn.restaurant.exception;

public class InvalidTokenException extends BusinessException {

    // Sửa chữ tiếng Việt có dấu
    public InvalidTokenException(String message) {
        super(401, "Token không hợp lệ", message);
    }
}
