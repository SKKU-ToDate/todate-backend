package com.todate.backend.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todate.backend.user.domain.Relationship;

public interface RelationshipRepository extends JpaRepository<Relationship, Long>{

}
