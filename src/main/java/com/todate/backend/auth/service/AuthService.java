package com.todate.backend.auth.service;

import com.todate.backend.auth.dto.request.SignUpRequest;
import com.todate.backend.user.domain.User;
import com.todate.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signupUser(SignUpRequest signUpRequest){
        if (userRepository.existsUserByUserId(signUpRequest.getUserId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 회원입니다.");
        }

        String encoded = passwordEncoder.encode(signUpRequest.getPassword());

        User user = User.create(signUpRequest.getUserId(), encoded, signUpRequest.getName());
        userRepository.save(user);
    }
}
