package com.vn.restaurant.exception;

import com.vn.restaurant.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.warn("Lỗi nghiệp vụ tại {} {} | mã trạng thái={} | lỗi={} | thông báo={}",
                request.getMethod(), request.getRequestURI(), ex.getStatusCode(), ex.getError(), ex.getMessage());

        ApiResponse<Void> body = ApiResponse.ofError(ex.getStatusCode(), ex.getMessage(), ex.getError());
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        List<ApiResponse.FieldError> details = ex.getBindingResult().getFieldErrors().stream()
                .map(this::mapFieldError)
                .toList();

        log.warn("Lỗi xác thực dữ liệu tại {} {} | chi tiết lỗi={}",
                request.getMethod(), request.getRequestURI(), details);

        ApiResponse<Void> body = ApiResponse.badRequest("Dữ liệu không hợp lệ", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex,
            HttpServletRequest request) {
        log.warn("Lỗi vi phạm ràng buộc tại {} {} | thông báo={}",
                request.getMethod(), request.getRequestURI(), ex.getMessage());

        ApiResponse<Void> body = ApiResponse.badRequest(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({ AuthenticationException.class, AuthenticationCredentialsNotFoundException.class })
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(Exception ex, HttpServletRequest request) {
        log.warn("Lỗi xác thực tại {} {} | thông báo={}",
                request.getMethod(), request.getRequestURI(), ex.getMessage());

        ApiResponse<Void> body = ApiResponse.unauthorized("Sai tài khoản hoặc mật khẩu");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Lỗi hệ thống không xác định tại {} {}", request.getMethod(), request.getRequestURI(), ex);

        ApiResponse<Void> body = ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private ApiResponse.FieldError mapFieldError(FieldError error) {
        String message = error.getDefaultMessage() == null ? "Giá trị không hợp lệ" : error.getDefaultMessage();
        return new ApiResponse.FieldError(error.getField(), message);
    }
}