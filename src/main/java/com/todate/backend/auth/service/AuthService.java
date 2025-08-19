package com.todate.backend.auth.service;

import com.todate.backend.auth.dto.request.SignUpRequest;
import com.todate.backend.user.domain.User;
import com.todate.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;

    public void signupUser(SignUpRequest signUpRequest){
        if (userRepository.existsUserByUserId(signUpRequest.getUserId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 가입된 회원입니다.");
        }

        User user = User.create(signUpRequest.getUserId(), signUpRequest.getPassword(), signUpRequest.getName());
        userRepository.save(user);
    }
}
