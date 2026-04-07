package com.vn.restaurant.features.identity.spec;

import com.vn.restaurant.features.common.enums.RoleNameEnum;
import com.vn.restaurant.features.common.enums.UserStatusEnum;
import com.vn.restaurant.features.identity.model.Role;
import com.vn.restaurant.features.identity.model.User;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public final class UserSpecifications {

    private UserSpecifications() {
    }

    public static Specification<User> withFilters(String q, RoleNameEnum role, UserStatusEnum status) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (q != null && !q.isBlank()) {
                String keyword = "%" + q.trim().toLowerCase() + "%";
                Predicate usernameLike = cb.like(cb.lower(root.get("username")), keyword);
                Predicate fullNameLike = cb.like(cb.lower(root.get("fullName")), keyword);
                predicates.add(cb.or(usernameLike, fullNameLike));
            }

            if (role != null) {
                Join<User, Role> roleJoin = root.join("role");
                predicates.add(cb.equal(roleJoin.get("name"), role));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
