package com.vn.restaurant.exception;

import com.vn.restaurant.dto.ApiRes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiRes<Void>> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.warn("Lỗi nghiệp vụ tại {} {} | mã trạng thái={} | lỗi={} | thông báo={}",
                request.getMethod(), request.getRequestURI(), ex.getStatusCode(), ex.getError(), ex.getMessage());

        ApiRes<Void> body = ApiRes.ofError(ex.getStatusCode(), ex.getMessage(), ex.getError());
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiRes<Void>> handleValidation(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        Map<String, String> details = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        org.springframework.validation.FieldError::getField,
                        error -> getValidationMessage(error.getDefaultMessage()),
                        (existing, ignored) -> existing,
                        LinkedHashMap::new));

        log.warn("Lỗi xác thực dữ liệu tại {} {} | chi tiết lỗi={}",
                request.getMethod(), request.getRequestURI(), details);

        ApiRes<Void> body = ApiRes.badRequest("Dữ liệu không hợp lệ", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiRes<Void>> handleConstraintViolation(ConstraintViolationException ex,
            HttpServletRequest request) {
        Map<String, String> details = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> extractFieldName(violation.getPropertyPath().toString()),
                        violation -> getValidationMessage(violation.getMessage()),
                        (existing, ignored) -> existing,
                        LinkedHashMap::new));

        log.warn("Lỗi vi phạm ràng buộc tại {} {} | chi tiết lỗi={}",
                request.getMethod(), request.getRequestURI(), details);

        ApiRes<Void> body = ApiRes.badRequest("Dữ liệu không hợp lệ", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({ AuthenticationException.class, AuthenticationCredentialsNotFoundException.class })
    public ResponseEntity<ApiRes<Void>> handleAuthenticationException(Exception ex, HttpServletRequest request) {
        log.warn("Lỗi xác thực tại {} {} | thông báo={}",
                request.getMethod(), request.getRequestURI(), ex.getMessage());

        ApiRes<Void> body = ApiRes.unauthorized("Sai tài khoản hoặc mật khẩu");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiRes<Void>> handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Lỗi hệ thống không xác định tại {} {}", request.getMethod(), request.getRequestURI(), ex);

        ApiRes<Void> body = ApiRes.error("Có lỗi xảy ra, vui lòng thử lại sau");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private String getValidationMessage(String defaultMessage) {
        return defaultMessage == null ? "Giá trị không hợp lệ" : defaultMessage;
    }

    private String extractFieldName(String propertyPath) {
        if (propertyPath == null || propertyPath.isBlank()) {
            return "unknown";
        }
        int lastDotIndex = propertyPath.lastIndexOf('.');
        return lastDotIndex >= 0 ? propertyPath.substring(lastDotIndex + 1) : propertyPath;
    }
}