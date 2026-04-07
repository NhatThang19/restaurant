package com.vn.restaurant.features.identity.service;

import com.vn.restaurant.dto.PageRes;
import com.vn.restaurant.exception.BusinessException;
import com.vn.restaurant.exception.DuplicateResourceException;
import com.vn.restaurant.exception.ResourceNotFoundException;
import com.vn.restaurant.features.auth.repository.RefreshTokenRepository;
import com.vn.restaurant.features.common.enums.RoleNameEnum;
import com.vn.restaurant.features.common.enums.UserStatusEnum;
import com.vn.restaurant.features.identity.dto.req.ChangeUserPasswordReq;
import com.vn.restaurant.features.identity.dto.req.CreateUserReq;
import com.vn.restaurant.features.identity.dto.req.UpdateUserReq;
import com.vn.restaurant.features.identity.dto.req.UpdateUserStatusReq;
import com.vn.restaurant.features.identity.dto.res.UserRes;
import com.vn.restaurant.features.identity.model.Role;
import com.vn.restaurant.features.identity.model.User;
import com.vn.restaurant.features.identity.repository.RoleRepository;
import com.vn.restaurant.features.identity.repository.UserRepository;
import com.vn.restaurant.features.identity.spec.UserSpecifications;
import java.time.Instant;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "username",
            "fullName",
            "status",
            "hireDate",
            "createdAt",
            "updatedAt");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public User getByUsernameOrThrow(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Nguoi dung", "username", username));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PageRes<UserRes> getUsers(
            String q,
            RoleNameEnum role,
            UserStatusEnum status,
            int page,
            int size,
            String sort) {

        Pageable pageable = buildPageable(page, size, sort);
        Page<User> users = userRepository.findAll(UserSpecifications.withFilters(q, role, status), pageable);

        return new PageRes<>(
                users.getContent().stream().map(this::toUserRes).toList(),
                users.getNumber() + 1,
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.hasNext());
    }

    @Override
    @Transactional(readOnly = true)
    public UserRes getUserById(Integer userId) {
        User user = getUserByIdOrThrow(userId);
        return toUserRes(user);
    }

    @Override
    @Transactional
    public UserRes createUser(CreateUserReq request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException("Nguoi dung", "username", request.username());
        }

        Role role = getRoleByNameOrThrow(request.role());

        User user = User.builder()
                .username(request.username().trim())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName().trim())
                .phone(trimToNull(request.phone()))
                .email(trimToNull(request.email()))
                .address(trimToNull(request.address()))
                .dateOfBirth(request.dateOfBirth())
                .gender(request.gender())
                .hireDate(request.hireDate())
                .citizenId(trimToNull(request.citizenId()))
                .role(role)
                .status(request.status() == null ? UserStatusEnum.ACTIVE : request.status())
                .build();

        return toUserRes(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserRes updateUser(Integer userId, UpdateUserReq request) {
        User user = getUserByIdOrThrow(userId);
        Role role = getRoleByNameOrThrow(request.role());

        user.setFullName(request.fullName().trim());
        user.setPhone(trimToNull(request.phone()));
        user.setEmail(trimToNull(request.email()));
        user.setAddress(trimToNull(request.address()));
        user.setDateOfBirth(request.dateOfBirth());
        user.setGender(request.gender());
        user.setHireDate(request.hireDate());
        user.setCitizenId(trimToNull(request.citizenId()));
        user.setRole(role);
        user.setStatus(request.status());

        return toUserRes(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserRes updateUserStatus(Integer userId, UpdateUserStatusReq request, String actorUsername) {
        User user = getUserByIdOrThrow(userId);

        if (UserStatusEnum.LOCKED.equals(request.status()) && user.getUsername().equals(actorUsername)) {
            throw new BusinessException(400, "Yeu cau khong hop le", "Ban khong the khoa tai khoan cua chinh minh");
        }

        user.setStatus(request.status());
        User savedUser = userRepository.save(user);

        if (UserStatusEnum.LOCKED.equals(request.status())) {
            refreshTokenRepository.revokeActiveByUserId(userId, Instant.now());
        }

        return toUserRes(savedUser);
    }

    @Override
    @Transactional
    public void changeUserPassword(Integer userId, ChangeUserPasswordReq request) {
        User user = getUserByIdOrThrow(userId);

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        refreshTokenRepository.revokeActiveByUserId(userId, Instant.now());
    }

    @Override
    @Transactional
    public void deleteUser(Integer userId, String actorUsername) {
        User user = getUserByIdOrThrow(userId);

        if (user.getUsername().equals(actorUsername)) {
            throw new BusinessException(400, "Yeu cau khong hop le", "Ban khong the xoa tai khoan cua chinh minh");
        }

        refreshTokenRepository.deleteByUser_Id(userId);
        userRepository.delete(user);
    }

    private User getUserByIdOrThrow(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Nguoi dung", "id", String.valueOf(userId)));
    }

    private Role getRoleByNameOrThrow(RoleNameEnum roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Quyen", "ten", roleName.name()));
    }

    private UserRes toUserRes(User user) {
        return new UserRes(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getPhone(),
                user.getEmail(),
                user.getAddress(),
                user.getDateOfBirth(),
                user.getGender(),
                user.getHireDate(),
                user.getCitizenId(),
                user.getRole().getName(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Pageable buildPageable(int page, int size, String sort) {
        if (page < 1) {
            throw new BusinessException(400, "Yeu cau khong hop le", "So trang phai lon hon hoac bang 1");
        }
        if (size < 1 || size > 100) {
            throw new BusinessException(400, "Yeu cau khong hop le", "Kich thuoc trang phai tu 1 den 100");
        }

        String sortExpression = (sort == null || sort.isBlank()) ? "id,desc" : sort.trim();
        String[] sortParts = sortExpression.split(",");

        if (sortParts.length > 2) {
            throw new BusinessException(400, "Yeu cau khong hop le", "Sap xep phai theo dinh dang field,direction");
        }

        String sortBy = sortParts[0].trim();
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new BusinessException(400, "Yeu cau khong hop le", "Truong sap xep khong duoc ho tro");
        }

        Sort.Direction direction = Sort.Direction.DESC;
        if (sortParts.length == 2) {
            direction = Sort.Direction.fromOptionalString(sortParts[1].trim().toUpperCase())
                    .orElseThrow(() -> new BusinessException(400, "Yeu cau khong hop le",
                            "Huong sap xep phai la asc hoac desc"));
        }

        return PageRequest.of(page - 1, size, Sort.by(direction, sortBy));
    }
}