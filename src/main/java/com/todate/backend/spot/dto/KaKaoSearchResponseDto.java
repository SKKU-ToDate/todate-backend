package com.todate.backend.spot.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KaKaoSearchResponseDto {

    // 검색 결과 리스트
    private List<Document> documents;

    @Getter
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Document {

        private String placeName;

        private String addressName;

        private String x;

        private String y;

        private String placeUrl;

        private String phone;
        // 중심좌표까지의 거리
        private String distance;
    }

}
