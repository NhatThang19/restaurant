package com.vn.restaurant.features.common.audit;

import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("auditorProvider")
public class SpringSecurityAuditorAware implements AuditorAware<Integer> {

    @Override
    public Optional<Integer> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.of(0);
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Integer userId) {
            return Optional.of(userId);
        }

        if (principal instanceof String value) {
            try {
                return Optional.of(Integer.valueOf(value));
            } catch (NumberFormatException ignored) {
                return Optional.of(0);
            }
        }

        return Optional.of(0);
    }
}
