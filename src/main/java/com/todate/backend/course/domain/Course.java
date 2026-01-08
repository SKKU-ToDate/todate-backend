package com.todate.backend.course.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import com.todate.backend.spot.domain.Spot;

@Entity
@Getter
@Setter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "is_history", nullable = false)
    private boolean isHistory = false;

    @OneToMany(mappedBy = "courseId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Spot> spots = new ArrayList<>();

    protected Course() {

    }

    public Course(String name, LocalDate date, boolean isHistory) {
        this.name = name;
        this.date = date;
        this.isHistory = isHistory;
    }

    public void addSpot(Spot spot) {
        this.spots.add(spot);
        spot.setCourseId(this);
    }

    public void update(String name, LocalDate date) {
        this.name = name;
        this.date = date;
    }
}
