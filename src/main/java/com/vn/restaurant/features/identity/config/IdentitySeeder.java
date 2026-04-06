package com.vn.restaurant.features.identity.config;

import com.vn.restaurant.features.common.enums.GenderEnum;
import com.vn.restaurant.features.common.enums.RoleNameEnum;
import com.vn.restaurant.features.common.enums.UserStatusEnum;
import com.vn.restaurant.features.identity.model.Role;
import com.vn.restaurant.features.identity.model.User;
import com.vn.restaurant.features.identity.repository.RoleRepository;
import com.vn.restaurant.features.identity.service.UserService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class IdentitySeeder {

    private static final Logger log = LoggerFactory.getLogger(IdentitySeeder.class);

    private final RoleRepository roleRepository;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Bean
    CommandLineRunner seedDefaultAccounts() {
        return args -> {
            log.info("Bắt đầu khởi tạo dữ liệu định danh mặc định (roles/users)");
            Role managerRole = getOrCreateRole(RoleNameEnum.MANAGER);
            Role waiterRole = getOrCreateRole(RoleNameEnum.WAITER);
            Role cashierRole = getOrCreateRole(RoleNameEnum.CASHIER);
            Role kitchenRole = getOrCreateRole(RoleNameEnum.KITCHEN);

            createUserIfMissing(
                    "manager",
                    "Admin Manager",
                    "0900000001",
                    "manager@restaurant.local",
                    "1 Nguyen Hue, District 1, HCMC",
                    LocalDate.of(1990, 1, 15),
                    GenderEnum.MALE,
                    LocalDate.of(2021, 6, 1),
                    "079090000001",
                    managerRole);
            createUserIfMissing(
                    "waiter",
                    "Default Waiter",
                    "0900000002",
                    "waiter@restaurant.local",
                    "2 Le Loi, District 1, HCMC",
                    LocalDate.of(1998, 5, 20),
                    GenderEnum.FEMALE,
                    LocalDate.of(2023, 3, 10),
                    "079090000002",
                    waiterRole);
            createUserIfMissing(
                    "cashier",
                    "Default Cashier",
                    "0900000003",
                    "cashier@restaurant.local",
                    "3 Tran Hung Dao, District 5, HCMC",
                    LocalDate.of(1995, 9, 3),
                    GenderEnum.FEMALE,
                    LocalDate.of(2022, 8, 5),
                    "079090000003",
                    cashierRole);
            createUserIfMissing(
                    "kitchen",
                    "Default Kitchen",
                    "0900000004",
                    "kitchen@restaurant.local",
                    "4 Vo Van Kiet, District 6, HCMC",
                    LocalDate.of(1992, 12, 11),
                    GenderEnum.MALE,
                    LocalDate.of(2020, 11, 15),
                    "079090000004",
                    kitchenRole);
            log.info("Hoàn tất khởi tạo dữ liệu định danh mặc định");
        };
    }

    private Role getOrCreateRole(RoleNameEnum roleName) {
        return roleRepository.findByName(roleName)
                .map(existingRole -> {
                    log.info("Role '{}' đã tồn tại, bỏ qua tạo mới", roleName);
                    return existingRole;
                })
                .orElseGet(() -> {
                    log.info("Đang tạo role mới '{}'", roleName);
                    return roleRepository.save(Role.builder().name(roleName).build());
                });
    }

    private void createUserIfMissing(
            String username,
            String fullName,
            String phone,
            String email,
            String address,
            LocalDate dateOfBirth,
            GenderEnum gender,
            LocalDate hireDate,
            String citizenId,
            Role role) {
        if (userService.existsByUsername(username)) {
            log.info("Tài khoản '{}' đã tồn tại, bỏ qua tạo mới", username);
            return;
        }

        log.info("Đang tạo tài khoản mặc định '{}' với role '{}'", username, role.getName());
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode("123456"))
                .fullName(fullName)
                .phone(phone)
                .email(email)
                .address(address)
                .dateOfBirth(dateOfBirth)
                .gender(gender)
                .hireDate(hireDate)
                .citizenId(citizenId)
                .role(role)
                .status(UserStatusEnum.ACTIVE)
                .build();
        userService.save(user);
        log.info("Đã tạo tài khoản '{}' thành công", username);
    }
}