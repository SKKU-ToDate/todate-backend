package com.todate.backend.course.dto.request;

import java.time.LocalDate;
import java.util.List;

import com.todate.backend.spot.dto.request.SpotSaveRequestDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CourseCreateRequestDto {
    private String name;
    private LocalDate date;
    private List<SpotSaveRequestDto> spots;
}
