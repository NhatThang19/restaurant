package com.vn.restaurant.features.identity.config;

import com.vn.restaurant.features.common.enums.RoleNameEnum;
import com.vn.restaurant.features.common.enums.UserStatusEnum;
import com.vn.restaurant.features.identity.model.Role;
import com.vn.restaurant.features.identity.model.User;
import com.vn.restaurant.features.identity.repository.RoleRepository;
import com.vn.restaurant.features.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class IdentitySeeder {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Bean
    CommandLineRunner seedDefaultAccounts() {
        return args -> {
            Role managerRole = getOrCreateRole(RoleNameEnum.MANAGER);
            Role waiterRole = getOrCreateRole(RoleNameEnum.WAITER);
            Role cashierRole = getOrCreateRole(RoleNameEnum.CASHIER);
            Role kitchenRole = getOrCreateRole(RoleNameEnum.KITCHEN);

            createUserIfMissing("manager", "Admin Manager", managerRole);
            createUserIfMissing("waiter", "Default Waiter", waiterRole);
            createUserIfMissing("cashier", "Default Cashier", cashierRole);
            createUserIfMissing("kitchen", "Default Kitchen", kitchenRole);
        };
    }

    private Role getOrCreateRole(RoleNameEnum roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(Role.builder().name(roleName).build()));
    }

    private void createUserIfMissing(String username, String fullName, Role role) {
        if (userRepository.existsByUsername(username)) {
            return;
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode("123456"))
                .fullName(fullName)
                .role(role)
                .status(UserStatusEnum.ACTIVE)
                .build();
        userRepository.save(user);
    }
}
