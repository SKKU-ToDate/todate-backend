package com.todate.backend.spot.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SpotRequestDto {

    private String keyword;

    private String rect;
}
