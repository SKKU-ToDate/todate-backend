package com.todate.backend.spot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todate.backend.spot.domain.Spot;

public interface SpotRepository extends JpaRepository<Spot, Long>{

}
