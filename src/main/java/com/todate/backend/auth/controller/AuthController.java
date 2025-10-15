package com.todate.backend.auth.controller;

import com.todate.backend.auth.dto.request.SignInRequest;
import com.todate.backend.auth.dto.request.SignUpRequest;
import com.todate.backend.auth.dto.response.AccessTokenResponse;
import com.todate.backend.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
        authService.signupUser(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> signIn(
        @Valid @RequestBody SignInRequest signInRequest) {
        AccessTokenResponse accessTokenResponse = authService.login(signInRequest);
        return ResponseEntity.ok(accessTokenResponse);
    }
}
