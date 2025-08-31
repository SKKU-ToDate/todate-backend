package com.todate.backend.course.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todate.backend.course.domain.Course;
import com.todate.backend.course.domain.UserCourse;
import com.todate.backend.user.domain.User;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long>{
    List<UserCourse> findByUser(User user);

    Optional<UserCourse> findByUserAndCourse(User user, Course course);

}
