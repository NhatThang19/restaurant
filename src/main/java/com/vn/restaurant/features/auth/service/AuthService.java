package com.vn.restaurant.features.auth.service;

import com.vn.restaurant.features.auth.dto.req.LoginRequest;
import com.vn.restaurant.features.auth.dto.res.LoginResponse;
import com.vn.restaurant.features.auth.dto.res.MeResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request, String userAgent, String ipAddress);

    LoginResponse refresh(String rawRefreshToken, String userAgent, String ipAddress);

    void logout(String rawRefreshToken);

    MeResponse getMe(String username);
}
