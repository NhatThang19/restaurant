package com.vn.restaurant.exception;

public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String resource, String field, String value) {
        super(409, "Xung dot du lieu", String.format("%s voi %s '%s' da ton tai", resource, field, value));
    }
}
