package com.todate.backend.course;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.todate.backend.course.domain.Course;
import com.todate.backend.course.domain.UserCourse;
import com.todate.backend.course.repository.CourseRepository;
import com.todate.backend.course.repository.UserCourseRepository;
import com.todate.backend.user.domain.User;
import com.todate.backend.user.repository.UserRepository;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize; 
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc // MockMvc를 사용하기 위한 설정
class CourseIntegrationTest {

    @Autowired
    private MockMvc mockMvc; // HTTP 요청을 보내는 역할을 함

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserCourseRepository userCourseRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testUser", "password123", "테스트유저");
        userRepository.save(testUser);
    }

    @Test
    @DisplayName("코스 생성 통합 테스트")
    @WithMockUser(username = "testUser") // 'testUser'로 로그인한 상태를 시뮬레이션
    void createCourse_IntegrationTest() throws Exception {
        // === When (실행) ===
        // Postman에서 POST /api/v1/course 를 호출하는 것과 동일
        mockMvc.perform(post("/api/v1/course")
                        .contentType(MediaType.APPLICATION_JSON))
                // === Then (검증 1: HTTP 응답 검증) ===
                .andExpect(status().isCreated()) // HTTP 상태 코드가 201 Created 인지 확인
                .andExpect(header().exists("Location")); // 응답 헤더에 Location이 있는지 확인

        // === Then (검증 2: DB 상태 검증) ===
        // DB를 직접 조회하여 데이터가 잘 저장되었는지 확인
        List<UserCourse> userCourses = userCourseRepository.findByUser(testUser);
        assertThat(userCourses).hasSize(1);
    }

    @Test
    @DisplayName("코스 목록 조회 통합 테스트")
    @WithMockUser(username = "testUser")
    void findCourses_IntegrationTest() throws Exception {
        // === Given (준비) ===
        Course course1 = courseRepository.save(new Course("코스1", LocalDate.now(), false));
        userCourseRepository.save(new UserCourse(testUser, course1));

        // === When (실행) ===
        mockMvc.perform(get("/api/v1/course/user/{userId}", "testUser")
                        .contentType(MediaType.APPLICATION_JSON))
                // === Then (검증 1: HTTP 응답 검증) ===
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courses", hasSize(1)))
                .andExpect(jsonPath("$.courses[0].courseName").value("코스1"));
    }

    @Test
    @DisplayName("코스 삭제 통합 테스트")
    @WithMockUser(username = "testUser")
    void deleteCourse_IntegrationTest() throws Exception {
        // === Given (준비) ===
        Course courseToDelete = courseRepository.save(new Course("삭제될 코스", LocalDate.now(), false));
        userCourseRepository.save(new UserCourse(testUser, courseToDelete));
        Long courseId = courseToDelete.getId();

        // === When (실행) ===
        mockMvc.perform(delete("/api/v1/course/{id}", courseId))
                // === Then (검증 1: HTTP 응답 검증) ===
                .andExpect(status().isNoContent());

        // === Then (검증 2: DB 상태 검증) ===
        assertThat(courseRepository.findById(courseId)).isEmpty();
        assertThat(userCourseRepository.findByUser(testUser)).isEmpty();
    }
}