package com.vn.restaurant.features.identity.service;

import com.vn.restaurant.dto.PageRes;
import com.vn.restaurant.features.common.enums.RoleNameEnum;
import com.vn.restaurant.features.common.enums.UserStatusEnum;
import com.vn.restaurant.features.identity.dto.req.ChangeUserPasswordReq;
import com.vn.restaurant.features.identity.dto.req.CreateUserReq;
import com.vn.restaurant.features.identity.dto.req.UpdateUserReq;
import com.vn.restaurant.features.identity.dto.req.UpdateUserStatusReq;
import com.vn.restaurant.features.identity.dto.res.UserRes;
import com.vn.restaurant.features.identity.model.User;

public interface UserService {

    User getByUsernameOrThrow(String username);

    boolean existsByUsername(String username);

    User save(User user);

    PageRes<UserRes> getUsers(String q, RoleNameEnum role, UserStatusEnum status, int page, int size, String sort);

    UserRes getUserById(Integer userId);

    UserRes createUser(CreateUserReq request);

    UserRes updateUser(Integer userId, UpdateUserReq request);

    UserRes updateUserStatus(Integer userId, UpdateUserStatusReq request, String actorUsername);

    void changeUserPassword(Integer userId, ChangeUserPasswordReq request);

    void deleteUser(Integer userId, String actorUsername);
}
