package com.vn.restaurant.features.auth.service;

import com.vn.restaurant.features.auth.dto.req.LoginReq;
import com.vn.restaurant.features.auth.dto.res.LoginRes;
import com.vn.restaurant.features.auth.dto.res.MeRes;

public interface AuthService {

    LoginRes login(LoginReq request, String userAgent, String ipAddress);

    LoginRes refresh(String rawRefreshToken, String userAgent, String ipAddress);

    void logout(String rawRefreshToken);

    MeRes getMe(String username);
}
