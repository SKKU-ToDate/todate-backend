package com.todate.backend.course.controller;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import org.antlr.v4.runtime.misc.ObjectEqualityComparator;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todate.backend.course.domain.Course;
import com.todate.backend.course.dto.response.CourseResponse;
import com.todate.backend.course.service.CourseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    //데이트 코스 생성
    @PostMapping
    public ResponseEntity<Void> createCourse(
            @RequestBody String username,
            @AuthenticationPrincipal UserDetails userDetails){
        String tokenUsername = userDetails.getUsername();
        if(!Objects.equals(username, tokenUsername)){
            throw new IllegalStateException("요청된 사용자와 인증된 사용자가 일치하지 않습니다.")
        }
        Long courseId = courseService.CreateCourse(tokenUsername);
        URI location = URI.create("/api/v1/course/" + courseId);
        return ResponseEntity.created(location).build();
    }

    //데이트 코스 조회
    @GetMapping("/{username}")
    public ResponseEntity<CourseResponse> getCourses(
            @PathVariable("username") String pathUsername,
            @AuthenticationPrincipal UserDetails userDetails){
        
        String tokenUsername = userDetails.getUsername();
        if(!Objects.equals(pathUsername, tokenUsername)){
            throw new IllegalStateException("요청된 사용자와 인증된 사용자가 일치하지 않습니다.");
        }

        CourseResponse response = courseService.findAllCourses(tokenUsername);
        return ResponseEntity.ok(response);

    }

    //데이트 코스 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable("id") Long courseId,
            @AuthenticationPrincipal UserDetails userDetails){
        
        String tokenUserId = userDetails.getUsername();
        courseService.DeleteCourse(courseId, tokenUserId);
        return ResponseEntity.noContent().build();

    }
}
