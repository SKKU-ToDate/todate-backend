package com.todate.backend.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todate.backend.user.domain.Relationship;

public interface RelationshipRepository extends JpaRepository<Relationship, Long>{

    Optional<Relationship> findByPartner1UsernameOrPartner2Username(String partner1, String partner2);
}
