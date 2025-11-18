package com.todate.backend.spot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SpotResponseDto {
    @JsonProperty("place_name")
    private String placeName;
    
    private Double x;
    
    private Double y;
    
    @JsonProperty("address_name")
    private String addressName;

    @JsonProperty("place_url")
    private String placeUrl;  

    private String phone;

    private String distance;
}
