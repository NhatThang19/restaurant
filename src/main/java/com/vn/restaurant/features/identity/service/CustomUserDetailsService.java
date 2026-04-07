package com.vn.restaurant.features.identity.service;

import com.vn.restaurant.exception.ResourceNotFoundException;
import com.vn.restaurant.features.common.enums.UserStatusEnum;
import com.vn.restaurant.features.identity.model.User;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        try {
            user = userService.getByUsernameOrThrow(username);
        } catch (ResourceNotFoundException ex) {
            throw new UsernameNotFoundException("Khong tim thay nguoi dung voi ten dang nhap: " + username, ex);
        }

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().name()));

        boolean locked = UserStatusEnum.LOCKED.equals(user.getStatus());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountLocked(locked)
                .build();
    }
}
