package com.todate.backend.user.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "relationships",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "partner_1"),
        @UniqueConstraint(columnNames = "partner_2")
    }
)
@Getter
@Setter
public class Relationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "partner_1", nullable = false)

    private User partner1;

    @OneToOne
    @JoinColumn(name = "partner_2", nullable = false)
    private User partner2;
}
