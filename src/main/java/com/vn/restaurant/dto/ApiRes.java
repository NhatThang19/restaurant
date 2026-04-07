package com.vn.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ApiRes", description = "Dinh dang phan hoi chung cho toan bo API")
public record ApiRes<T>(
        @Schema(description = "Ma trang thai HTTP", example = "200") int statusCode,

        @Schema(description = "Thong bao trang thai xu ly", example = "Thanh cong") String message,

        @Schema(description = "Du lieu tra ve (chi xuat hien khi thanh cong hoac khong co loi nghiem trong)") T data,

        @Schema(description = "Mo ta loai loi (chi xuat hien khi co loi)", example = "Yeu cau khong hop le") String error,

        @Schema(description = "Danh sach chi tiet cac loi, thuong dung de validate cac truong du lieu dau vao", example = "{\"username\": \"Khong duoc de trong\"}") Map<String, String> details) {

    // Success responses
    public static <T> ApiRes<T> success(T data) {
        return new ApiRes<>(200, "Thanh cong", data, null, null);
    }

    public static <T> ApiRes<T> success(String message, T data) {
        return new ApiRes<>(200, message, data, null, null);
    }

    public static <T> ApiRes<T> created(T data) {
        return new ApiRes<>(201, "Tao moi thanh cong", data, null, null);
    }

    public static <T> ApiRes<T> created(String message, T data) {
        return new ApiRes<>(201, message, data, null, null);
    }

    // Error responses
    public static <T> ApiRes<T> badRequest(String message) {
        return new ApiRes<>(400, message, null, "Yeu cau khong hop le", null);
    }

    public static <T> ApiRes<T> badRequest(String message, Map<String, String> details) {
        return new ApiRes<>(400, message, null, "Yeu cau khong hop le", details);
    }

    public static <T> ApiRes<T> unauthorized(String message) {
        return new ApiRes<>(401, message, null, "Chua xac thuc", null);
    }

    public static <T> ApiRes<T> forbidden(String message) {
        return new ApiRes<>(403, message, null, "Khong co quyen truy cap", null);
    }

    public static <T> ApiRes<T> notFound(String message) {
        return new ApiRes<>(404, message, null, "Khong tim thay du lieu", null);
    }

    public static <T> ApiRes<T> conflict(String message) {
        return new ApiRes<>(409, message, null, "Xung dot du lieu", null);
    }

    public static <T> ApiRes<T> error(String message) {
        return new ApiRes<>(500, message, null, "Loi he thong", null);
    }

    public static <T> ApiRes<T> of(int statusCode, String message, T data) {
        return new ApiRes<>(statusCode, message, data, null, null);
    }

    public static <T> ApiRes<T> ofError(int statusCode, String message, String error) {
        return new ApiRes<>(statusCode, message, null, error, null);
    }
}