package com.som.deliveryplatform.domain.user.dto.response;

import com.som.deliveryplatform.domain.user.model.Role;

public record MeResponse(String email, Role role) {
}
