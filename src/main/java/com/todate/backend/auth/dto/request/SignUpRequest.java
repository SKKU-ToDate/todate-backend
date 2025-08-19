package com.todate.backend.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SignUpRequest {
    @NotBlank(message = "아이디를 입력해주세요.")
    String userId;
    @NotBlank(message = "비밀번호를 입력해주세요.")
    String password;
    @NotBlank(message = "이름을 입력해주세요.")
    String name;
}
