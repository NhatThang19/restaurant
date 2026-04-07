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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiRes<Void>> handleBusinessException(BusinessException ex, HttpServletRequest request) {
        log.warn("Loi nghiep vu tai {} {} | status={} | error={} | message={}",
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

        log.warn("Loi xac thuc tai {} {} | details={}", request.getMethod(), request.getRequestURI(), details);

        ApiRes<Void> body = ApiRes.badRequest("Du lieu yeu cau khong hop le", details);
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

        log.warn("Vi pham rang buoc tai {} {} | details={}", request.getMethod(), request.getRequestURI(), details);

        ApiRes<Void> body = ApiRes.badRequest("Du lieu yeu cau khong hop le", details);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiRes<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        String message = String.format("Gia tri khong hop le cho tham so '%s'", ex.getName());

        log.warn("Sai kieu du lieu tai {} {} | parameter={} | value={}",
                request.getMethod(), request.getRequestURI(), ex.getName(), ex.getValue());

        ApiRes<Void> body = ApiRes.badRequest(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiRes<Void>> handleNotReadable(HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        Throwable cause = ex.getMostSpecificCause();
        String detailMessage = cause == null ? ex.getMessage() : cause.getMessage();

        log.warn("Khong the doc noi dung yeu cau tai {} {} | message={}",
                request.getMethod(), request.getRequestURI(), detailMessage);

        ApiRes<Void> body = ApiRes.badRequest("Noi dung yeu cau khong hop le");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({ AuthenticationException.class, AuthenticationCredentialsNotFoundException.class })
    public ResponseEntity<ApiRes<Void>> handleAuthenticationException(Exception ex, HttpServletRequest request) {
        log.warn("Loi xac thuc danh tinh tai {} {} | message={}",
                request.getMethod(), request.getRequestURI(), ex.getMessage());

        ApiRes<Void> body = ApiRes.unauthorized("Ten dang nhap hoac mat khau khong hop le");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiRes<Void>> handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Loi he thong khong mong muon tai {} {}", request.getMethod(), request.getRequestURI(), ex);

        ApiRes<Void> body = ApiRes.error("Da xay ra loi khong mong muon. Vui long thu lai sau");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private String getValidationMessage(String defaultMessage) {
        return defaultMessage == null ? "Gia tri khong hop le" : defaultMessage;
    }

    private String extractFieldName(String propertyPath) {
        if (propertyPath == null || propertyPath.isBlank()) {
            return "khong ro";
        }
        int lastDotIndex = propertyPath.lastIndexOf('.');
        return lastDotIndex >= 0 ? propertyPath.substring(lastDotIndex + 1) : propertyPath;
    }
}