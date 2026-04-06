package com.vn.restaurant.exception;

public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String resource, String field, String value) {
        super(409, "Xung đột dữ liệu", String.format("%s với %s '%s' đã tồn tại", resource, field, value));
    }
}
