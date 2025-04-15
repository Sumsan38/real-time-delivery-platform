package com.som.deliveryplatform.global.auth.dto;

import com.som.deliveryplatform.domain.user.model.Role;

public record LoginSuccessResponse(String emile, Role role) {
}
