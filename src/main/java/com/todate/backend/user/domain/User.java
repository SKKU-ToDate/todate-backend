package com.todate.backend.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    private String userId;

    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String name;

    public static User create(String userId, String encodedPassword, String name) {
        return User.builder().userId(userId).password(encodedPassword).name(name).build();
    }
}
