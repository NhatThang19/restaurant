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
@Tag(name = "Authentication", description = "API xac thuc va thong tin nguoi dung hien tai")
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "Dang nhap", description = "Xac thuc username/password va tra ve access token + refresh token")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Dang nhap thanh cong", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "success", value = """
          {
            "statusCode": 200,
            "message": "Dang nhap thanh cong",
            "data": {
              "accessToken": "eyJhbGciOiJIUzI1NiJ9.access...",
              "refreshToken": "eyJhbGciOiJIUzI1NiJ9.refresh..."
            },
            "error": null,
            "details": null
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Du lieu dau vao khong hop le", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "validation_error", value = """
          {
            "statusCode": 400,
            "message": "Du lieu khong hop le",
            "data": null,
            "error": "Yeu cau khong hop le",
            "details": {
              "truong 1": "message loi validate cho truong 1",
              "truong 2": "message loi validate cho truong 2",
              "...": "..."
            }
          }
          """))),
      @ApiResponse(responseCode = "401", description = "Sai thong tin dang nhap", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "unauthorized", value = """
          {
            "statusCode": 401,
            "message": "Sai tai khoan hoac mat khau",
            "data": null,
            "error": "Chua xac thuc",
            "details": null
          }
          """))),
      @ApiResponse(responseCode = "500", description = "Loi he thong", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "server_error", value = """
          {
            "statusCode": 500,
            "message": "Co loi xay ra, vui long thu lai sau",
            "data": null,
            "error": "Loi he thong",
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
    return ResponseEntity.ok(ApiRes.success("Dang nhap thanh cong", loginRes));
  }

  @Operation(summary = "Lam moi token", description = "Dung refresh token de cap access token moi")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Lam moi token thanh cong", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "success", value = """
          {
            "statusCode": 200,
            "message": "Token da duoc lam moi",
            "data": {
              "accessToken": "eyJhbGciOiJIUzI1NiJ9.access.new...",
              "refreshToken": "eyJhbGciOiJIUzI1NiJ9.refresh.new..."
            },
            "error": null,
            "details": null
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Thieu refresh token", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "missing_refresh_token", value = """
          {
            "statusCode": 400,
            "message": "Refresh token khong duoc cung cap",
            "data": null,
            "error": "Yeu cau khong hop le",
            "details": null
          }
          """))),
      @ApiResponse(responseCode = "401", description = "Refresh token khong hop le/het han/sai loai", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = {
          @ExampleObject(name = "invalid_refresh_token", value = """
              {
                "statusCode": 401,
                "message": "Refresh token khong hop le",
                "data": null,
                "error": "Token khong hop le",
                "details": null
              }
              """),
          @ExampleObject(name = "expired_refresh_token", value = """
              {
                "statusCode": 401,
                "message": "Refresh token da het han",
                "data": null,
                "error": "Token khong hop le",
                "details": null
              }
              """),
          @ExampleObject(name = "wrong_token_type", value = """
              {
                "statusCode": 401,
                "message": "Token khong phai la refresh token",
                "data": null,
                "error": "Token khong hop le",
                "details": null
              }
              """)
      })),
      @ApiResponse(responseCode = "500", description = "Loi he thong", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "server_error", value = """
          {
            "statusCode": 500,
            "message": "Co loi xay ra, vui long thu lai sau",
            "data": null,
            "error": "Loi he thong",
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
      throw new BusinessException(400, "Yeu cau khong hop le", "Refresh token khong duoc cung cap");
    }

    String userAgent = httpRequest.getHeader("User-Agent");
    String ipAddress = extractClientIp(httpRequest);

    LoginRes loginRes = authService.refresh(rawRefreshToken, userAgent, ipAddress);
    return ResponseEntity.ok(ApiRes.success("Token da duoc lam moi", loginRes));
  }

  @Operation(summary = "Dang xuat", description = "Thu hoi refresh token hien tai")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Dang xuat thanh cong", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "success", value = """
          {
            "statusCode": 200,
            "message": "Dang xuat thanh cong",
            "data": null,
            "error": null,
            "details": null
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Thieu refresh token", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "missing_refresh_token", value = """
          {
            "statusCode": 400,
            "message": "Refresh token khong duoc cung cap",
            "data": null,
            "error": "Yeu cau khong hop le",
            "details": null
          }
          """))),
      @ApiResponse(responseCode = "401", description = "Refresh token khong hop le/het han/sai loai", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = {
          @ExampleObject(name = "invalid_refresh_token", value = """
              {
                "statusCode": 401,
                "message": "Refresh token khong hop le",
                "data": null,
                "error": "Token khong hop le",
                "details": null
              }
              """),
          @ExampleObject(name = "expired_refresh_token", value = """
              {
                "statusCode": 401,
                "message": "Refresh token da het han",
                "data": null,
                "error": "Token khong hop le",
                "details": null
              }
              """),
          @ExampleObject(name = "wrong_token_type", value = """
              {
                "statusCode": 401,
                "message": "Token khong phai la refresh token",
                "data": null,
                "error": "Token khong hop le",
                "details": null
              }
              """)
      })),
      @ApiResponse(responseCode = "500", description = "Loi he thong", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "server_error", value = """
          {
            "statusCode": 500,
            "message": "Co loi xay ra, vui long thu lai sau",
            "data": null,
            "error": "Loi he thong",
            "details": null
          }
          """)))
  })
  @PostMapping("/logout")
  public ResponseEntity<ApiRes<Void>> logout(@RequestBody(required = false) RefreshReq body) {
    String rawRefreshToken = extractRefreshToken(body);
    if (rawRefreshToken == null) {
      throw new BusinessException(400, "Yeu cau khong hop le", "Refresh token khong duoc cung cap");
    }

    authService.logout(rawRefreshToken);
    return ResponseEntity.ok(ApiRes.success("Dang xuat thanh cong", null));
  }

  @Operation(summary = "Lay thong tin tai khoan", description = "Tra ve thong tin nguoi dung tu access token", security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Lay thong tin thanh cong", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "success", value = """
          {
            "statusCode": 200,
            "message": "Thanh cong",
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
      @ApiResponse(responseCode = "401", description = "Token khong hop le hoac thieu token", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = {
          @ExampleObject(name = "wrong_access_token_type", value = """
              {
                "statusCode": 401,
                "message": "Token khong phai access token",
                "data": null,
                "error": "Token khong hop le",
                "details": null
              }
              """),
          @ExampleObject(name = "missing_or_invalid_access_token", value = """
              {
                "statusCode": 401,
                "message": "Sai tai khoan hoac mat khau",
                "data": null,
                "error": "Chua xac thuc",
                "details": null
              }
              """)
      })),
      @ApiResponse(responseCode = "500", description = "Loi he thong", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "server_error", value = """
          {
            "statusCode": 500,
            "message": "Co loi xay ra, vui long thu lai sau",
            "data": null,
            "error": "Loi he thong",
            "details": null
          }
          """)))
  })
  @GetMapping("/me")
  public ResponseEntity<ApiRes<MeRes>> getMe(@AuthenticationPrincipal Jwt jwt) {
    String tokenType = jwt.getClaimAsString("type");
    if (!"access".equals(tokenType)) {
      throw new InvalidTokenException("Token khong phai access token");
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