package com.vn.restaurant.features.identity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vn.restaurant.dto.PageRes;
import com.vn.restaurant.exception.BusinessException;
import com.vn.restaurant.exception.DuplicateResourceException;
import com.vn.restaurant.features.auth.repository.RefreshTokenRepository;
import com.vn.restaurant.features.common.enums.GenderEnum;
import com.vn.restaurant.features.common.enums.RoleNameEnum;
import com.vn.restaurant.features.common.enums.UserStatusEnum;
import com.vn.restaurant.features.identity.dto.req.ChangeUserPasswordReq;
import com.vn.restaurant.features.identity.dto.req.CreateUserReq;
import com.vn.restaurant.features.identity.dto.req.UpdateUserStatusReq;
import com.vn.restaurant.features.identity.dto.res.UserRes;
import com.vn.restaurant.features.identity.model.Role;
import com.vn.restaurant.features.identity.model.User;
import com.vn.restaurant.features.identity.repository.RoleRepository;
import com.vn.restaurant.features.identity.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private Role waiterRole;
    private User existingUser;

    @BeforeEach
    void setUp() {
        waiterRole = Role.builder()
                .id(2)
                .name(RoleNameEnum.WAITER)
                .build();

        existingUser = User.builder()
                .id(10)
                .username("waiter")
                .password("encoded")
                .fullName("Default Waiter")
                .gender(GenderEnum.FEMALE)
                .dateOfBirth(LocalDate.of(1998, 5, 20))
                .hireDate(LocalDate.of(2023, 3, 10))
                .role(waiterRole)
                .status(UserStatusEnum.ACTIVE)
                .build();
    }

    @Test
    void createUser_shouldThrowConflictWhenUsernameExists() {
        CreateUserReq req = new CreateUserReq(
                "waiter",
                "123456",
                "Waiter A",
                "0900000001",
                "w@restaurant.local",
                "HCMC",
                LocalDate.of(1999, 1, 1),
                GenderEnum.FEMALE,
                LocalDate.of(2024, 1, 1),
                "079090000099",
                RoleNameEnum.WAITER,
                UserStatusEnum.ACTIVE);

        when(userRepository.existsByUsername("waiter")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(req))
                .isInstanceOf(DuplicateResourceException.class);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void createUser_shouldEncodePasswordBeforeSave() {
        CreateUserReq req = new CreateUserReq(
                "waiter02",
                "123456",
                "Waiter B",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                RoleNameEnum.WAITER,
                null);

        when(userRepository.existsByUsername("waiter02")).thenReturn(false);
        when(roleRepository.findByName(RoleNameEnum.WAITER)).thenReturn(Optional.of(waiterRole));
        when(passwordEncoder.encode("123456")).thenReturn("encoded-123456");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(20);
            return user;
        });

        userService.createUser(req);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("encoded-123456");
        assertThat(captor.getValue().getStatus()).isEqualTo(UserStatusEnum.ACTIVE);
    }

    @Test
    void getUsers_shouldMapRequestPageOneToJpaPageZeroAndResponseOneBased() {
        Page<User> page = new PageImpl<>(List.of(existingUser), PageRequest.of(0, 10), 1);
        when(userRepository.findAll((Specification<User>) any(), any(Pageable.class))).thenReturn(page);

        PageRes<UserRes> response = userService.getUsers(null, null, null, 1, 10, "id,desc");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(userRepository).findAll((Specification<User>) any(), captor.capture());
        assertThat(captor.getValue().getPageNumber()).isEqualTo(0);
        assertThat(response.page()).isEqualTo(1);
    }

    @Test
    void getUsers_shouldMapRequestPageTwoToJpaPageOne() {
        when(userRepository.findAll((Specification<User>) any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(1, 10), 0));

        userService.getUsers(null, null, null, 2, 10, "id,desc");

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(userRepository).findAll((Specification<User>) any(), captor.capture());
        assertThat(captor.getValue().getPageNumber()).isEqualTo(1);
    }

    @Test
    void getUsers_shouldRejectWhenPageIsZero() {
        assertThatThrownBy(() -> userService.getUsers(null, null, null, 0, 10, "id,desc"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("greater than or equal to 1");
    }

    @Test
    void updateUserStatus_shouldRejectSelfLock() {
        UpdateUserStatusReq req = new UpdateUserStatusReq(UserStatusEnum.LOCKED);
        when(userRepository.findById(10)).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.updateUserStatus(10, req, "waiter"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("lock your own account");

        verify(refreshTokenRepository, never()).revokeActiveByUserId(anyInt(), any());
    }

    @Test
    void changeUserPassword_shouldEncodeAndRevokeActiveTokens() {
        ChangeUserPasswordReq req = new ChangeUserPasswordReq("newpass123");

        when(userRepository.findById(10)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newpass123")).thenReturn("encoded-newpass");

        userService.changeUserPassword(10, req);

        assertThat(existingUser.getPassword()).isEqualTo("encoded-newpass");
        verify(userRepository).save(existingUser);
        verify(refreshTokenRepository).revokeActiveByUserId(anyInt(), any());
    }

    @Test
    void deleteUser_shouldRejectSelfDelete() {
        when(userRepository.findById(10)).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> userService.deleteUser(10, "waiter"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("delete your own account");

        verify(refreshTokenRepository, never()).deleteByUser_Id(anyInt());
        verify(userRepository, never()).delete(any(User.class));
    }
}