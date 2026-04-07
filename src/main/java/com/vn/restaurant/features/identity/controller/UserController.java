package com.vn.restaurant.features.identity.controller;

import com.vn.restaurant.dto.ApiRes;
import com.vn.restaurant.features.common.enums.RoleNameEnum;
import com.vn.restaurant.features.common.enums.UserStatusEnum;
import com.vn.restaurant.features.identity.dto.req.ChangeUserPasswordReq;
import com.vn.restaurant.features.identity.dto.req.CreateUserReq;
import com.vn.restaurant.features.identity.dto.req.UpdateUserReq;
import com.vn.restaurant.features.identity.dto.req.UpdateUserStatusReq;
import com.vn.restaurant.dto.PageRes;
import com.vn.restaurant.features.identity.dto.res.UserRes;
import com.vn.restaurant.features.identity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@PreAuthorize("hasRole('MANAGER')")
@Tag(name = "User Management", description = "API quan ly nguoi dung danh cho MANAGER")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

        private final UserService userService;

        @Operation(summary = "Lay danh sach nguoi dung", description = "Tim kiem theo username/fullName, loc theo role/status, va phan trang")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lay danh sach nguoi dung thanh cong", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class), examples = @ExampleObject(name = "success", value = """
                                        {
                                          "statusCode": 200,
                                          "message": "Lay danh sach nguoi dung thanh cong",
                                          "data": {
                                            "content": [
                                              {
                                                "id": 1,
                                                "username": "manager",
                                                "fullName": "Admin Manager",
                                                "role": "MANAGER",
                                                "status": "ACTIVE"
                                              }
                                            ],
                                            "page": 1,
                                            "size": 10,
                                            "totalElements": 1,
                                            "totalPages": 1,
                                            "hasNext": false
                                          },
                                          "error": null,
                                          "details": null
                                        }
                                        """))),
                        @ApiResponse(responseCode = "400", description = "Tham so yeu cau khong hop le", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "401", description = "Chua xac thuc", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "403", description = "Khong co quyen truy cap", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "500", description = "Loi he thong", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class)))
        })
        @GetMapping
        public ResponseEntity<ApiRes<PageRes<UserRes>>> getUsers(
                        @RequestParam(required = false) String q,
                        @RequestParam(required = false) RoleNameEnum role,
                        @RequestParam(required = false) UserStatusEnum status,
                        @RequestParam(defaultValue = "1") @Min(value = 1, message = "page phai >= 1") int page,
                        @RequestParam(defaultValue = "10") @Min(value = 1, message = "size phai >= 1") @Max(value = 100, message = "size phai <= 100") int size,
                        @RequestParam(defaultValue = "id,desc") String sort) {

                PageRes<UserRes> response = userService.getUsers(q, role, status, page, size, sort);
                return ResponseEntity.ok(ApiRes.success("Lay danh sach nguoi dung thanh cong", response));
        }

        @Operation(summary = "Lay chi tiet nguoi dung", description = "Tra ve chi tiet nguoi dung theo ID (khong bao gom mat khau)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Lay chi tiet nguoi dung thanh cong", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "404", description = "Khong tim thay nguoi dung", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "401", description = "Chua xac thuc", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "403", description = "Khong co quyen truy cap", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "500", description = "Loi he thong", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class)))
        })
        @GetMapping("/{id}")
        public ResponseEntity<ApiRes<UserRes>> getUserById(@PathVariable("id") Integer id) {
                UserRes response = userService.getUserById(id);
                return ResponseEntity.ok(ApiRes.success("Lay chi tiet nguoi dung thanh cong", response));
        }

        @Operation(summary = "Tao nguoi dung moi", description = "Tao tai khoan moi voi username duy nhat va mat khau da duoc ma hoa")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Tao nguoi dung thanh cong", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "400", description = "Du lieu yeu cau khong hop le", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "409", description = "Ten dang nhap da ton tai", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "401", description = "Chua xac thuc", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "403", description = "Khong co quyen truy cap", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class)))
        })
        @PostMapping
        public ResponseEntity<ApiRes<UserRes>> createUser(@Valid @RequestBody CreateUserReq request) {
                UserRes response = userService.createUser(request);
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(ApiRes.created("Tao nguoi dung thanh cong", response));
        }

        @Operation(summary = "Cap nhat nguoi dung", description = "Cap nhat thong tin ca nhan va role/status, khong the thay doi username")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cap nhat nguoi dung thanh cong", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "400", description = "Du lieu yeu cau khong hop le", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "404", description = "Khong tim thay nguoi dung", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "401", description = "Chua xac thuc", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "403", description = "Khong co quyen truy cap", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class)))
        })
        @PutMapping("/{id}")
        public ResponseEntity<ApiRes<UserRes>> updateUser(
                        @PathVariable("id") Integer id,
                        @Valid @RequestBody UpdateUserReq request) {

                UserRes response = userService.updateUser(id, request);
                return ResponseEntity.ok(ApiRes.success("Cap nhat nguoi dung thanh cong", response));
        }

        @Operation(summary = "Khoa hoac mo khoa tai khoan", description = "Cap nhat trang thai nguoi dung ACTIVE hoac LOCKED")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cap nhat trang thai thanh cong", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "400", description = "Du lieu yeu cau khong hop le", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "404", description = "Khong tim thay nguoi dung", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "401", description = "Chua xac thuc", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "403", description = "Khong co quyen truy cap", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class)))
        })
        @PatchMapping("/{id}/status")
        public ResponseEntity<ApiRes<UserRes>> updateUserStatus(
                        @PathVariable("id") Integer id,
                        @Valid @RequestBody UpdateUserStatusReq request,
                        @AuthenticationPrincipal Jwt jwt) {

                UserRes response = userService.updateUserStatus(id, request, jwt.getSubject());
                return ResponseEntity.ok(ApiRes.success("Cap nhat trang thai nguoi dung thanh cong", response));
        }

        @Operation(summary = "Thay doi mat khau tai khoan", description = "Manager thay doi mat khau cho tai khoan nguoi dung (mat khau moi duoc ma hoa)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Thay doi mat khau thanh cong", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "400", description = "Du lieu yeu cau khong hop le", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "404", description = "Khong tim thay nguoi dung", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "401", description = "Chua xac thuc", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "403", description = "Khong co quyen truy cap", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class)))
        })
        @PatchMapping("/{id}/password")
        public ResponseEntity<ApiRes<Void>> changeUserPassword(
                        @PathVariable("id") Integer id,
                        @Valid @RequestBody ChangeUserPasswordReq request) {

                userService.changeUserPassword(id, request);
                return ResponseEntity.ok(ApiRes.success("Thay doi mat khau thanh cong", null));
        }

        @Operation(summary = "Xoa tai khoan", description = "Xoa cung tai khoan nguoi dung")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Xoa nguoi dung thanh cong", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "400", description = "Yeu cau khong hop le", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "404", description = "Khong tim thay nguoi dung", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "401", description = "Chua xac thuc", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class))),
                        @ApiResponse(responseCode = "403", description = "Khong co quyen truy cap", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiRes.class)))
        })
        @DeleteMapping("/{id}")
        public ResponseEntity<ApiRes<Void>> deleteUser(
                        @PathVariable("id") Integer id,
                        @AuthenticationPrincipal Jwt jwt) {

                userService.deleteUser(id, jwt.getSubject());
                return ResponseEntity.ok(ApiRes.success("Xoa nguoi dung thanh cong", null));
        }
}