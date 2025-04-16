package com.som.deliveryplatform.domain.user.controller;

import com.som.deliveryplatform.domain.user.dto.response.MeResponse;
import com.som.deliveryplatform.domain.user.service.UserService;
import com.som.deliveryplatform.global.auth.principal.UserPrincipal;
import com.som.deliveryplatform.global.common.ResponseCode;
import com.som.deliveryplatform.global.common.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ResponseDto<MeResponse>> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        MeResponse response = userService.getCurrentUser(principal);
        return ResponseEntity.ok(ResponseDto.of(ResponseCode.SUCCESS, response));
    }
}
