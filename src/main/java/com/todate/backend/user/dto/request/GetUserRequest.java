package com.todate.backend.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetUserRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String userId;
}
