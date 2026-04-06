package com.vn.restaurant.exception;

public class BusinessException extends RuntimeException {

    private final int statusCode;
    private final String error;

    public BusinessException(int statusCode, String error, String message) {
        super(message);
        this.statusCode = statusCode;
        this.error = error;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getError() {
        return error;
    }
}
