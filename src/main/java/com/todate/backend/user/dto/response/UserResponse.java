package com.todate.backend.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String name;
    private String partner;
    private long numOfCourses;

    public static UserResponse of(String name, String partner, long numOfCourses) {
        return new UserResponse(name, partner, numOfCourses);
    }
}
