package com.som.deliveryplatform.domain.user.service;

import com.som.deliveryplatform.domain.user.dto.response.MeResponse;
import com.som.deliveryplatform.global.auth.principal.UserPrincipal;

public interface UserService {
    MeResponse getCurrentUser(UserPrincipal userPrincipal);
}
