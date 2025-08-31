package com.todate.backend.course.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todate.backend.course.domain.Course;
import com.todate.backend.course.domain.UserCourse;
import com.todate.backend.course.dto.response.CourseResponse;
import com.todate.backend.course.dto.response.CourseResponse._CourseInfo;
import com.todate.backend.course.repository.CourseRepository;
import com.todate.backend.course.repository.UserCourseRepository;
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
    @Transactional
    public Long CreateCourse(String userName){
        
        User user = userRepository.findById(userName)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userName));

        Course newCourse = new Course("새로운 데이트 코스", LocalDate.now(), false);  
        
        courseRepository.save(newCourse);
        
        UserCourse userCourse = new UserCourse(user, newCourse);

        userCourseRepository.save(userCourse);

        return newCourse.getId();
    }

    // 데이트 코스 전체 조회
    @Transactional(readOnly = true)
    public CourseResponse findAllCourses(String userName){     
        User user = userRepository.findById(userName)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userName));

        List<UserCourse> userCourses = userCourseRepository.findByUser(user);
        List<_CourseInfo> courseInfos = userCourses.stream()
                .map(userCourse -> userCourse.getCourse())
                .map(course -> _CourseInfo.from(course))
                .collect(Collectors.toList());

        return new CourseResponse(courseInfos);
    }

    //데이트 코스 삭제
    @Transactional
    public void DeleteCourse(Long courseId, String userName){
        User user = userRepository.findById(userName)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userName));
        
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("코스를 찾을 수 없습니다."));
        
        UserCourse userCourse = userCourseRepository.findByUserAndCourse(user, course)
                .orElseThrow(() -> new IllegalArgumentException("해당 코스를 삭제할 권한이 없습니다."));
        
        userCourseRepository.delete(userCourse);
        courseRepository.delete(course);
    }

}
