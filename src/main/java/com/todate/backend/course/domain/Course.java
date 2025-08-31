package com.todate.backend.course.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name="is_history", nullable = false)
    private boolean isHistory = false;

    protected Course(){
        
    }
    public Course(String name, LocalDate date, boolean isHistory){
        this.name = name;
        this.date = date;
        this.isHistory = isHistory;
    }
}
