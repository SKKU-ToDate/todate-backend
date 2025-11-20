package com.todate.backend.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.Collections;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    private String username;  // Google email

    @Column(nullable = false)
    private String name;

    @Column(name = "google_id", nullable = false, unique = true)
    private String googleId;  // Google's 'sub' claim

    public static User createFromGoogle(String email, String googleId, String name) {
        return User.builder()
                .username(email)
                .googleId(googleId)
                .name(name)
                .build();
    }

    @Override
    public String getPassword() {
        return null;  // No password for OAuth users
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }
}
