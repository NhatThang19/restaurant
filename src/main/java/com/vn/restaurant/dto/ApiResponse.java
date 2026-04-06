package com.vn.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        int statusCode,
        String message,
        T data,
        String error,
        List<FieldError> details) {

    public record FieldError(String field, String message) {
    }

    // Success responses
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Thành công", data, null, null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data, null, null);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, "Tạo mới thành công", data, null, null);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(201, message, data, null, null);
    }

    // Error responses
    public static <T> ApiResponse<T> badRequest(String message) {
        return new ApiResponse<>(400, message, null, "Yêu cầu không hợp lệ", null);
    }

    public static <T> ApiResponse<T> badRequest(String message, List<FieldError> details) {
        return new ApiResponse<>(400, message, null, "Yêu cầu không hợp lệ", details);
    }

    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(401, message, null, "Chưa xác thực", null);
    }

    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<>(403, message, null, "Không có quyền truy cập", null);
    }

    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(404, message, null, "Không tìm thấy dữ liệu", null);
    }

    public static <T> ApiResponse<T> conflict(String message) {
        return new ApiResponse<>(409, message, null, "Xung đột dữ liệu", null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null, "Lỗi hệ thống", null);
    }

    public static <T> ApiResponse<T> of(int statusCode, String message, T data) {
        return new ApiResponse<>(statusCode, message, data, null, null);
    }

    public static <T> ApiResponse<T> ofError(int statusCode, String message, String error) {
        return new ApiResponse<>(statusCode, message, null, error, null);
    }
}
