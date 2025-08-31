package com.todate.backend.course.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.todate.backend.course.domain.Course;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CourseResponse {

    private final List<_CourseInfo> courses;


    //데이트 코스 조회
    @Getter
    public static class _CourseInfo{
        @JsonProperty("course_id")
        private Long courseId;

        @JsonProperty("course_name")
        private String courseName;

        private LocalDate date;

        @JsonProperty("is_history")
        private boolean isHistory;

        public static _CourseInfo from(Course course){
            _CourseInfo dto = new _CourseInfo();
            dto.courseId = course.getId();
            dto.courseName = course.getName();
            dto.date = course.getDate();
            dto.isHistory = course.isHistory();
            return dto;
        }

    }
}
