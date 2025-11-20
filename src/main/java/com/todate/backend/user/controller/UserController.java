package com.todate.backend.user.controller;

import com.todate.backend.user.dto.request.GetUserRequest;
import com.todate.backend.user.dto.request.PatchUserRequest;
import com.todate.backend.user.dto.response.UserResponse;
import com.todate.backend.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping()
    public ResponseEntity<UserResponse> getUser(@Valid @RequestBody GetUserRequest getUserRequest) {
        UserResponse userResponse = userService.getUser(getUserRequest.getUserId());
        return ResponseEntity.ok(userResponse);
    }

    @PatchMapping()
    public ResponseEntity<Void> patchUser(@Valid @RequestBody PatchUserRequest patchUserRequest) {
        userService.updateUser(patchUserRequest);
        return ResponseEntity.noContent().build();
    }
}
