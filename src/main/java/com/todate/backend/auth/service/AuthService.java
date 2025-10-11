package com.todate.backend.auth.service;

import com.todate.backend.auth.dto.request.SignInRequest;
import com.todate.backend.auth.dto.request.SignUpRequest;
import com.todate.backend.auth.dto.response.AccessTokenResponse;
import com.todate.backend.auth.jwt.JwtUtil;
import com.todate.backend.user.domain.User;
import com.todate.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public void signupUser(SignUpRequest signUpRequest) {
        if (userRepository.existsUserByUsername(signUpRequest.getUserId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 회원입니다.");
        }

        String encoded = passwordEncoder.encode(signUpRequest.getPassword());
        User user = User.create(signUpRequest.getUserId(), encoded, signUpRequest.getName());
        userRepository.save(user);
    }

    public AccessTokenResponse login(SignInRequest signInRequest) {
        Authentication authToken = UsernamePasswordAuthenticationToken.unauthenticated(
                signInRequest.getUserId(),
                signInRequest.getPassword()
        );

        try {
            Authentication authenticated = authenticationManager.authenticate(authToken);
            String username = authenticated.getName();
            String accessToken = jwtUtil.generateAccessToken(username);
            return AccessTokenResponse.of(accessToken, jwtUtil.accessTokenTtl());
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다.", e);
        }
    }
}
