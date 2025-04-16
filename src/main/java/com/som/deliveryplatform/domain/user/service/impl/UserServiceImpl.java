package com.som.deliveryplatform.domain.user.service.impl;

import com.som.deliveryplatform.domain.user.dto.response.MeResponse;
import com.som.deliveryplatform.domain.user.service.UserService;
import com.som.deliveryplatform.global.auth.principal.UserPrincipal;
import com.som.deliveryplatform.global.exception.UnauthorizedAccessException;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Override
    public MeResponse getCurrentUser(UserPrincipal userPrincipal) {
        if(userPrincipal == null || StringUtils.isBlank(userPrincipal.getEmail())) {
            throw new UnauthorizedAccessException("인증된 사용자만 접근 가능합니다.");
        }

        return new MeResponse(userPrincipal.getEmail(), userPrincipal.getRole());
    }
}
