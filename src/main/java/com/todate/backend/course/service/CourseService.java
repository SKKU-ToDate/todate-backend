package com.todate.backend.course.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todate.backend.course.domain.Course;
import com.todate.backend.course.domain.UserCourse;
import com.todate.backend.course.dto.request.CourseCreateRequestDto;
import com.todate.backend.course.dto.response.CourseResponse;
import com.todate.backend.course.dto.response.CourseResponse._CourseInfo;
import com.todate.backend.course.repository.CourseRepository;
import com.todate.backend.course.repository.UserCourseRepository;
import com.todate.backend.spot.domain.Spot;
import com.todate.backend.spot.dto.request.SpotSaveRequestDto;
import com.todate.backend.user.domain.User;
import com.todate.backend.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {

        private final UserRepository userRepository;
        private final CourseRepository courseRepository;
        private final UserCourseRepository userCourseRepository;

        // 데이트 코스 생성
        // 데이트 코스 생성 (Batch)
        @Transactional
        public Long createCourse(String userName, CourseCreateRequestDto request) {

                User user = userRepository.findById(userName)
                                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userName));

                // 1. 코스 생성
                Course newCourse = new Course(request.getName(), request.getDate(), false);

                // 2. 장소 리스트 생성 및 연결
                if (request.getSpots() != null) {
                        for (SpotSaveRequestDto spotDto : request.getSpots()) {
                                Spot spot = Spot.builder()
                                                .placeName(spotDto.getPlaceName())
                                                .addressName(spotDto.getAddressName())
                                                .longitude(spotDto.getLongitude())
                                                .latitude(spotDto.getLatitude())
                                                .placeUrl(spotDto.getPlaceUrl())
                                                .kakaoPlaceId(spotDto.getKakaoPlaceId())
                                                .seq(spotDto.getSeq())
                                                .courseId(newCourse) // 연결
                                                .build();

                                newCourse.addSpot(spot);
                        }
                }

                // 3. 저장 (Cascade로 Spot들도 같이 저장됨)
                courseRepository.save(newCourse);

                // 4. UserCourse 연결
                UserCourse userCourse = new UserCourse(user, newCourse);
                userCourseRepository.save(userCourse);

                return newCourse.getId();
        }

        // 데이트 코스 수정 (Batch Replace)
        @Transactional
        public void updateCourse(Long courseId, String userName, CourseCreateRequestDto request) {
                User user = userRepository.findById(userName)
                                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userName));

                Course course = courseRepository.findById(courseId)
                                .orElseThrow(() -> new IllegalArgumentException("코스를 찾을 수 없습니다."));

                // 권한 확인 (UserCourse 테이블 조회)
                userCourseRepository.findByUserAndCourse(user, course)
                                .orElseThrow(() -> new IllegalArgumentException("해당 코스를 수정할 권한이 없습니다."));

                // 1. 코스 정보 업데이트
                course.update(request.getName(), request.getDate());

                // 2. 기존 장소 모두 제거 (OrphanRemoval로 의해 DB에서도 삭제됨)
                course.getSpots().clear();

                // 3. 새로운 장소 리스트 추가
                if (request.getSpots() != null) {
                        for (SpotSaveRequestDto spotDto : request.getSpots()) {
                                Spot spot = Spot.builder()
                                                .placeName(spotDto.getPlaceName())
                                                .addressName(spotDto.getAddressName())
                                                .longitude(spotDto.getLongitude())
                                                .latitude(spotDto.getLatitude())
                                                .placeUrl(spotDto.getPlaceUrl())
                                                .kakaoPlaceId(spotDto.getKakaoPlaceId())
                                                .seq(spotDto.getSeq())
                                                .courseId(course) // 연결
                                                .build();
                                course.addSpot(spot);
                        }
                }
                // 트랜잭션 종료 시 더티 체킹으로 자동 저장
        }

        // 데이트 코스 전체 조회
        @Transactional(readOnly = true)
        public CourseResponse findAllCourses(String userName) {
                User user = userRepository.findById(userName)
                                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userName));

                List<UserCourse> userCourses = userCourseRepository.findByUser(user);
                List<_CourseInfo> courseInfos = userCourses.stream()
                                .map(userCourse -> userCourse.getCourse())
                                .map(course -> _CourseInfo.from(course))
                                .collect(Collectors.toList());

                return new CourseResponse(courseInfos);
        }

        // 데이트 코스 삭제
        @Transactional
        public void DeleteCourse(Long courseId, String userName) {
                User user = userRepository.findById(userName)
                                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userName));

                Course course = courseRepository.findById(courseId)
                                .orElseThrow(() -> new IllegalArgumentException("코스를 찾을 수 없습니다."));

                UserCourse userCourse = userCourseRepository.findByUserAndCourse(user, course)
                                .orElseThrow(() -> new IllegalArgumentException("해당 코스를 삭제할 권한이 없습니다."));

                userCourseRepository.delete(userCourse);
                courseRepository.delete(course);
        }

}
