package com.todate.backend.spot.domain;

import com.todate.backend.course.domain.Course;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Spot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course courseId;

    @Column(nullable = false)
    private Long seq;

    @Column(nullable = false)
    private String placeName;

    @Column(nullable = false)
    private String addressName;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private String placeUrl;

    @Column(nullable = false)
    private Long kakaoPlaceId;

    protected Spot() {
    }

    @lombok.Builder
    public Spot(Course courseId, Long seq, String placeName, String addressName, Double longitude, Double latitude,
            String placeUrl, Long kakaoPlaceId) {
        this.courseId = courseId;
        this.seq = seq;
        this.placeName = placeName;
        this.addressName = addressName;
        this.longitude = longitude;
        this.latitude = latitude;
        this.placeUrl = placeUrl;
        this.kakaoPlaceId = kakaoPlaceId;
    }

}
