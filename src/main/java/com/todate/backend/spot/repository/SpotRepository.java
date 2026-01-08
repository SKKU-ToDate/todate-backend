package com.todate.backend.spot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todate.backend.spot.domain.Spot;

@Repository
public interface SpotRepository extends JpaRepository<Spot, Long> {

}
