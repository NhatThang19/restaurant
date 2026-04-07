package com.vn.restaurant.features.identity.repository;

import com.vn.restaurant.features.common.enums.RoleNameEnum;
import com.vn.restaurant.features.common.enums.UserStatusEnum;
import com.vn.restaurant.features.identity.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findByStatus(UserStatusEnum status);

    List<User> findByRole_Name(RoleNameEnum roleName);
}
