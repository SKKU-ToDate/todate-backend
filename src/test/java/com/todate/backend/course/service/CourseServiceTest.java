package com.todate.backend.course.service;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.todate.backend.course.domain.Course;
import com.todate.backend.course.domain.UserCourse;
import com.todate.backend.course.dto.response.CourseResponse;
import com.todate.backend.course.dto.request.CourseCreateRequestDto;
import com.todate.backend.course.repository.CourseRepository;
import com.todate.backend.course.repository.UserCourseRepository;
import com.todate.backend.spot.dto.request.SpotSaveRequestDto;
import com.todate.backend.spot.repository.SpotRepository;
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
    @Autowired
    private SpotRepository spotRepository;

    private User testUser;

    // 각 테스트가 실행되기 전에, 테스트용 유저를 미리 DB에 저장해 둡니다.
    @BeforeEach
    void setUp() {
        testUser = new User("testUser", "password123", "테스트유저");
        userRepository.save(testUser);
    }

    @Test
    @DisplayName("성공: 코스 및 장소 일괄 생성")
    void createCourseTest() {
        // === Given (준비) ===
        String userId = testUser.getUsername();

        // 요청 DTO 생성
        CourseCreateRequestDto request = new CourseCreateRequestDto();
        request.setName("나만의 데이트 코스");
        request.setDate(LocalDate.now());

        // 장소 추가
        SpotSaveRequestDto spot1 = new SpotSaveRequestDto();
        spot1.setPlaceName("장소1");
        spot1.setAddressName("주소1");
        spot1.setLongitude(127.0);
        spot1.setLatitude(37.0);
        spot1.setPlaceUrl("http://place.com/1");
        spot1.setSeq(1L);
        spot1.setKakaoPlaceId(12345L);

        SpotSaveRequestDto spot2 = new SpotSaveRequestDto();
        spot2.setPlaceName("장소2");
        spot2.setAddressName("주소2");
        spot2.setLongitude(127.1);
        spot2.setLatitude(37.1);
        spot2.setPlaceUrl("http://place.com/2");
        spot2.setSeq(2L);
        spot2.setKakaoPlaceId(67890L);

        request.setSpots(List.of(spot1, spot2));

        // === When (실행) ===
        Long createdCourseId = courseService.createCourse(userId, request);

        // === Then (검증) ===
        // 1. Course가 DB에 저장되었는지 확인
        Course savedCourse = courseRepository.findById(createdCourseId).orElseThrow();
        assertThat(savedCourse.getName()).isEqualTo("나만의 데이트 코스");

        // 2. UserCourse 연결 정보 확인
        assertThat(userCourseRepository.findByUser(testUser)).hasSize(1);

        // 3. Spot들이 저장되고 Course와 연결되었는지 확인
        assertThat(spotRepository.count()).isEqualTo(2);
        assertThat(savedCourse.getSpots()).hasSize(2);
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