package com.vn.restaurant.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource, String field, String value) {
        super(404, "Khong tim thay du lieu", String.format("%s voi %s '%s' khong ton tai", resource, field, value));
    }
}
