package com.todate.backend.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todate.backend.course.domain.UserCourse;
import com.todate.backend.user.domain.User;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long>{

    long countByUserId(User user);
}
