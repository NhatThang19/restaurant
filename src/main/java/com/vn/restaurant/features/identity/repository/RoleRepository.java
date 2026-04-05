package com.vn.restaurant.features.identity.repository;

import com.vn.restaurant.features.common.enums.RoleNameEnum;
import com.vn.restaurant.features.identity.model.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Optional<Role> findByName(RoleNameEnum name);

    boolean existsByName(RoleNameEnum name);
}
