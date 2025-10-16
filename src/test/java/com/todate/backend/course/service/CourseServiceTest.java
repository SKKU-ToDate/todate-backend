package com.todate.backend.course.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.todate.backend.course.domain.Course;
import com.todate.backend.course.domain.UserCourse;
import com.todate.backend.course.dto.response.CourseResponse;
import com.todate.backend.course.repository.CourseRepository;
import com.todate.backend.course.repository.UserCourseRepository;
import com.todate.backend.user.domain.User;
import com.todate.backend.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

@SpringBootTest
@Transactional
class CourseServiceTest {

    @Autowired
    private CourseService courseService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserCourseRepository userCourseRepository;

    private User testUser;

    // 각 테스트가 실행되기 전에, 테스트용 유저를 미리 DB에 저장해 둡니다.
    @BeforeEach
    void setUp() {
        testUser = new User("testUser", "password123", "테스트유저");
        userRepository.save(testUser);
    }

    @Test
    @DisplayName("성공: 코스 생성")
    void createCourseTest() {
        // === Given (준비) ===
        String userId = testUser.getUsername();

        // === When (실행) ===
        Long createdCourseId = courseService.CreateCourse(userId);

        // === Then (검증) ===
        // 1. Course가 DB에 저장되었는지 확인
        assertThat(courseRepository.findById(createdCourseId)).isPresent();
        // 2. UserCourse 연결 정보가 DB에 저장되었는지 확인
        assertThat(userCourseRepository.findByUser(testUser)).hasSize(1);
    }



    @Test
    @DisplayName("성공: 특정 유저의 코스 목록 조회")
    void findCoursesByUserTest() {
        // === Given (준비) ===
        // 테스트 유저에게 2개의 코스를 미리 생성해 줌
        Course course1 = courseRepository.save(new Course("코스1", LocalDate.now(), false));
        Course course2 = courseRepository.save(new Course("코스2", LocalDate.now(), false));
        userCourseRepository.save(new UserCourse(testUser, course1));
        userCourseRepository.save(new UserCourse(testUser, course2));

        // === When (실행) ===
        CourseResponse response = courseService.findAllCourses(testUser.getUsername());

        // === Then (검증) ===
        assertThat(response.getCourses()).hasSize(2); // 조회된 코스가 2개인지 확인
        assertThat(response.getCourses())
                .extracting("courseName") // DTO 리스트에서 courseName 필드만 추출
                .containsExactlyInAnyOrder("코스1", "코스2"); // 이름이 일치하는지 확인
    }

    @Test
    @DisplayName("성공: 자신의 코스 삭제")
    void deleteCourseTest() {
        // === Given (준비) ===
        // 삭제할 코스와 연결 정보를 미리 생성
        Course courseToDelete = courseRepository.save(new Course("삭제될 코스", LocalDate.now(), false));
        userCourseRepository.save(new UserCourse(testUser, courseToDelete));
        Long courseId = courseToDelete.getId();

        // === When (실행) ===
        courseService.DeleteCourse(courseId, testUser.getUsername());

        // === Then (검증) ===
        // 1. Course가 DB에서 삭제되었는지 확인 (Optional이 비어있어야 함)
        assertThat(courseRepository.findById(courseId)).isEmpty();
        // 2. UserCourse 연결 정보도 DB에서 삭제되었는지 확인 (리스트가 비어있어야 함)
        assertThat(userCourseRepository.findByUser(testUser)).isEmpty();
    }
    
    @Test
    @DisplayName("실패: 다른 사람의 코스 삭제 시 예외 발생")
    void deleteCourseByAnotherUserTest() {
        // === Given (준비) ===
        User anotherUser = userRepository.save(new User("anotherUser", "pw", "다른유저"));
        Course courseToDelete = courseRepository.save(new Course("남의 코스", LocalDate.now(), false));
        userCourseRepository.save(new UserCourse(anotherUser, courseToDelete)); // 코스 주인은 anotherUser
        Long courseId = courseToDelete.getId();
        
        // === When & Then (실행 및 검증) ===
        // testUser가 anotherUser의 코스를 삭제하려고 할 때, IllegalStateException이 발생하는지 확인
        assertThrows(IllegalArgumentException.class, () -> {
            courseService.DeleteCourse(courseId, testUser.getUsername());
        });
    }
}