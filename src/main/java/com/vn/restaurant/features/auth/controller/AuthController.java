package com.vn.restaurant.features.auth.controller;

import com.vn.restaurant.dto.ApiRes;
import com.vn.restaurant.exception.BusinessException;
import com.vn.restaurant.exception.InvalidTokenException;
import com.vn.restaurant.features.auth.dto.req.LoginReq;
import com.vn.restaurant.features.auth.dto.req.RefreshReq;
import com.vn.restaurant.features.auth.dto.res.LoginRes;
import com.vn.restaurant.features.auth.dto.res.MeRes;
import com.vn.restaurant.features.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "API xác thực và thông tin người dùng hiện tại")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Đăng nhập", description = "Xác thực username/password và trả về access token + refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đăng nhập thành công", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "success", value = """
                    {
                      "statusCode": 200,
                      "message": "Đăng nhập thành công",
                      "data": {
                        "accessToken": "eyJhbGciOiJIUzI1NiJ9.access...",
                        "refreshToken": "eyJhbGciOiJIUzI1NiJ9.refresh..."
                      },
                      "error": null,
                      "details": null
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Dữ liệu đầu vào không hợp lệ", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "validation_error", value = """
                    {
                      "statusCode": 400,
                      "message": "Dữ liệu không hợp lệ",
                      "data": null,
                      "error": "Yêu cầu không hợp lệ",
                      "details": {
                        "username": "Tên đăng nhập không được để trống",
                        "password": "Mật khẩu không được để trống"
                      }
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Sai thông tin đăng nhập", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "unauthorized", value = """
                    {
                      "statusCode": 401,
                      "message": "Sai tài khoản hoặc mật khẩu",
                      "data": null,
                      "error": "Chưa xác thực",
                      "details": null
                    }
                    """))),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "server_error", value = """
                    {
                      "statusCode": 500,
                      "message": "Có lỗi xảy ra, vui lòng thử lại sau",
                      "data": null,
                      "error": "Lỗi hệ thống",
                      "details": null
                    }
                    """)))
    })
    @PostMapping("/login")
    public ResponseEntity<ApiRes<LoginRes>> login(
            @Valid @RequestBody LoginReq request,
            HttpServletRequest httpRequest) {

        String userAgent = httpRequest.getHeader("User-Agent");
        String ipAddress = extractClientIp(httpRequest);

        LoginRes loginRes = authService.login(request, userAgent, ipAddress);
        return ResponseEntity.ok(ApiRes.success("Đăng nhập thành công", loginRes));
    }

    @Operation(summary = "Làm mới token", description = "Dùng refresh token để cấp access token mới")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Làm mới token thành công", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "success", value = """
                    {
                      "statusCode": 200,
                      "message": "Token đã được làm mới",
                      "data": {
                        "accessToken": "eyJhbGciOiJIUzI1NiJ9.access.new...",
                        "refreshToken": "eyJhbGciOiJIUzI1NiJ9.refresh.new..."
                      },
                      "error": null,
                      "details": null
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Thiếu refresh token", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "missing_refresh_token", value = """
                    {
                      "statusCode": 400,
                      "message": "Refresh token không được cung cấp",
                      "data": null,
                      "error": "Yêu cầu không hợp lệ",
                      "details": null
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Refresh token không hợp lệ/hết hạn/sai loại", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = {
                    @ExampleObject(name = "invalid_refresh_token", value = """
                            {
                              "statusCode": 401,
                              "message": "Refresh token không hợp lệ",
                              "data": null,
                              "error": "Token không hợp lệ",
                              "details": null
                            }
                            """),
                    @ExampleObject(name = "expired_refresh_token", value = """
                            {
                              "statusCode": 401,
                              "message": "Refresh token đã hết hạn",
                              "data": null,
                              "error": "Token không hợp lệ",
                              "details": null
                            }
                            """),
                    @ExampleObject(name = "wrong_token_type", value = """
                            {
                              "statusCode": 401,
                              "message": "Token không phải là refresh token",
                              "data": null,
                              "error": "Token không hợp lệ",
                              "details": null
                            }
                            """)
            })),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "server_error", value = """
                    {
                      "statusCode": 500,
                      "message": "Có lỗi xảy ra, vui lòng thử lại sau",
                      "data": null,
                      "error": "Lỗi hệ thống",
                      "details": null
                    }
                    """)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiRes<LoginRes>> refresh(
            @RequestBody(required = false) RefreshReq body,
            HttpServletRequest httpRequest) {

        String rawRefreshToken = extractRefreshToken(body);
        if (rawRefreshToken == null) {
            throw new BusinessException(400, "Yêu cầu không hợp lệ", "Refresh token không được cung cấp");
        }

        String userAgent = httpRequest.getHeader("User-Agent");
        String ipAddress = extractClientIp(httpRequest);

        LoginRes loginRes = authService.refresh(rawRefreshToken, userAgent, ipAddress);
        return ResponseEntity.ok(ApiRes.success("Token đã được làm mới", loginRes));
    }

    @Operation(summary = "Đăng xuất", description = "Thu hồi refresh token hiện tại")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Đăng xuất thành công", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "success", value = """
                    {
                      "statusCode": 200,
                      "message": "Đăng xuất thành công",
                      "data": null,
                      "error": null,
                      "details": null
                    }
                    """))),
            @ApiResponse(responseCode = "400", description = "Thiếu refresh token", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "missing_refresh_token", value = """
                    {
                      "statusCode": 400,
                      "message": "Refresh token không được cung cấp",
                      "data": null,
                      "error": "Yêu cầu không hợp lệ",
                      "details": null
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Refresh token không hợp lệ/hết hạn/sai loại", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = {
                    @ExampleObject(name = "invalid_refresh_token", value = """
                            {
                              "statusCode": 401,
                              "message": "Refresh token không hợp lệ",
                              "data": null,
                              "error": "Token không hợp lệ",
                              "details": null
                            }
                            """),
                    @ExampleObject(name = "expired_refresh_token", value = """
                            {
                              "statusCode": 401,
                              "message": "Refresh token đã hết hạn",
                              "data": null,
                              "error": "Token không hợp lệ",
                              "details": null
                            }
                            """),
                    @ExampleObject(name = "wrong_token_type", value = """
                            {
                              "statusCode": 401,
                              "message": "Token không phải là refresh token",
                              "data": null,
                              "error": "Token không hợp lệ",
                              "details": null
                            }
                            """)
            })),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "server_error", value = """
                    {
                      "statusCode": 500,
                      "message": "Có lỗi xảy ra, vui lòng thử lại sau",
                      "data": null,
                      "error": "Lỗi hệ thống",
                      "details": null
                    }
                    """)))
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiRes<Void>> logout(@RequestBody(required = false) RefreshReq body) {
        String rawRefreshToken = extractRefreshToken(body);
        if (rawRefreshToken == null) {
            throw new BusinessException(400, "Yêu cầu không hợp lệ", "Refresh token không được cung cấp");
        }

        authService.logout(rawRefreshToken);
        return ResponseEntity.ok(ApiRes.success("Đăng xuất thành công", null));
    }

    @Operation(summary = "Lấy thông tin tài khoản", description = "Trả về thông tin người dùng từ access token", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy thông tin thành công", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "success", value = """
                    {
                      "statusCode": 200,
                      "message": "Thành công",
                      "data": {
                        "id": 1,
                        "username": "manager",
                        "fullName": "Admin Manager",
                        "phone": "0900000001",
                        "email": "manager@restaurant.local",
                        "address": "1 Nguyen Hue, District 1, HCMC",
                        "dateOfBirth": "1990-01-15",
                        "gender": "MALE",
                        "hireDate": "2021-06-01",
                        "citizenId": "079090000001",
                        "role": "MANAGER",
                        "status": "ACTIVE"
                      },
                      "error": null,
                      "details": null
                    }
                    """))),
            @ApiResponse(responseCode = "401", description = "Token không hợp lệ hoặc thiếu token", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = {
                    @ExampleObject(name = "wrong_access_token_type", value = """
                            {
                              "statusCode": 401,
                              "message": "Token không phải access token",
                              "data": null,
                              "error": "Token không hợp lệ",
                              "details": null
                            }
                            """),
                    @ExampleObject(name = "missing_or_invalid_access_token", value = """
                            {
                              "statusCode": 401,
                              "message": "Sai tài khoản hoặc mật khẩu",
                              "data": null,
                              "error": "Chưa xác thực",
                              "details": null
                            }
                            """)
            })),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "server_error", value = """
                    {
                      "statusCode": 500,
                      "message": "Có lỗi xảy ra, vui lòng thử lại sau",
                      "data": null,
                      "error": "Lỗi hệ thống",
                      "details": null
                    }
                    """)))
    })
    @GetMapping("/me")
    public ResponseEntity<ApiRes<MeRes>> getMe(@AuthenticationPrincipal Jwt jwt) {
        String tokenType = jwt.getClaimAsString("type");
        if (!"access".equals(tokenType)) {
            throw new InvalidTokenException("Token không phải access token");
        }

        MeRes response = authService.getMe(jwt.getSubject());
        return ResponseEntity.ok(ApiRes.success(response));
    }

    private String extractRefreshToken(RefreshReq body) {
        if (body != null && body.refreshToken() != null && !body.refreshToken().isBlank()) {
            return body.refreshToken();
        }
        return null;
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
