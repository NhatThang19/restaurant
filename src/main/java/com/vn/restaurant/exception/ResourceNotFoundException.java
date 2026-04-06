package com.vn.restaurant.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource, String field, String value) {
        super(404, "Không tìm thấy dữ liệu", String.format("%s với %s '%s' không tồn tại", resource, field, value));
    }
}
