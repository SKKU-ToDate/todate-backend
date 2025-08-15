package com.todate.backend.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todate.backend.course.domain.Course;


public interface CourseRepository extends JpaRepository<Course, Long>{

}
