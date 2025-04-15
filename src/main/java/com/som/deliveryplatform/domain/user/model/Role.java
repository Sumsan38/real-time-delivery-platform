package com.som.deliveryplatform.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {

    GUEST("ROLE_GUEST"),
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),

    ;

    private final String key;

    public static Role valueOfKey(String roleKey) {
        return Role.valueOf(roleKey.replace("ROLE_", ""));
    }
}
