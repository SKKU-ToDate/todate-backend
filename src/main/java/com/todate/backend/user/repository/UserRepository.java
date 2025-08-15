package com.todate.backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todate.backend.user.domain.User;

public interface UserRepository extends JpaRepository<User, String>{

}
