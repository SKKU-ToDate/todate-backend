package com.todate.backend.course.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todate.backend.course.dto.request.CourseCreateRequestDto;
import com.todate.backend.course.dto.response.CourseResponse;
import com.todate.backend.course.service.CourseService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // 데이트 코스 생성
    @PostMapping
    public ResponseEntity<Void> createCourse(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CourseCreateRequestDto request) {

        String tokenUsername = userDetails.getUsername();

        Long courseId = courseService.createCourse(tokenUsername, request);
        URI location = URI.create("course/" + courseId);
        return ResponseEntity.created(location).build();
    }

    // 데이트 코스 조회
    @GetMapping
    public ResponseEntity<CourseResponse> getCourses(
            @AuthenticationPrincipal UserDetails userDetails) {

        String tokenUsername = userDetails.getUsername();

        CourseResponse response = courseService.findAllCourses(tokenUsername);
        return ResponseEntity.ok(response);

    }

    // 데이트 코스 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable("id") Long courseId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String tokenUserId = userDetails.getUsername();
        courseService.DeleteCourse(courseId, tokenUserId);
        return ResponseEntity.noContent().build();

    }
}
