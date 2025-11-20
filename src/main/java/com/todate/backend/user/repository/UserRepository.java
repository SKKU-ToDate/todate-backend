package com.todate.backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todate.backend.user.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String>{
    Boolean existsUserByUsername(String username);
    Optional<User> findByUsername(String username);

    // Google OAuth2 사용자 조회
    Optional<User> findByGoogleId(String googleId);
}
