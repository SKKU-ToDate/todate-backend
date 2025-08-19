package com.todate.backend.auth.controller;

import com.todate.backend.auth.dto.request.SignUpRequest;
import com.todate.backend.auth.service.AuthService;
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
    AuthService authService;

    @PostMapping("signup")
    public ResponseEntity<Void> signup(@RequestBody SignUpRequest signUpRequest){
        authService.signupUser(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
