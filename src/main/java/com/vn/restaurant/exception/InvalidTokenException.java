package com.vn.restaurant.exception;

public class InvalidTokenException extends BusinessException {

    public InvalidTokenException(String message) {
        super(401, "Token khong hop le", message);
    }
}
