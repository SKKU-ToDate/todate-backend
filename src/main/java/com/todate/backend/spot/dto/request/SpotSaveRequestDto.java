package com.todate.backend.spot.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SpotSaveRequestDto {
    private String placeName;
    private String addressName;
    private Double longitude;
    private Double latitude;
    private String placeUrl;
    private Long kakaoPlaceId;
    private Long seq;
}
