package com.vn.restaurant.features.identity.service;

import com.vn.restaurant.features.identity.model.User;

public interface UserService {

    User getByUsernameOrThrow(String username);

    boolean existsByUsername(String username);

    User save(User user);
}
