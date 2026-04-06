package com.vn.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiRes<T>(
        int statusCode,
        String message,
        T data,
        String error,
        Map<String, String> details) {
    // Success responses
    public static <T> ApiRes<T> success(T data) {
        return new ApiRes<>(200, "Thành công", data, null, null);
    }

    public static <T> ApiRes<T> success(String message, T data) {
        return new ApiRes<>(200, message, data, null, null);
    }

    public static <T> ApiRes<T> created(T data) {
        return new ApiRes<>(201, "Tạo mới thành công", data, null, null);
    }

    public static <T> ApiRes<T> created(String message, T data) {
        return new ApiRes<>(201, message, data, null, null);
    }

    // Error responses
    public static <T> ApiRes<T> badRequest(String message) {
        return new ApiRes<>(400, message, null, "Yêu cầu không hợp lệ", null);
    }

    public static <T> ApiRes<T> badRequest(String message, Map<String, String> details) {
        return new ApiRes<>(400, message, null, "Yêu cầu không hợp lệ", details);
    }

    public static <T> ApiRes<T> unauthorized(String message) {
        return new ApiRes<>(401, message, null, "Chưa xác thực", null);
    }

    public static <T> ApiRes<T> forbidden(String message) {
        return new ApiRes<>(403, message, null, "Không có quyền truy cập", null);
    }

    public static <T> ApiRes<T> notFound(String message) {
        return new ApiRes<>(404, message, null, "Không tìm thấy dữ liệu", null);
    }

    public static <T> ApiRes<T> conflict(String message) {
        return new ApiRes<>(409, message, null, "Xung đột dữ liệu", null);
    }

    public static <T> ApiRes<T> error(String message) {
        return new ApiRes<>(500, message, null, "Lỗi hệ thống", null);
    }

    public static <T> ApiRes<T> of(int statusCode, String message, T data) {
        return new ApiRes<>(statusCode, message, data, null, null);
    }

    public static <T> ApiRes<T> ofError(int statusCode, String message, String error) {
        return new ApiRes<>(statusCode, message, null, error, null);
    }
}
