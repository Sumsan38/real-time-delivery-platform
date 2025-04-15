package com.som.deliveryplatform.global.auth.principal;

import com.som.deliveryplatform.domain.user.model.Role;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class UserPrincipal implements OAuth2User {

    private final Long id;
    private final String email;
    private final Role role;
    private final Map<String, Object> attributes;

    @Builder
    public UserPrincipal(Long id, String email, Role role, Map<String, Object> attributes) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.getKey()));
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }
}
