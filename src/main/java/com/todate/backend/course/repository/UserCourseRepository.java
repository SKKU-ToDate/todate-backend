package com.todate.backend.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todate.backend.course.domain.UserCourse;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long>{

}
